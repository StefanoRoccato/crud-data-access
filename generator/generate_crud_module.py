#!/usr/bin/env python3
import argparse
import csv
import io
import json
import os
import re
import subprocess
import sys
import tempfile
from pathlib import Path

import yaml
from jinja2 import Environment, FileSystemLoader

try:
    import oracledb
except Exception:
    oracledb = None

BASE_PACKAGE = 'it.svg.crud'
SUPPORTED_FUNCTION_CODES = ['SR', 'UR', 'IR', 'DR']
DEFAULT_TABLE_SOURCE_OWNER = 'VPO_CRUD'

PROCEDURE_SQL = """
select owner, object_name, procedure_name, object_type
from all_procedures
where (object_name = upper('{module_name}') or procedure_name = upper('{module_name}'))
  and owner = upper('{owner}')
order by owner, object_type, object_name, procedure_name
"""

ARGUMENTS_SQL = """
select argument_name, position, sequence, data_type, in_out, data_level, data_precision, data_scale, type_owner, type_name
from all_arguments
where owner = upper('{owner}')
  and object_name = upper('{object_name}')
order by sequence
"""

TABLE_SQL = """
select substr(text,8,length(text)-20) as tabella
from all_source
where owner = '{table_source_owner}'
  and name = upper('{module_name}')
  and text like 'TYPE%'
order by line
"""

TAB_COLUMNS_SQL = """
select column_name, nullable, data_type, data_precision, data_scale
from all_tab_columns
where owner = upper('{owner}')
  and table_name = upper('{table_name}')
order by column_id
"""


def derive_owner(module_name: str) -> str:
    return (module_name or '')[:3].upper() + '_CRUD'


def camel_case(name: str) -> str:
    parts = [p for p in re.split(r'[^A-Za-z0-9]+', (name or '').lower()) if p]
    return parts[0] + ''.join(p.title() for p in parts[1:]) if parts else 'arg'


def pascal_case(name: str) -> str:
    cc = camel_case(name)
    return cc[:1].upper() + cc[1:]


def to_java_type(oracle_type, precision, scale):
    dt = (oracle_type or '').upper()
    if dt in ('VARCHAR2', 'CHAR', 'NVARCHAR2', 'NCHAR', 'CLOB'):
        return 'String'
    if dt == 'DATE':
        return 'java.time.LocalDate'
    if dt.startswith('TIMESTAMP'):
        return 'java.time.LocalDateTime'
    if dt == 'NUMBER':
        if scale not in (None, '', '0', 0):
            return 'java.math.BigDecimal'
        try:
            return 'Integer' if int(precision) <= 9 else 'Long'
        except Exception:
            return 'Long'
    return 'String'


def is_numeric(oracle_type):
    return (oracle_type or '').upper() == 'NUMBER'


# Maps a resolved Java type (as returned by to_java_type) to the corresponding java.sql.Types constant.
_JAVA_TYPE_TO_SQL_TYPE = {
    'String':                   'java.sql.Types.VARCHAR',
    'Integer':                  'java.sql.Types.INTEGER',
    'Long':                     'java.sql.Types.BIGINT',
    'java.math.BigDecimal':     'java.sql.Types.NUMERIC',
    'java.time.LocalDate':      'java.sql.Types.DATE',
    'java.time.LocalDateTime':  'java.sql.Types.TIMESTAMP',
}


def to_sql_type(java_type: str) -> str:
    """Return the java.sql.Types constant for the given Java type string."""
    return _JAVA_TYPE_TO_SQL_TYPE.get(java_type, 'java.sql.Types.VARCHAR')


def to_os_path(path_value):
    path_obj = Path(path_value).resolve()
    if os.name != 'nt':
        return path_obj

    path_str = str(path_obj)
    if path_str.startswith('\\\\?\\'):
        return path_obj

    # Su Windows usiamo sempre il prefisso long-path per evitare WinError 206.
    if re.match(r'^[A-Za-z]:\\', path_str):
        return Path('\\\\?\\' + path_str)
    return path_obj


def resolve_java_home_for_sqlcl(preferred_java_home=None):
    java_exec = 'java.exe' if os.name == 'nt' else 'java'

    if preferred_java_home:
        preferred = Path(preferred_java_home)
        if (preferred / 'bin' / java_exec).exists():
            return str(preferred)
        raise RuntimeError(f'JAVA_HOME passato non valido: {preferred_java_home}')

    env_java = os.environ.get('JAVA_HOME')
    if env_java and (Path(env_java) / 'bin' / java_exec).exists():
        return env_java

    ext_root = Path.home() / '.vscode' / 'extensions'
    if ext_root.exists():
        candidates = sorted(ext_root.glob('oracle.sql-developer-*'), key=lambda p: p.name, reverse=True)
        for ext in candidates:
            bundled = ext / 'dbtools' / 'jdk'
            if (bundled / 'bin' / java_exec).exists():
                return str(bundled)

    return None


