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


## Come usare `crud-data-access-core` come libreria Maven

### 1. Prerequisito: `mvn install` locale (o GitHub Packages)

```bash
# Dal repo crud-data-access
mvn install
```

Questo deposita `it.svg.crud:crud-data-access-core:1.0.0-SNAPSHOT` nel `.m2` locale.

### 2. Aggiungere la dipendenza al microservizio

```xml
<dependency>
    <groupId>it.svg.crud</groupId>
    <artifactId>crud-data-access-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 3. Auto-configuration

Spring Boot auto-configura automaticamente tutti i bean CRUD
(`IndividualeService`, `EventoContrattoService`, …) tramite
`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.

**Nota**: il datasource Oracle (`crud.db.tns`) è opzionale. Se non configurato,
la libreria usa il datasource primario del microservizio (es. H2 per i test).

### 4. Iniettare il service nel microservizio

```java
@Repository
public class IndividualeRepository {
    private final IndividualeService individualeService;
    private final IndividualeRecordMapper mapper;

    public IndividualeRepository(IndividualeService individualeService,
                                  IndividualeRecordMapper mapper) {
        this.individualeService = individualeService;
        this.mapper = mapper;
    }

    public Optional<Individuale> findBySeqRapporto(Long seqRapporto, String codCompagnia, Long ts) {
        IoParameters ioParams = new IoParameters("SR", null, /* ... */ null, ts, null, null, null);
        IndividualeRecord input = new IndividualeRecord("SR", null, /* ... */ seqRapporto, null, /* ... */);
        CrudModuleResult<IndividualeRecord> result = individualeService.execute(ioParams, input);
        IndividualeRecord out = result.record();
        if (out == null || out.sTipoIndividuale() == null) return Optional.empty();
        return Optional.of(mapper.toEntity(out));
    }
}
```

### 5. Generare un nuovo modulo CRUD

```bash
python generator/generate_crud_module.py \
  --module VPO04500 \
  --target-table EVENTO_CONTRATTO \      # opzionale: forzare nome tabella
  --db-user "$DB_USER" --db-password "$DB_PASSWORD" \
  --db-tns "$DB_TNS" --conn-name "$CONN_NAME"
```

Dopo la generazione:
1. Committare `generated-modules/` su `crud-data-access`
2. `mvn install` su `crud-data-access`
3. Aggiornare la dipendenza nel microservizio

---

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

