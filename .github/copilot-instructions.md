# Copilot Instructions — crud-data-access

## What this project is

A two-layer toolkit for COBOL/Oracle modernisation at SVG:

1. **Java core library** (`src/`) — a reusable Spring Boot data-access engine. Consumed as a dependency by modernisation microservices (e.g. `cobol-modernisation-VPOVM700`). It contains only plumbing: `AbstractOracleProcedureRepository`, shared DTOs (`IoParameters`, `CrudModuleResult`), constants, exceptions, and config. **No controllers. No JPA entities. No Spring Data.**

2. **Python generator** (`generator/`) — reads Oracle metadata (via `oracledb` or SQLcl) and renders Jinja2 templates into module-specific Java classes. Output goes to `generated-modules/` and is committed as reference examples.

The two layers share the package root `it.svg.crud`.

---

## Build commands

```bash
# Compile and package the Java core library
mvn package

# Compile only (skip tests)
mvn compile

# Run tests (none exist yet — test structure is scaffolded at src/test/java/it/svg/crud/)
mvn test
```

> **Windows note**: `maven-compiler-plugin` is configured with `useIncrementalCompilation=false` (required for MapStruct on Windows to avoid silent class drops).

---

## Generator usage

```bash
# From the repo root — direct Oracle connection (python-oracledb)
python generator/generate_crud_module.py \
  --module VPO01100 \
  --db-user "$DB_USER" --db-password "$DB_PASSWORD" --db-tns "$DB_TNS" \
  --conn-name "$DB_CONN_NAME" \
  --mode direct \
  --out generated-modules

# SQLcl fallback (required for some legacy Oracle accounts, DPY-3015)
python generator/generate_crud_module.py \
  --module VPO01100 \
  --db-user "$DB_USER" --db-password "$DB_PASSWORD" --db-tns "$DB_TNS" \
  --conn-name "$DB_CONN_NAME" \
  --mode sqlcl --sqlcl-path sql \
  --out generated-modules
```

The generator automatically falls back from `direct` to `sqlcl` when it encounters `DPY-3015`.

Default values already set: `--templates-dir generator/templates`, `--override-dir generator/overrides`.

### Forza il nome tabella con `--target-table`

Se la query automatica su `ALL_SOURCE` non restituisce risultati (es. la struttura del sorgente Oracle non corrisponde al pattern atteso), i nomi delle classi verranno derivati dal nome modulo come fallback (es. `Vpo01100Repository`). In quel caso usare `--target-table`:

```bash
python generator/generate_crud_module.py \
  --module VPO01100 \
  --target-table INDIVIDUALE \   # forza il nome tabella → IndividualeRepository
  --db-user "$DB_USER" ...
```

Il generator stampa su stderr un avviso chiaro quando scatta il fallback.

---

## Uso come libreria Maven

Il progetto produce `crud-data-access-core-1.0.0-SNAPSHOT.jar` (JAR standard, no fat-JAR).
I moduli generati (`generated-modules/src/`) vengono inclusi tramite `build-helper-maven-plugin`.

### Build + install locale
```bash
mvn install    # deposita in .m2 locale
```

### Distribuzione GitHub Packages
```bash
mvn deploy    # pubblica su maven.pkg.github.com/StefanoRoccato/crud-data-access
```

### Dipendenza per microservizi consumer
```xml
<dependency>
    <groupId>it.svg.crud</groupId>
    <artifactId>crud-data-access-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Auto-configuration Spring Boot
La libreria si auto-configura tramite `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.
Attiva il component scan di `it.svg.crud` → tutti i bean CRUD (`IndividualeService`, ecc.) vengono registrati.

**Regole di conflitto gestite automaticamente:**
- `DataSourceConfig`: attiva solo se `crud.db.tns` è configurato (non interferisce con H2/jOOQ nei consumer)
- `CrudOracleHealthIndicator`: attiva solo con `crud.db.tns`
- `GlobalExceptionHandler` crud: registrato solo se il microservizio non ha già un `@RestControllerAdvice`
- Repository bean: usano `@Repository("crudXxxRepository")` per evitare conflitti con omonimi nel microservizio

---

## Architecture

### Java core (`src/main/java/it/svg/crud/`)