def run_sqlcl_query(sqlcl_path, db_user, db_password, db_tns, conn_name, sql_text, java_home=None):
    with tempfile.NamedTemporaryFile('w', suffix='.sql', delete=False, encoding='utf-8') as temp_sql:
        sql_path = Path(temp_sql.name)
        temp_sql.write('set sqlformat csv\n')
        temp_sql.write('set heading on\n')
        temp_sql.write('set feedback off\n')
        temp_sql.write('set pagesize 50000\n')
        normalized_sql = (sql_text or '').rstrip()
        if normalized_sql and not normalized_sql.endswith(';'):
            normalized_sql += '\n;'
        temp_sql.write(normalized_sql + '\nexit\n')

    # Prova prima la named connection SQLcl (se presente), poi la connessione esplicita user/password@tns.
    conn_candidates = []
    if conn_name:
        conn_candidates.append(conn_name)
    conn_candidates.append(f'{db_user}/{db_password}@{db_tns}')

    errors = []
    run_env = os.environ.copy()
    effective_java_home = resolve_java_home_for_sqlcl(java_home)
    if effective_java_home:
        run_env['JAVA_HOME'] = effective_java_home
        java_bin = str(Path(effective_java_home) / 'bin')
        run_env['PATH'] = java_bin + os.pathsep + run_env.get('PATH', '')
        print(f'>>> JAVA_HOME per SQLcl: {effective_java_home}', file=sys.stderr)
    else:
        print('>>> Warning: JAVA_HOME non trovato automaticamente. SQLcl richiede Java 17+.', file=sys.stderr)

    try:
        for conn in conn_candidates:
            print(f'>>> Esecuzione query Oracle via SQLcl su connessione: {conn}', file=sys.stderr)
            try:
                result = subprocess.run([sqlcl_path, conn, '@' + str(sql_path)], capture_output=True, text=True, env=run_env)
            except FileNotFoundError as exc:
                raise RuntimeError(
                    f'>>> SQLcl non trovato: {sqlcl_path}. Impostare --sqlcl-path con il path completo di sql/sql.exe'
                ) from exc

            if result.returncode == 0 and result.stdout.strip():
                return result.stdout

            errors.append(
                f'Connessione: {conn}\n'
                f'Exit code: {result.returncode}\n'
                f'STDOUT:\n{(result.stdout or "").strip()}\n'
                f'STDERR:\n{(result.stderr or "").strip()}'
            )
    finally:
        try:
            sql_path.unlink(missing_ok=True)
        except Exception:
            pass

    raise RuntimeError('>>> Query Oracle fallita via SQLcl su tutte le connessioni candidate.\n\n' + '\n\n---\n\n'.join(errors))


def csv_rows(text: str):
    # SQLcl prepende banner/righe informative non CSV.
    # Teniamo solo le righe che iniziano con '"' (SQLcl csvformat wrappa ogni valore in virgolette).
    # Non richiediamo la virgola perché le query a singola colonna non ne hanno.
    csv_lines = [line for line in text.splitlines() if line.lstrip().startswith('"')]
    if not csv_lines:
        return []

    rows = list(csv.reader(io.StringIO('\n'.join(csv_lines))))
    if not rows:
        return []

    header = [h.strip().strip('"') for h in rows[0]]
    out = []
    for r in rows[1:]:
        if not r or len(r) != len(header):
            continue
        cleaned = [v.strip().strip('"') if isinstance(v, str) else v for v in r]
        out.append(dict(zip(header, cleaned)))
    return out


