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

INDEX_TABLE_SQL = """
select table_name as tabella
from all_indexes
where owner = upper('{owner}')
    and index_name like upper('{short_name}%')
    and rownum = 1
"""

PK_COLUMNS_SQL = """
select column_name
from all_cons_columns
where owner = upper('{owner}')
    and constraint_name like upper('{short_name}_PK%')
order by position
"""

TAB_COLUMNS_SQL = """
select column_name, nullable, data_type, data_precision, data_scale
from all_tab_columns
where owner = upper('{owner}')
  and table_name = upper('{table_name}')
  and column_name not in ('ID_RIGA', 'ID_TIMESTAMP_FINE_VAL', 'ID_DATA_INIZIO_VAL', 'ID_DATA_FINE_VAL', 'ID_SEQ_INS')
order by column_id
"""


def derive_owner(module_name: str) -> str:
    return (module_name or '')[:3].upper() + '_CRUD'


def derive_schema_owner(module_name: str) -> str:
    """Return business schema owner (e.g. VIAT42xx -> VIA)."""
    return (module_name or '')[:3].upper()


def derive_short_name(module_name: str) -> str:
    """Return short module name used for index/PK lookup (e.g. VIAT4200 -> VIAT42)."""
    upper = re.sub(r'[^A-Za-z0-9]', '', (module_name or '').upper())
    if upper.endswith('00') and len(upper) > 2:
        return upper[:-2]
    return upper


def strip_select_suffix(table_name: str) -> str:
    """Strip final _S suffix used by some SELECT views."""
    if not table_name:
        return table_name
    return re.sub(r'_[Ss]$', '', table_name.strip())


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


# Ordered list of IoParameters constructor fields, the expected Oracle argument name
# for the corresponding IO_ procedure parameter, and whether a Long→Integer cast is needed
# (IoParameters.returnCode and .flagAggiornaDb are Integer, but the Oracle type maps to Long).
_IO_PARAMS_CONSTRUCTOR = [
    # (ioParameters_field,  oracle_arg_name,              needs_int_cast)
    ('functionCode',         'IO_FUNCTION_CODE',           False),
    ('returnCode',           'IO_RETURN_CODE',             True),
    ('flagAggiornaDb',       'IO_FLAG_AGGIORNA_DB',        True),
    ('flag1',                'IO_FLAG_1',                  False),
    ('tipoStoricita',        'IO_TIPO_STORICITA',          False),
    ('flagTrovato',          'IO_FLAG_TROVATO',            False),
    ('idTimestampInizioVal', 'IO_ID_TIMESTAMP_INIZIO_VAL', False),
    ('idTimestampFineVal',   'IO_ID_TIMESTAMP_FINE_VAL',   False),
    ('idDataInizioVal',      'IO_ID_DATA_INIZIO_VAL',      False),
    ('idDataFineVal',        'IO_ID_DATA_FINE_VAL',        False),
    ('dataCont',             'IO_DATA_CONT',               False),
    ('idRiga',               'IO_ID_RIGA',                 False),
    ('idLock',               'IO_ID_LOCK',                 False),
    ('flUpdate',             'IO_FL_UPDATE',               False),
    ('concurrentTempUpdate', 'IO_CONCURRENT_TEMP_UPDATE',  False),
    ('timestampApp',         'IO_TIMESTAMP_APP',           False),
    ('sqlerrmc',             None,                         False),
    ('livLog',               None,                         False),
    ('sessionId',            None,                         False),
]


def _cs_read_lines(java_name: str, position: int, java_type: str) -> list:
    """Return the Java line(s) needed to read one OUT parameter from a CallableStatement."""
    if java_type == 'String':
        return [f'String {java_name} = cs.getString({position});']
    if java_type in ('Long', 'Integer', 'java.math.BigDecimal'):
        return [f'{java_type} {java_name} = cs.getObject({position}, {java_type}.class);']
    if java_type == 'java.time.LocalDate':
        raw = f'raw{java_name[0].upper()}{java_name[1:]}'
        return [
            f'java.sql.Date {raw} = cs.getDate({position});',
            f'java.time.LocalDate {java_name} = {raw} != null ? {raw}.toLocalDate() : null;',
        ]
    if java_type == 'java.time.LocalDateTime':
        raw = f'raw{java_name[0].upper()}{java_name[1:]}'
        return [
            f'java.sql.Timestamp {raw} = cs.getTimestamp({position});',
            f'java.time.LocalDateTime {java_name} = {raw} != null ? {raw}.toLocalDateTime() : null;',
        ]
    return [f'Object {java_name} = cs.getObject({position}); // TODO: unknown type {java_type}']