```
config/        CrudDbProperties + DataSourceConfig (OracleDataSource via JDBC thin)
               CrudOracleHealthIndicator (Spring Actuator health check for Oracle + circuit breakers)
constants/     CrudReturnCodeConstants, CrudFunctionCodeConstants, AppConstants
exception/     CrudDataAccessException, ResourceNotFoundException, BadRequestException,
               UnsupportedCrudPatternException, GlobalExceptionHandler (@RestControllerAdvice)
model/dto/     IoParameters (universal record), CrudModuleResult<T>, ArgumentMetadataResponse
repository/    AbstractOracleProcedureRepository — base for ALL generated repositories
service/       CrudMetadataService interface + stub impl (resolved by generator, not at runtime)
util/          OracleTypeMapper, ValueNormalizer
```

`AbstractOracleProcedureRepository` owns the JDBC `CallableStatement` lifecycle **plus Resilience4j circuit-breaker and Micrometer metrics**. Its constructor requires `DataSource`, `MeterRegistry`, and `CircuitBreakerRegistry`. Generated repositories extend it and provide:
- A `SqlBinder` lambda (IN/INOUT params via `cs.setObject(pos, ...)`)
- An extractor lambda (OUT params read-back + `CrudModuleResult` construction)

Each call to `execute(sql, procedureName, binder, extractor)` automatically:
- Wraps the JDBC call in a named Resilience4j circuit breaker (`crud.<procedureName>`)
- Records a `crud.procedure.execution` Timer and `crud.procedure.calls` Counter in Micrometer
- Logs circuit breaker state transitions at INFO level

### Generated output (`generated-modules/`)

For each module (e.g. `VPO01100` → table `INDIVIDUALE`) the generator emits into `src/main/java/it/svg/crud/`:
- `model/dto/<Table>Record.java` — Java record, one field per Oracle argument
- `repository/<Table>Repository.java` — extends `AbstractOracleProcedureRepository`
- `service/<Table>Service.java` — interface
- `service/impl/<Table>ServiceImpl.java` — `@Service @Transactional` with functionCode validation, MDC logging, returnCode check
- `constants/<Table>Constants.java` — `MODULE_NAME`, `TARGET_TABLE`, `OWNER`, `SUPPORTED_FUNCTION_CODES`
- `src/test/…/service/impl/<Table>ServiceImplTest.java` — 4 unit tests (JUnit 5 + Mockito)
- `docs/<MODULE>_metadata.md` + `<MODULE>_model.json` — introspection artifacts

**Class name derivation**: class names are based on the **target table name** (`INDIVIDUALE` → `IndividualeRepository`). If the target table cannot be resolved from Oracle (`UNKNOWN`), the module name is used as fallback (`VPO01100` → `Vpo01100Repository`).

### Technology stack
- **Java 21**, Spring Boot 3.5+, Maven
- **Resilience4j 2.3+** — circuit breaker on every procedure call (config in `application.yml` under `resilience4j`)
- **Micrometer + OpenTelemetry OTLP** — `crud.procedure.execution` Timer, `crud.procedure.calls` Counter per procedure
- **Spring Boot Actuator** — `/actuator/health` includes `crudOracle` indicator (DB ping + circuit breaker states)
- **MapStruct 1.6+** — available in consuming microservices; declared in pom for annotation processing
- **ArchUnit + json-unit-assertj** — test dependencies

### Key conventions

### Package and naming
- Root package: `it.svg.crud` (groupId `it.svg.crud`, consistent with sibling project `it.svg.*`)
- Module names are UPPER_CASE Oracle names (e.g. `VPO01100`)
- **Java class names derive from the target table name**: `INDIVIDUALE` → `IndividualeRepository`, `IndividualeService`, etc.
- If the target table cannot be resolved from Oracle, the module name is used as fallback: `VPO01100` → `Vpo01100Repository`
- All class names can be overridden individually in `generator/overrides/<MODULE>.yaml` (keys: `record_class_name`, `repository_class_name`, `service_interface_name`, `service_impl_name`, `constants_class_name`)
- Oracle argument `ORACLE_ARGUMENT_NAME` → Java field `oracleArgumentName` (camelCase)

### Owner / schema derivation
- Schema owner = first 3 characters of module name + `_CRUD`: `VPO01100` → `VPO_CRUD`
- Target table is looked up from `ALL_SOURCE` on the owner schema