def fetch_sqlcl(args):
    owner = args.db_owner or derive_owner(args.module)

    proc_rows = csv_rows(run_sqlcl_query(
        args.sqlcl_path, args.db_user, args.db_password, args.db_tns, args.conn_name,
        PROCEDURE_SQL.format(module_name=args.module, owner=owner), args.java_home
    ))
    if not proc_rows:
        raise SystemExit(f'Procedura/modulo {args.module} non trovato su ALL_PROCEDURES nello schema {owner}')
    proc = proc_rows[0]
    if proc.get('OBJECT_TYPE') == 'PACKAGE':
        raise SystemExit('Generatore banale: package non supportati')

    object_name = proc['OBJECT_NAME']
    callable_name = object_name

    table_rows = csv_rows(run_sqlcl_query(
        args.sqlcl_path, args.db_user, args.db_password, args.db_tns, args.conn_name,
        TABLE_SQL.format(table_source_owner=args.table_source_owner, module_name=args.module), args.java_home
    ))
    target_table = None if not table_rows else (table_rows[0].get('TABELLA') or table_rows[0].get('tabella'))

    arg_rows = csv_rows(run_sqlcl_query(
        args.sqlcl_path, args.db_user, args.db_password, args.db_tns, args.conn_name,
        ARGUMENTS_SQL.format(owner=owner, object_name=object_name), args.java_home
    ))

    column_map = {}
    if target_table:
        col_rows = csv_rows(run_sqlcl_query(
            args.sqlcl_path, args.db_user, args.db_password, args.db_tns, args.conn_name,
            TAB_COLUMNS_SQL.format(owner=owner, table_name=target_table), args.java_home
        ))
        for c in col_rows:
            column_map[(c.get('COLUMN_NAME') or '').upper()] = c

    args_out = []
    for a in arg_rows:
        if str(a.get('DATA_LEVEL', '')).strip() not in ('', '0'):
            continue
        oracle_arg = a.get('ARGUMENT_NAME') or f"ARG_{a.get('POSITION')}"
        col = column_map.get(oracle_arg.upper())
        nullable = True if col is None else (col.get('NULLABLE', 'Y').upper() == 'Y')
        data_type = (col.get('DATA_TYPE') if col else a.get('DATA_TYPE'))
        precision = (col.get('DATA_PRECISION') if col else a.get('DATA_PRECISION'))
        scale = (col.get('DATA_SCALE') if col else a.get('DATA_SCALE'))
        args_out.append({
            'oracle_argument_name': oracle_arg,
            'position': int(a.get('POSITION') or 0),
            'data_type': data_type,
            'parameter_mode': (a.get('IN_OUT') or 'IN').upper(),
            'data_precision': precision,
            'data_scale': scale,
            'nullable': nullable,
        })

    return {
        'owner': owner,
        'callable_name': callable_name,
        'object_name': object_name,
        'target_table': target_table,
        'arguments': args_out,
    }


def fetch_direct(args):
    if oracledb is None:
        raise SystemExit('Modulo Python oracledb non disponibile. Installare: pip install oracledb Jinja2 pyyaml')

    owner = args.db_owner or derive_owner(args.module)
    conn = oracledb.connect(user=args.db_user, password=args.db_password, dsn=args.db_tns)
    try:
        cur = conn.cursor()
        cur.execute(PROCEDURE_SQL.format(module_name=args.module, owner=owner))
        procs = cur.fetchall()
        if not procs:
            raise SystemExit(f'Procedura/modulo {args.module} non trovato su ALL_PROCEDURES nello schema {owner}')
        proc = procs[0]
        if proc[3] == 'PACKAGE':
            raise SystemExit('Generatore banale: package non supportati')
        object_name = proc[1]
        callable_name = object_name

        cur.execute(TABLE_SQL.format(table_source_owner=args.table_source_owner, module_name=args.module))
        rows = cur.fetchall()
        target_table = None if not rows else rows[0][0]

        cur.execute(ARGUMENTS_SQL.format(owner=owner, object_name=object_name))
        arg_rows = cur.fetchall()

        column_map = {}
        if target_table:
            cur.execute(TAB_COLUMNS_SQL.format(owner=owner, table_name=target_table))
            for c in cur.fetchall():
                column_map[(c[0] or '').upper()] = {
                    'COLUMN_NAME': c[0],
                    'NULLABLE': c[1],
                    'DATA_TYPE': c[2],
                    'DATA_PRECISION': c[3],
                    'DATA_SCALE': c[4],
                }

        args_out = []
        for argument_name, position, sequence, data_type, in_out, data_level, data_precision, data_scale, type_owner, type_name in arg_rows:
            if data_level not in (None, 0):
                continue
            oracle_arg = argument_name or f'ARG_{position}'
            col = column_map.get(oracle_arg.upper())
            nullable = True if col is None else (str(col['NULLABLE']).upper() == 'Y')
            eff_data_type = col['DATA_TYPE'] if col else data_type
            eff_precision = col['DATA_PRECISION'] if col else data_precision
            eff_scale = col['DATA_SCALE'] if col else data_scale
            args_out.append({
                'oracle_argument_name': oracle_arg,
                'position': int(position or 0),
                'data_type': eff_data_type,
                'parameter_mode': (in_out or 'IN').upper(),
                'data_precision': eff_precision,
                'data_scale': eff_scale,
                'nullable': nullable,
            })

        return {
            'owner': owner,
            'callable_name': callable_name,
            'object_name': object_name,
            'target_table': target_table,
            'arguments': args_out,
        }
    finally:
        conn.close()


