# CRUD Generator - Tooling Python

Genera i moduli Java CRUD a partire dai metadati delle stored procedure Oracle.

## Prerequisiti

```bash
pip install oracledb Jinja2 pyyaml
```

## Utilizzo

```bash
# Dalla root del progetto crud-data-access
python generator/generate_crud_module.py \
  --module VPO01100 \
  --db-user "$DB_USER" \
  --db-password "$DB_PASSWORD" \
  --db-tns "$DB_TNS" \
  --conn-name "$DB_CONN_NAME" \
  --mode direct \
  --out generated-modules
```

Vedere `examples/` per script pronti all'uso (`.sh` e `.ps1`).

## Parametri principali

| Parametro | Default | Descrizione |
|---|---|---|
| `--module` | — | Nome modulo Oracle (es. VPO01100) |
| `--mode` | `direct` | `direct` (oracledb) o `sqlcl` |
| `--out` | `generated-modules` | Directory output |
| `--override-dir` | `generator/overrides` | Directory YAML override per modulo |
| `--templates-dir` | `generator/templates` | Directory template Jinja2 |

## Override per modulo

Creare `overrides/<MODULO>.yaml` per personalizzare nomi classi, tipi Java, ecc.
Esempio: `overrides/VPO01100.yaml`

## Output generato

Per ogni modulo genera in `<out>/src/main/java/it/svg/crud/`:
- `model/dto/<Modulo>Record.java`
- `repository/<Modulo>Repository.java`
- `service/<Modulo>Service.java`
- `service/impl/<Modulo>ServiceImpl.java`
- `docs/<MODULO>_metadata.md`
- `<MODULO>_model.json`
