# CRUD Data Access Generator (core backend only)

Libreria core e generatore Python per la generazione di classi Java di accesso dati basate su **stored procedure Oracle** omonime ai moduli COBOL.
Progetto di supporto a `cobol-modernisation-VPOVM700` e futuri progetti di modernizzazione SVG.

Package Java: `it.svg.crud`

## Perimetro supportato
- Solo **procedure standalone**
- **No package**
- **No cursor**
- **No collection**
- **No object type Oracle complessi**
- Mapping `modulo -> owner` = primi 3 caratteri del modulo (es. `VPO01100 -> VPO`)
- Mapping `modulo -> tabella` tramite `ALL_SOURCE` su owner `VPO_CRUD`
- Nullability campi da `ALL_TAB_COLUMNS`
- Function code supportati: `SR`, `UR`, `IR`, `DR`
- `IO-PARAMETERS` standard unico per tutte le CRUD

## Output del generatore Python
Per un modulo (es. `VPO01100`) genera:
- `model.dto`
  - `<Modulo>Record`
  - `IoParameters`
- `constants`
  - `CrudReturnCodeConstants`
  - `CrudFunctionCodeConstants`
- `repository`
  - `<Modulo>Repository`
- `service`
  - `<Modulo>Service`
- `service.impl`
  - `<Modulo>ServiceImpl`
- `exception`
  - eccezioni e handler base
- `config`
  - configurazione datasource Oracle
- `util`
  - supporto normalizzazione e mapping tipi
- `md`
  - metadata deterministici del modulo generato

## Struttura del progetto

```
crud-data-access/
├── pom.xml                          # Core Java library (groupId: it.svg.crud)
├── src/
│   ├── main/java/it/svg/crud/       # Sorgenti Java core
│   │   ├── config/
│   │   ├── constants/
│   │   ├── exception/
│   │   ├── model/dto/
│   │   ├── repository/
│   │   ├── service/impl/
│   │   └── util/
│   └── test/java/it/svg/crud/       # Test (struttura predisposta)
├── generator/                       # Tooling Python per la generazione
│   ├── generate_crud_module.py      # Script principale
│   ├── templates/                   # Template Jinja2
│   ├── overrides/                   # Override YAML per modulo
│   └── examples/                    # Script di esempio
├── generated-modules/               # Artefatti generati di esempio (versionati)
└── docs/                            # Documentazione
```


## Scelta architetturale

Lo standard Java allegato prevede anche controller, JPA e microservice completi. Qui **non** vengono generati controller né entity JPA, perché il target è un **core backend di accesso dati** da riusare dentro microservizi più grandi.

## Esecuzione rapida
### Python direct
```bash
python generator/generate_crud_module.py \
  --module VPO01100 \
  --db-user "$DB_USER" \
  --db-password "$DB_PASSWORD" \
  --db-tns "$DB_TNS" \
  --conn-name "$DB_CONN_NAME" \
  --mode direct \
  --out generated-modules
```

### Python SQLcl-compatible
```bash
python generator/generate_crud_module.py \
  --module VPO01100 \
  --db-user "$DB_USER" \
  --db-password "$DB_PASSWORD" \
  --db-tns "$DB_TNS" \
  --conn-name "$DB_CONN_NAME" \
  --mode sqlcl \
  --sqlcl-path sql \
  --out generated-modules
```