def load_overrides(module_name, override_dir):
    p = Path(override_dir) / f'{module_name.upper()}.yaml'
    if not p.exists():
        return {}
    return yaml.safe_load(p.read_text(encoding='utf-8')) or {}


def build_model(args, metadata, overrides):
    fields = []
    field_overrides = overrides.get('fields', {})
    for a in metadata['arguments']:
        ov = field_overrides.get(a['oracle_argument_name'], {})
        numeric = is_numeric(a['data_type'])
        nullable = ov.get('nullable', a['nullable'])
        java_type = ov.get('java_type') or to_java_type(a['data_type'], a['data_precision'], a['data_scale'])
        java_name = ov.get('java_name') or camel_case(a['oracle_argument_name'])
        fields.append({
            'position': a['position'],
            'oracle_argument_name': a['oracle_argument_name'],
            'java_name': java_name,
            'java_name_first_upper': java_name[:1].upper() + java_name[1:],
            'java_type': java_type,
            'parameter_mode': a['parameter_mode'],
            'numeric': numeric,
            'nullable': bool(nullable),
            'normalization_policy': 'ZERO_IF_NOT_NULLABLE_NUMERIC' if numeric and not nullable else 'NULL_OTHERWISE'
        })

    # Derive class name base from target table when available; fall back to module name.
    # If target_table is None or 'UNKNOWN' (Oracle metadata not reachable), use the module name.
    # All class names can also be overridden individually via the YAML override file.
    raw_table = metadata.get('target_table')
    if raw_table and raw_table.upper() != 'UNKNOWN':
        # Strip common Oracle view suffixes (_S = SELECT view, _V = view) used in ALL_SOURCE TYPE declarations.
        # E.g. "INDIVIDUALE_S" → "INDIVIDUALE" so the class is named IndividualeRepository not IndividualeSRepository.
        name_for_class = re.sub(r'_[SVsv]$', '', raw_table.strip())
        class_name_base = pascal_case(name_for_class)
    else:
        class_name_base = pascal_case(args.module)

    record_class_name = overrides.get('record_class_name') or f"{class_name_base}Record"
    repository_class_name = overrides.get('repository_class_name') or f"{class_name_base}Repository"
    service_interface_name = overrides.get('service_interface_name') or f"{class_name_base}Service"
    service_impl_name = overrides.get('service_impl_name') or f"{class_name_base}ServiceImpl"
    constants_class_name = overrides.get('constants_class_name') or f"{class_name_base}Constants"

    binders = []
    readers = []
    for f in fields:
        getter = f"record.{f['java_name_first_upper'][0].lower() + f['java_name_first_upper'][1:]}()"
        if f['parameter_mode'] in ('IN', 'INOUT', 'IN/OUT'):
            default_expr = '0' if f['numeric'] and not f['nullable'] else 'null'
            binders.append(f"cs.setObject({f['position']}, {getter} == null ? {default_expr} : {getter});")
        if f['parameter_mode'] in ('OUT', 'INOUT', 'IN/OUT'):
            sql_type = to_sql_type(f['java_type'])
            binders.append(f"cs.registerOutParameter({f['position']}, {sql_type});")
            readers.append(f"// TODO leggere OUT param {f['oracle_argument_name']} posizione {f['position']}")

    model = {
        'base_package': BASE_PACKAGE,
        'module_name': args.module.upper(),
        'module_name_lower': args.module.lower(),
        'owner': metadata['owner'],
        'callable_name': overrides.get('callable_name') or metadata['callable_name'],
        'target_table': metadata['target_table'] or 'UNKNOWN',
        'record_class_name': record_class_name,
        'repository_class_name': repository_class_name,
        'service_interface_name': service_interface_name,
        'service_impl_name': service_impl_name,
        'constants_class_name': constants_class_name,
        'fields': fields,
        'sql_placeholders': ', '.join(['?'] * len(fields)),
        'binders': binders,
        'readers': readers,
        'supported_function_codes': SUPPORTED_FUNCTION_CODES,
    }
    return model