def _build_readers(fields: list, record_class_name: str, callable_name: str, target_table: str) -> list:
    """Build the list of Java lines for the extractor lambda body (reads OUT params + returns result)."""
    out_modes = ('OUT', 'INOUT', 'IN/OUT')
    out_fields = [f for f in fields if f['parameter_mode'] in out_modes]

    if not out_fields:
        return [
            f'return new CrudModuleResult<>(ioParameters, record, "{callable_name}", "{target_table}", System.currentTimeMillis() - start);',
        ]

    args_by_oracle = {f['oracle_argument_name'].upper(): f for f in fields}

    lines = ['try {']

    io_out = [f for f in out_fields if f['oracle_argument_name'].upper().startswith('IO_')]
    s_out  = [f for f in out_fields if not f['oracle_argument_name'].upper().startswith('IO_')]

    if io_out:
        lines.append('// Lettura parametri IO_ di output (INOUT)')
        for f in io_out:
            lines.extend(_cs_read_lines(f['java_name'], f['position'], f['java_type']))
        lines.append('')

    if s_out:
        lines.append('// Lettura parametri S_ di output (OUT/INOUT)')
        for f in s_out:
            lines.extend(_cs_read_lines(f['java_name'], f['position'], f['java_type']))
        lines.append('')

    # outRecord construction
    lines.append(f'{record_class_name} outRecord = new {record_class_name}(')
    for i, f in enumerate(fields):
        comma = '' if i == len(fields) - 1 else ','
        pos_comment = f'// {f["position"]}'
        if f['parameter_mode'] in out_modes:
            lines.append(f'    {f["java_name"]}{comma}  {pos_comment}')
        else:
            lines.append(f'    record.{f["java_name"]}(){comma}  {pos_comment} - IN only')
    lines.append(');')
    lines.append('')

    # updatedIoParams construction
    lines.append('IoParameters updatedIoParams = new IoParameters(')
    last_idx = len(_IO_PARAMS_CONSTRUCTOR) - 1
    for idx, (io_field, oracle_arg, needs_int_cast) in enumerate(_IO_PARAMS_CONSTRUCTOR):
        comma = '' if idx == last_idx else ','
        field = args_by_oracle.get(oracle_arg) if oracle_arg else None
        if field is not None and field['parameter_mode'] in out_modes:
            jn = field['java_name']
            expr = f'{jn} != null ? {jn}.intValue() : null' if needs_int_cast else jn
        else:
            expr = f'ioParameters.{io_field}()'
        lines.append(f'    {expr}{comma}')
    lines.append(');')
    lines.append('')

    lines.append(f'return new CrudModuleResult<>(updatedIoParams, outRecord, "{callable_name}", "{target_table}", System.currentTimeMillis() - start);')
    lines.append('} catch (java.sql.SQLException e) {')
    lines.append(f'    throw new it.svg.crud.exception.CrudDataAccessException("Error reading OUT parameters from {callable_name}", e);')
    lines.append('}')

    return lines


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


def _resolve_target_table_fallback_sqlcl(args, target_table):
    """Resolve target table fallback from ALL_INDEXES and PK columns from ALL_CONS_COLUMNS."""
    if target_table:
        return {
            'target_table': target_table,
            'strategy': 'PROCEDURE',
            'select_owner': None,
            'select_short_name': None,
            'select_table': None,
            'select_condition_columns': [],
            'table_columns': [],
        }

    select_owner = derive_schema_owner(args.module)
    short_name = derive_short_name(args.module)

    idx_rows = csv_rows(run_sqlcl_query(
        args.sqlcl_path, args.db_user, args.db_password, args.db_tns, args.conn_name,
        INDEX_TABLE_SQL.format(owner=select_owner, short_name=short_name), args.java_home
    ))
    if not idx_rows:
        return {
            'target_table': None,
            'strategy': 'PROCEDURE',
            'select_owner': select_owner,
            'select_short_name': short_name,
            'select_table': None,
            'select_condition_columns': [],
            'table_columns': [],
        }

    fallback_table = idx_rows[0].get('TABELLA') or idx_rows[0].get('tabella')
    select_table = strip_select_suffix(fallback_table)
    table_name_for_columns = fallback_table

    cond_rows = csv_rows(run_sqlcl_query(
        args.sqlcl_path, args.db_user, args.db_password, args.db_tns, args.conn_name,
        PK_COLUMNS_SQL.format(owner=select_owner, short_name=short_name), args.java_home
    ))
    condition_columns = [r.get('COLUMN_NAME') or r.get('column_name') for r in cond_rows]
    condition_columns = [c for c in condition_columns if c]

    col_rows = csv_rows(run_sqlcl_query(
        args.sqlcl_path, args.db_user, args.db_password, args.db_tns, args.conn_name,
        TAB_COLUMNS_SQL.format(owner=select_owner, table_name=table_name_for_columns), args.java_home
    ))
    table_columns = []
    for c in col_rows:
        table_columns.append({
            'COLUMN_NAME': c.get('COLUMN_NAME') or c.get('column_name'),
            'NULLABLE': c.get('NULLABLE') or c.get('nullable'),
            'DATA_TYPE': c.get('DATA_TYPE') or c.get('data_type'),
            'DATA_PRECISION': c.get('DATA_PRECISION') or c.get('data_precision'),
            'DATA_SCALE': c.get('DATA_SCALE') or c.get('data_scale'),
        })

    return {
        'target_table': fallback_table,
        'strategy': 'SELECT',
        'select_owner': select_owner,
        'select_short_name': short_name,
        'select_table': select_table,
        'select_condition_columns': condition_columns,
        'table_columns': table_columns,
    }