### DTOs are Java records
All data transfer objects (`IoParameters`, `CrudModuleResult<T>`, generated `<Module>Record`) are Java `record` types. Constructors are the only way to create them.

### Constructor injection only
No field injection (`@Autowired`). All Spring beans use constructor injection.

### IoParameters
`IoParameters` is universal across all CRUD modules — every Oracle procedure uses the same standard IO block. It is always passed alongside the module-specific `<Module>Record`.

### Normalization policy
- Numeric field that is `NOT NULL` in Oracle → normalized to `0` when Java value is `null`
- All other fields → `null` passthrough
- Defined in `ValueNormalizer` and enforced in generated `SqlBinder` lambdas

### OUT parameter TODOs
Generated repositories emit `// TODO leggere OUT param <NAME> posizione <N>` comments in the extractor lambda for every `OUT`/`INOUT` argument. These must be completed manually by reading `cs.getObject(pos)` and mapping back to the record.

### Module overrides
To rename fields, override Java types, or force specific class names, create `generator/overrides/<MODULE>.yaml`:
```yaml
callable_name: VPO01100              # optional override of procedure name
# Class name overrides (default: derived from target table name)
record_class_name: IndividualeRecord
repository_class_name: IndividualeRepository
service_interface_name: IndividualeService
service_impl_name: IndividualeServiceImpl
constants_class_name: IndividualeConstants
fields:
  ORACLE_ARGUMENT_NAME:
    java_name: myJavaName            # camelCase override
    java_type: java.math.BigDecimal  # type override
    nullable: false
```

### Oracle type mapping (defaults)
| Oracle type | Java type |
|---|---|
| `VARCHAR2`, `CHAR`, `CLOB` | `String` |
| `DATE` | `java.time.LocalDate` |
| `TIMESTAMP*` | `java.time.LocalDateTime` |
| `NUMBER(≤9, 0)` | `Integer` |
| `NUMBER(>9, 0)` | `Long` |
| `NUMBER(*, >0)` | `java.math.BigDecimal` |

### Generated repository constructor signature
Every generated `<Module>Repository` must pass all three arguments to `super()`:
```java
public Vpo01100Repository(DataSource dataSource,
                           MeterRegistry meterRegistry,
                           CircuitBreakerRegistry circuitBreakerRegistry) {
    super(dataSource, meterRegistry, circuitBreakerRegistry);
}
```
And call `execute(sql, "PROCEDURE_NAME", binder, extractor)` — the second argument (`procedureName`) is required for metric tagging and circuit-breaker naming.

### Error handling — GlobalExceptionHandler
`GlobalExceptionHandler` is a `@RestControllerAdvice` that maps:
- `ResourceNotFoundException` → HTTP 404 + `{ "code": "NOT_FOUND", ... }`
- `BadRequestException` → HTTP 400 + `{ "code": "BAD_REQUEST", ... }`
- `UnsupportedCrudPatternException` → HTTP 501 + `{ "code": "UNSUPPORTED_LEGACY_BOUNDARY", ... }`
- `CrudDataAccessException` → HTTP 500 + `{ "code": "DATA_ACCESS_ERROR", ... }`

### @Transactional rule
`@Transactional` is acceptable on service methods that exclusively perform JDBC calls against a single Oracle data source. Do NOT annotate methods that cross HTTP, MQ, or other unmanaged external boundaries.

### What is intentionally NOT generated
- REST controllers
- JPA entities / Spring Data repositories
- MapStruct mappers
- OpenAPI annotations on generated classes

These belong in the consuming microservice, not in the CRUD core.

---

## Environment variables

| Variable | Purpose |
|---|---|
| `DB_USER` | Oracle username |
| `DB_PASSWORD` | Oracle password |
| `DB_TNS` | Oracle TNS alias or connect string |
| `DB_CONN_NAME` | SQLcl named connection |
| `TNS_ADMIN` | Optional: path to `tnsnames.ora` directory |
| `TABLE_SOURCE_OWNER` | Default `VPO_CRUD` — schema where `ALL_SOURCE` is queried for table mapping |

Copy `src/main/resources/application-local.example.yml` to `application-local.yml` (gitignored) for local Spring config.