def render(args, model):
    env = Environment(loader=FileSystemLoader(args.templates_dir))
    out = to_os_path(args.out)
    java_base = out / 'src/main/java' / Path(*BASE_PACKAGE.split('.'))
    java_base.mkdir(parents=True, exist_ok=True)
    md_base = out / 'docs'
    md_base.mkdir(parents=True, exist_ok=True)

    dto_dir = java_base / 'model' / 'dto'
    repository_dir = java_base / 'repository'
    service_dir = java_base / 'service'
    service_impl_dir = service_dir / 'impl'
    constants_dir = java_base / 'constants'

    dto_dir.mkdir(parents=True, exist_ok=True)
    repository_dir.mkdir(parents=True, exist_ok=True)
    service_dir.mkdir(parents=True, exist_ok=True)
    service_impl_dir.mkdir(parents=True, exist_ok=True)
    constants_dir.mkdir(parents=True, exist_ok=True)

    # Test output directory mirrors the main source tree under src/test/java
    test_base = out / 'src/test/java' / Path(*BASE_PACKAGE.split('.'))
    test_service_impl_dir = test_base / 'service' / 'impl'
    test_service_impl_dir.mkdir(parents=True, exist_ok=True)

    (dto_dir / f"{model['record_class_name']}.java").write_text(env.get_template('module_record.java.j2').render(**model), encoding='utf-8')
    (repository_dir / f"{model['repository_class_name']}.java").write_text(env.get_template('module_repository.java.j2').render(**model), encoding='utf-8')
    (service_dir / f"{model['service_interface_name']}.java").write_text(env.get_template('module_service.java.j2').render(**model), encoding='utf-8')
    (service_impl_dir / f"{model['service_impl_name']}.java").write_text(env.get_template('module_service_impl.java.j2').render(**model), encoding='utf-8')
    (constants_dir / f"{model['constants_class_name']}.java").write_text(env.get_template('module_constants.java.j2').render(**model), encoding='utf-8')
    (test_service_impl_dir / f"{model['service_impl_name']}Test.java").write_text(env.get_template('module_service_test.java.j2').render(**model), encoding='utf-8')

    (md_base / f"{model['module_name']}_metadata.md").write_text(env.get_template('module_metadata.md.j2').render(**model), encoding='utf-8')
    rendered_json = json.dumps(model, indent=2, ensure_ascii=False)
    (out / f"{model['module_name']}_model.json").write_text(rendered_json, encoding='utf-8')


def main():
    p = argparse.ArgumentParser(description='Generate CRUD banal data-access backend (service+repository+dto) from Oracle metadata. Supports only SR, UR, IR, DR and only standalone procedures.')
    p.add_argument('--module', required=True)
    p.add_argument('--db-user', required=True)
    p.add_argument('--db-password', required=True)
    p.add_argument('--db-tns', required=True)
    p.add_argument('--conn-name', required=True)
    p.add_argument('--db-owner')
    p.add_argument('--table-source-owner', default=DEFAULT_TABLE_SOURCE_OWNER)
    p.add_argument('--target-table', default=None,
                   help='Forza il nome della tabella Oracle (es. INDIVIDUALE). '
                        'Sovrascrive la ricerca automatica su ALL_SOURCE. '
                        'Usare quando la query automatica non restituisce risultati.')
    p.add_argument('--mode', choices=['direct', 'sqlcl'], default='direct')
    p.add_argument('--sqlcl-path', default='sql')
    p.add_argument('--java-home', default=None)
    p.add_argument('--out', default='generated-modules')
    p.add_argument('--override-dir', default='generator/overrides')
    p.add_argument('--templates-dir', default='generator/templates')
    args = p.parse_args()

    overrides = load_overrides(args.module, args.override_dir)
    if args.mode == 'direct':
        try:
            metadata = fetch_direct(args)
        except Exception as exc:
            # Alcuni account Oracle legacy non sono supportati in thin mode (es. DPY-3015).
            # In questo caso ripieghiamo su SQLcl senza richiedere di cambiare il comando.
            if 'DPY-3015' in str(exc):
                print('>>> Warning: connessione diretta non supportata (DPY-3015), uso fallback SQLcl.', file=sys.stderr)
                metadata = fetch_sqlcl(args)
            else:
                raise
    else:
        metadata = fetch_sqlcl(args)

    # --target-table CLI override: sovrascrive il valore proveniente da Oracle.
    if args.target_table:
        metadata['target_table'] = args.target_table.upper()
        print(f'>>> target_table forzato via --target-table: {metadata["target_table"]}', file=sys.stderr)
    elif not metadata.get('target_table'):
        print(
            f'>>> Warning: target_table non trovato su ALL_SOURCE per il modulo {args.module}.\n'
            f'    I nomi delle classi verranno derivati dal nome modulo ({args.module}) come fallback.\n'
            f'    Per forzare il nome tabella usa: --target-table <NOME_TABELLA>',
            file=sys.stderr
        )

    model = build_model(args, metadata, overrides)
    render(args, model)
    print(f"Generated module {args.module.upper()} in {args.out}")


if __name__ == '__main__':
    main()