def _resolve_target_table_fallback_direct(args, cursor, target_table):
    """Direct-connection variant of target table fallback."""
    if target_table:
        return {
            'target_table': target_table,
            'strategy': 'PROCEDURE',
            'select_owner': None,
            'select_short_name': None,
            'select_table': None,
            'select_condition_columns': [],
            'table_columns': [],
        }

    select_owner = derive_schema_owner(args.module)
    short_name = derive_short_name(args.module)

    cursor.execute(INDEX_TABLE_SQL.format(owner=select_owner, short_name=short_name))
    idx_rows = cursor.fetchall()
    if not idx_rows:
        return {
            'target_table': None,
            'strategy': 'PROCEDURE',
            'select_owner': select_owner,
            'select_short_name': short_name,
            'select_table': None,
            'select_condition_columns': [],
            'table_columns': [],
        }

    fallback_table = idx_rows[0][0]
    select_table = strip_select_suffix(fallback_table)
    table_name_for_columns = fallback_table

    cursor.execute(PK_COLUMNS_SQL.format(owner=select_owner, short_name=short_name))
    condition_columns = [r[0] for r in cursor.fetchall() if r and r[0]]

    cursor.execute(TAB_COLUMNS_SQL.format(owner=select_owner, table_name=table_name_for_columns))
    table_columns = []
    for c in cursor.fetchall():
        table_columns.append({
            'COLUMN_NAME': c[0],
            'NULLABLE': c[1],
            'DATA_TYPE': c[2],
            'DATA_PRECISION': c[3],
            'DATA_SCALE': c[4],
        })

    return {
        'target_table': fallback_table,
        'strategy': 'SELECT',
        'select_owner': select_owner,
        'select_short_name': short_name,
        'select_table': select_table,
        'select_condition_columns': condition_columns,
        'table_columns': table_columns,
    }


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
    fallback_info = _resolve_target_table_fallback_sqlcl(args, target_table)
    target_table = fallback_info['target_table']

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
        'generation_strategy': fallback_info['strategy'],
        'select_owner': fallback_info['select_owner'],
        'select_short_name': fallback_info['select_short_name'],
        'select_table': fallback_info['select_table'],
        'select_condition_columns': fallback_info['select_condition_columns'],
        'table_columns': fallback_info['table_columns'],
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
        fallback_info = _resolve_target_table_fallback_direct(args, cur, target_table)
        target_table = fallback_info['target_table']

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
            'generation_strategy': fallback_info['strategy'],
            'select_owner': fallback_info['select_owner'],
            'select_short_name': fallback_info['select_short_name'],
            'select_table': fallback_info['select_table'],
            'select_condition_columns': fallback_info['select_condition_columns'],
            'table_columns': fallback_info['table_columns'],
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
    generation_strategy = metadata.get('generation_strategy') or 'PROCEDURE'
    fields = []
    field_overrides = overrides.get('fields', {})

    if generation_strategy == 'SELECT' and metadata.get('table_columns'):
        condition_set = {c.upper() for c in (metadata.get('select_condition_columns') or [])}
        for idx, c in enumerate(metadata['table_columns'], start=1):
            oracle_name = c['COLUMN_NAME']
            ov = field_overrides.get(oracle_name, {})
            numeric = is_numeric(c['DATA_TYPE'])
            nullable = ov.get('nullable', str(c.get('NULLABLE', 'Y')).upper() == 'Y')
            java_type = ov.get('java_type') or to_java_type(c['DATA_TYPE'], c['DATA_PRECISION'], c['DATA_SCALE'])
            java_name = ov.get('java_name') or camel_case(oracle_name)
            fields.append({
                'position': idx,
                'oracle_argument_name': oracle_name,
                'sql_column_name': oracle_name,
                'java_name': java_name,
                'java_name_first_upper': java_name[:1].upper() + java_name[1:],
                'java_type': java_type,
                'parameter_mode': 'IN' if oracle_name.upper() in condition_set else 'OUT',
                'numeric': numeric,
                'nullable': bool(nullable),
                'normalization_policy': 'ZERO_IF_NOT_NULLABLE_NUMERIC' if numeric and not nullable else 'NULL_OTHERWISE'
            })
    else:
        for a in metadata['arguments']:
            ov = field_overrides.get(a['oracle_argument_name'], {})
            numeric = is_numeric(a['data_type'])
            nullable = ov.get('nullable', a['nullable'])
            java_type = ov.get('java_type') or to_java_type(a['data_type'], a['data_precision'], a['data_scale'])
            java_name = ov.get('java_name') or camel_case(a['oracle_argument_name'])
            fields.append({
                'position': a['position'],
                'oracle_argument_name': a['oracle_argument_name'],
                'sql_column_name': a['oracle_argument_name'],
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

    callable_name = overrides.get('callable_name') or metadata['callable_name']
    target_table_val = metadata['target_table'] or 'UNKNOWN'

    binders = []
    readers = []
    if generation_strategy == 'PROCEDURE':
        for f in fields:
            getter = f"record.{f['java_name_first_upper'][0].lower() + f['java_name_first_upper'][1:]}()"
            sql_type = to_sql_type(f['java_type'])
            if f['parameter_mode'] in ('IN', 'INOUT', 'IN/OUT'):
                default_expr = '0' if f['numeric'] and not f['nullable'] else 'null'
                binders.append(f"cs.setObject({f['position']}, {getter} == null ? {default_expr} : {getter}, {sql_type});")
            if f['parameter_mode'] in ('OUT', 'INOUT', 'IN/OUT'):
                binders.append(f"cs.registerOutParameter({f['position']}, {sql_type});")

        readers = _build_readers(fields, record_class_name, callable_name, target_table_val)

    select_table = metadata.get('select_table') or strip_select_suffix(target_table_val)
    select_condition_columns = metadata.get('select_condition_columns') or []
    condition_fields = [f for f in fields if f['oracle_argument_name'].upper() in {c.upper() for c in select_condition_columns}]
    select_projection = ', '.join(f['oracle_argument_name'] for f in fields) if fields else '*'
    if condition_fields:
        where_clause = ' AND '.join(f"{f['oracle_argument_name']} = ?" for f in condition_fields)
    else:
        where_clause = '1 = 1'
    select_sql = f"SELECT {select_projection} FROM {select_table} WHERE {where_clause}"

    model = {
        'base_package': BASE_PACKAGE,
        'module_name': args.module.upper(),
        'module_name_lower': args.module.lower(),
        'owner': metadata['owner'],
        'callable_name': callable_name,
        'target_table': target_table_val,
        'record_class_name': record_class_name,
        'repository_class_name': repository_class_name,
        'service_interface_name': service_interface_name,
        'service_impl_name': service_impl_name,
        'constants_class_name': constants_class_name,
        'generation_strategy': generation_strategy,
        'fields': fields,
        'sql_placeholders': ', '.join(['?'] * len(fields)),
        'binders': binders,
        'readers': readers,
        'select_owner': metadata.get('select_owner') or derive_schema_owner(args.module),
        'select_short_name': metadata.get('select_short_name') or derive_short_name(args.module),
        'select_table': select_table,
        'select_condition_columns': select_condition_columns,
        'select_condition_fields': condition_fields,
        'select_sql': select_sql,
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
            f'    Nessun fallback su ALL_INDEXES disponibile; i nomi classi useranno il modulo ({args.module}).\n'
            f'    Per forzare il nome tabella usa: --target-table <NOME_TABELLA>',
            file=sys.stderr
        )

    model = build_model(args, metadata, overrides)
    render(args, model)
    print(f"Generated module {args.module.upper()} in {args.out}")


if __name__ == '__main__':
    main()
