# Progettazione layer Java Spring Boot 3.x per sostituzione CRUD COBOL VPO

## 1. Scopo

Definire il **miglior approccio architetturale** per riscrivere in **Java Spring Boot 3.x+** il layer di accesso ai dati dei moduli COBOL VPO, mantenendo **immutata la semantica di accesso tramite stored procedure PL/SQL** che conservano il **nome del modulo COBOL** (es. `Individuale`, `EventoContratto`).

Questo documento usa come moduli campione:
- `Individuale` → tabella `INDIVIDUALE`
- `EventoContratto` → tabella `EVENTO_CONTRATTO`

## 2. Premesse osservate nei moduli campione

Dai materiali forniti emerge un pattern molto netto:

1. I moduli COBOL analizzati sono **CRUD shell infrastrutturali**, non servizi di business.
2. Il COBOL **non contiene SQL diretto**: delega tutto a una **stored procedure Oracle** omonima (`Individuale`, `EventoContratto`).
3. L’interfaccia è di tipo **passed-by-reference** e ruota attorno a:
   - una struttura di controllo (`IO-PARAMETERS`)
   - una struttura record tabellare (`INDIVIDUALE`, `EVENTO_CONTRATTO`, ecc.)
4. Esiste una semantica COBOL specifica da preservare:
   - gestione **null indicator**
   - normalizzazione campi non numerici
   - propagazione di `IO-RETURN-CODE` e `IO-SQLERRMC`
   - nessuna logica transazionale nel guscio COBOL
5. Il pattern è candidabile a una **generazione industrializzata** perché altamente ripetitivo.

## 3. Scelta architetturale consigliata

### Migliore opzione
La soluzione migliore è implementare un **layer adapter/repository generato**, basato su:

- **Spring Boot 3.x**
- **spring-jdbc**
- **invocazione esplicita di `CallableStatement` generata per modulo**
- **metadata model centrale** da cui generare classi, binder, mapper e test

### Perché questa è la scelta migliore
È la scelta più solida perché:

- preserva il comportamento reale dei moduli COBOL, che oggi chiamano procedure PL/SQL
- evita di introdurre semantiche ORM/JPA non presenti nel sistema legacy
- consente controllo puntuale su:
  - ordine parametri
  - direzione IN/OUT/INOUT
  - nullabilità
  - conversioni COBOL → Java → Oracle
  - ritorno codici Oracle / applicativi
- è **automatizzabile**, quindi scalabile su tutte le tabelle
- separa in modo pulito **codice generato** e **codice manuale**

### Cosa evitare
Per questo scenario, è preferibile **non** usare come baricentro:

- **JPA/Hibernate**: introduce un modello entity-oriented distante dal pattern procedurale reale
- **repository scritti a mano tabella per tabella**: troppo costosi da mantenere
- **reflection runtime pesante**: rende più fragile debugging, performance e controllo sui binding Oracle

---

## 4. Architettura logica proposta

La proposta è una architettura a 4 strati.

## 4.1 Strato 1 — Core infrastrutturale comune
Contiene componenti riusabili per tutti i moduli generati.

### Classi principali

#### `CrudFunctionCode`
Enum con i codici funzione (`SR`, `SM`, `IR`, `UR`, `DR`, `SF`, `SN`, `CC`, `SU`, `UT`, `UM`, ecc.).

#### `IoParameters`
DTO canonico Java che rappresenta l’area `IO-PARAMETERS`.

#### `ModuleRecord`
Interfaccia marker per i DTO record tabellari generati.

#### `StoredProcedureCallContext<T extends ModuleRecord>`
Contiene:
- `CrudFunctionCode functionCode`
- `IoParameters ioParameters`
- `T record`

#### `StoredProcedureCallResult<T extends ModuleRecord>`
Contiene:
- `IoParameters ioParameters`
- `T record`
- eventuali metadati tecnici (tempo chiamata, procedure name, correlation id)

#### `FieldBindingMetadata`
Descrive un campo:
- nome COBOL
- nome Java
- tipo Java
- posizione parametro
- direzione
- nullable sì/no
- policy di normalizzazione

#### `ModuleMetadata`
Descrive il modulo (`Individuale`, `EventoContratto`, ...):
- nome procedure
- record class
- elenco campi
- mapping parametri IO
- policy speciali

#### `NullNormalizationPolicy`
Astrazione per la semantica COBOL:
- `NULL_IF_BLANK`
- `ZERO_IF_NOT_NUMERIC`
- `NULL_IF_NOT_NUMERIC`
- `KEEP_AS_IS`

#### `StoredProcedureExecutor`
Servizio infrastrutturale che esegue fisicamente la chiamata Oracle.

#### `OracleErrorMapper`
Converte eccezioni SQL / SQLCODE in:
- `IO-RETURN-CODE`
- `IO-SQLERRMC`
- eccezioni Java opzionali

#### `DryRunPolicyEvaluator`
Preserva la semantica `IO-FLAG-AGGIORNA-DB`.

---

## 4.2 Strato 2 — Moduli generati per tabella/procedura
Per ogni modulo COBOL/procedura Oracle si genera un package dedicato.

Esempio per `Individuale`:

```text
it.company.vpo.modules.Individuale/
├── IndividualeRecord.java
├── IndividualeRepository.java
├── IndividualeProcedureCaller.java
├── IndividualeMetadata.java
├── IndividualeFieldNames.java
├── IndividualeMapper.java
├── IndividualeNormalizer.java
├── IndividualeService.java
└── IndividualeContractTest.java
```

Esempio per `EventoContratto`:

```text
it.company.vpo.modules.EventoContratto/
├── EventoContrattoRecord.java
├── EventoContrattoRepository.java
├── EventoContrattoProcedureCaller.java
├── EventoContrattoMetadata.java
├── EventoContrattoMapper.java
├── EventoContrattoNormalizer.java
├── EventoContrattoService.java
└── EventoContrattoContractTest.java
```

### Responsabilità delle classi generate

#### `IndividualeRecord`
DTO record tabellare Java fortemente tipizzato.

#### `IndividualeMetadata`
Descrive tutti i campi e il binding ordinato della procedura.

#### `IndividualeNormalizer`
Applica le regole derivate dal COBOL:
- blank/low-values/null indicator → `null`
- gruppo numerici “zero if not numeric”
- gruppo numerici “null if not numeric”

#### `IndividualeProcedureCaller`
Classe tecnica che costruisce ed esegue la `CallableStatement`.

#### `IndividualeRepository`
Espone un metodo unico o più metodi semantici:
- `execute(...)`
oppure
- `selectOne(...)`
- `insert(...)`
- `update(...)`
- `delete(...)`

#### `IndividualeService`
Fa orchestration applicativa minima:
- validazione codice funzione
- dry-run
- logging tecnico
- chiamata repository

---

## 4.3 Strato 3 — Facade applicativa
Questo strato serve solo se il layer viene esposto verso altri componenti o API.

### Opzione consigliata
Esporre **prima** una **facade Java interna**, non direttamente REST.

Motivo:
- i moduli legacy sono procedural shells, non veri servizi REST
- conviene stabilizzare prima l’equivalenza funzionale
- la REST API può essere un livello successivo

### Facade consigliata

```java
public interface VpoModuleFacade<T extends ModuleRecord> {
    StoredProcedureCallResult<T> execute(
        CrudFunctionCode functionCode,
        IoParameters ioParameters,
        T record
    );
}
```

Solo dopo la stabilizzazione si potrà aggiungere:
- REST controller
- OpenAPI
- endpoint business-oriented

---

## 4.4 Strato 4 — API esterne (opzionale)
Se serve esporre HTTP:
- usare controller sottili
- non duplicare la logica del repository
- mantenere il mapping COBOL/PLSQL nel backend interno

Per la prima fase di migrazione questo strato è **opzionale**, non prioritario.

---

## 5. Struttura delle classi consigliata

## 5.1 Package structure generale

```text
it.company.vpo/
├── VpoApplication.java
├── core/
│   ├── model/
│   │   ├── CrudFunctionCode.java
│   │   ├── IoParameters.java
│   │   ├── ModuleRecord.java
│   │   ├── StoredProcedureCallContext.java
│   │   └── StoredProcedureCallResult.java
│   ├── metadata/
│   │   ├── FieldBindingMetadata.java
│   │   ├── ModuleMetadata.java
│   │   └── NullNormalizationPolicy.java
│   ├── oracle/
│   │   ├── StoredProcedureExecutor.java
│   │   ├── OracleCallableStatementSupport.java
│   │   ├── OracleErrorMapper.java
│   │   └── OracleTypeMapper.java
│   ├── validation/
│   │   ├── FunctionCodeValidator.java
│   │   └── IoParametersValidator.java
│   ├── transaction/
│   │   └── DryRunPolicyEvaluator.java
│   └── logging/
│       ├── TechnicalLogContext.java
│       └── ModuleCallLogger.java
├── modules/
│   ├── Individuale/
│   ├── EventoContratto/
│   └── ... altri moduli generati
├── config/
│   ├── DataSourceConfig.java
│   ├── JdbcConfig.java
│   └── TransactionConfig.java
└── support/
    ├── generation/
    └── testing/
```

## 5.2 Regole di progettazione delle classi

### Regola 1 — Un modulo COBOL = un package dedicato
Evita classi monolitiche e facilita troubleshooting.

### Regola 2 — Un record tabellare = un DTO Java puro
No annotazioni JPA necessarie.

### Regola 3 — Un repository per modulo
Il repository rappresenta la procedura, non la tabella in senso ORM.

### Regola 4 — Un normalizer per modulo
Perché la semantica null/non-numeric può cambiare da modulo a modulo.

### Regola 5 — Core comune senza dipendenze sui moduli
I moduli dipendono dal core, non viceversa.

### Regola 6 — Codice generato isolato da quello manuale
Per rigenerare senza rompere customizzazioni.

---

## 6. Modalità di invocazione delle stored procedure

## Migliore opzione tecnica
Usare **`JdbcTemplate` + `CallableStatement` esplicita generata**.

### Perché
Nel vostro scenario ci sono molti aspetti “strict contract”:
- nome procedura fisso
- ordine parametri rilevante
- molti campi
- gestione IN / OUT / INOUT
- possibili indicatori/null handling da preservare
- error mapping Oracle-specifico

Su questi aspetti, `CallableStatement` generata è più affidabile di un layer troppo astratto.

### Pattern consigliato

```java
@Repository
public class IndividualeProcedureCaller {

    private final DataSource dataSource;
    private final IndividualeNormalizer normalizer;

    public IndividualeProcedureCaller(DataSource dataSource, IndividualeNormalizer normalizer) {
        this.dataSource = dataSource;
        this.normalizer = normalizer;
    }

    public StoredProcedureCallResult<IndividualeRecord> execute(
            CrudFunctionCode functionCode,
            IoParameters io,
            IndividualeRecord record) {

        IndividualeRecord normalized = normalizer.normalize(record);

        return new StoredProcedureExecutor(dataSource).execute(
            "{ call Individuale(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }",
            cs -> {
                // bind IO params
                // bind record params
                // register out params
            },
            cs -> {
                // read IO output
                // read record output
                return new StoredProcedureCallResult<>(io, normalized);
            }
        );
    }
}
```

Nota: il codice sopra è un pattern, non la firma definitiva.

---

## 7. Modellazione dei DTO

## 7.1 `IoParameters`
Va progettato come DTO comune, condiviso da tutti i moduli.

### Campi consigliati
- `functionCode`
- `returnCode`
- `flagAggiornaDb`
- `flag1`
- `tipoStoricita`
- `flagTrovato`
- `idTimestampInizioVal`
- `idTimestampFineVal`
- `idDataInizioVal`
- `idDataFineVal`
- `dataCont`
- `idRiga`
- `idLock`
- `flUpdate`
- `concurrentTempUpdate`
- `timestampApp`
- `sqlerrmc`
- `livLog`
- `sessionId`

### Raccomandazione
Aggiungere metodi helper non invasivi:

```java
public boolean isDryRun();
public boolean isSuccess();
public boolean isWarning();
public boolean isSqlError();
```

## 7.2 Record DTO per modulo
Ogni modulo deve avere il proprio DTO.

Esempio:
- `IndividualeRecord`
- `EventoContrattoRecord`

### Regola naming
- COBOL / DB: `SEQ-EVENTO-CONTRATTO`
- Java: `seqEventoContratto`

### Tipi Java consigliati
- `PIC X(n)` → `String`
- `S9(...)` → `Integer` / `Long` / `BigDecimal`
- date logiche `X(10)` → preferibilmente `LocalDate` se il formato è stabilizzato
- timestamp numerici SVG → `Long`

### Regola importante
Il DTO **non** deve contenere logica tecnica Oracle.
Le policy di normalizzazione vanno in `Normalizer` / `Metadata`.

---

## 8. Gestione null indicator e normalizzazione

Questo è il punto più importante per preservare l’equivalenza COBOL.

## 8.1 Principio
In Java **non va simulato CHR(255)**.
Bisogna invece convergere verso:
- `null` lato Java
- `NULL SQL` lato JDBC
- policy esplicite per i casi “not numeric → zero”

## 8.2 Strategia consigliata
Generare per ogni modulo una **tabella metadata-driven** che dica, per ogni campo, quale policy applicare.

Esempio:

```java
public enum NullNormalizationPolicy {
    KEEP_AS_IS,
    NULL_IF_BLANK,
    ZERO_IF_NOT_NUMERIC,
    NULL_IF_NOT_NUMERIC
}
```

### Vantaggi
- niente if/else manuali sparsi
- generazione automatica
- testabilità semplice
- piena tracciabilità rispetto al comportamento COBOL

## 8.3 Regola pratica
La pipeline deve essere:

1. input Java raw
2. normalizzazione modulo-specifica
3. bind JDBC
4. chiamata procedura
5. lettura output
6. denormalizzazione opzionale verso DTO finale

---

## 9. Strategy pattern per i codici funzione

Per scalare bene conviene evitare `switch` giganti sparsi.

## Opzione consigliata
Usare un dispatcher semplice ma centralizzato.

```java
public interface CrudOperationHandler<T extends ModuleRecord> {
    StoredProcedureCallResult<T> handle(IoParameters io, T record);
}
```

Tuttavia, nel vostro caso specifico, poiché **la procedura fisica è una sola** e riceve già `IO-FUNCTION-CODE`, la scelta migliore è:

### migliore compromesso
- **non generare un handler separato per ogni function code**
- validare il code lato Java
- passare il code alla stored procedure
- lasciare alla procedura la semantica finale

Quindi: repository unico per modulo, non 20 repository per funzione.

---

## 10. Transazioni

## Scelta consigliata
Il layer Java deve essere **transaction-aware ma non transaction-owner**.

### Regola
Usare:

```java
@Transactional
```

solo sui servizi applicativi che orchestrano la chiamata, con semantica di partecipazione alla transazione del chiamante.

### Principi
- niente commit espliciti nel repository
- niente rollback manuali salvo casi eccezionali
- rispetto del `dry run`
- coerenza con il pattern COBOL osservato

---

## 11. Error handling

## Obiettivo
Preservare il contratto legacy:
- `IO-RETURN-CODE`
- `IO-SQLERRMC`

## Approccio consigliato

### 11.1 Due livelli di errore
1. **Errore tecnico Java/JDBC**
2. **Errore funzionale/Oracle restituito dalla procedura**

### 11.2 Policy
- se la procedura valorizza `IO-RETURN-CODE`, il risultato applicativo viene dal contratto legacy
- se JDBC fallisce prima, si mappa l’errore in una eccezione tecnica + eventuale popolamento coerente di `IO-SQLERRMC`

### 11.3 Classi utili
- `OracleProcedureException`
- `LegacyReturnCodeException` (solo se si vuole fallire su return code non zero)
- `OracleErrorMapper`

### 11.4 Osservabilità minima
Loggare sempre:
- modulo (`Individuale`)
- function code
- correlation id / session id
- duration
- return code
- errore SQL sintetico

Mai loggare indiscriminatamente payload completi se contengono dati sensibili.

---

## 12. Modalità di generazione delle classi

## 12.1 Principio generale
La generazione deve essere **metadata-driven**, non prompt-driven puro.

### Migliore opzione
Pipeline a due fasi:

1. **estrazione metadata canonici**
2. **generazione codice da template**

Questo è molto più robusto di far generare direttamente classi Java “free form” da documenti Markdown.

---

## 12.2 Modello canonico intermedio
Generare, per ogni modulo, un file descrittivo strutturato (YAML o JSON).

### Esempio

```yaml
moduleName: Individuale
procedureName: Individuale
tableName: INDIVIDUALE
recordClassName: IndividualeRecord
ioParametersClassName: IoParameters
fields:
  - cobolName: SEQ-EVENTO-CONTRATTO
    javaName: seqEventoContratto
    javaType: Long
    nullable: false
    normalizationPolicy: ZERO_IF_NOT_NUMERIC
    direction: INOUT
  - cobolName: TIPO-RIF-ESTERNO
    javaName: tipoRifEsterno
    javaType: String
    nullable: true
    normalizationPolicy: NULL_IF_BLANK
    direction: INOUT
```

### Perché serve
Il modello canonico diventa la **single source of truth** per:
- DTO
- metadata
- binder JDBC
- mapper output
- test
- documentazione tecnica

---

## 12.3 Cosa generare automaticamente
Per ogni modulo conviene generare almeno:

1. `Record DTO`
2. `Metadata`
3. `Normalizer`
4. `ProcedureCaller`
5. `Repository`
6. `Service`
7. `Unit test di normalizzazione`
8. `Contract test skeleton`
9. `README del modulo`

### Facoltativi
- Controller REST
- OpenAPI models
- Mapper MapStruct
- test data factory

---

## 12.4 Tecnologie consigliate per la generazione

### Migliore opzione
Usare un generatore deterministico basato su template, ad esempio:
- **Mustache** / **Handlebars** per template testuali
- oppure **JavaPoet** per generare codice Java tipizzato

### Raccomandazione pratica
Per partire velocemente:
- **YAML/JSON canonico + Mustache** è la combinazione migliore

Perché:
- semplice da versionare
- facile da debuggare
- diff leggibili
- ottimo per grandi volumi di moduli

---

## 12.5 Separazione generated vs handwritten

## Regola fondamentale
Il codice generato deve stare in package/source set separati.

### Struttura consigliata

```text
src/main/java/it/company/vpo/core/...          # manuale
src/main/java/it/company/vpo/modules/...       # manuale leggero / facade
src/generated/java/it/company/vpo/generated/...# generato
```

### Pattern consigliato
- classi generate `final` dove ha senso
- interfacce manuali stabili
- eventuali estensioni manuali solo sopra i generated artifacts

Così puoi rigenerare senza perdere custom.

---

## 12.6 Flusso di generazione industriale consigliato

```text
Analisi COBOL / reverse spec / copybook
        ↓
Parsing strutturato
        ↓
Modello canonico YAML/JSON
        ↓
Validazione metadata
        ↓
Generazione codice Java
        ↓
Generazione test
        ↓
Compilazione CI
        ↓
Contract test su DB DEV
```

---

## 13. Strategia di naming

## 13.1 Classi
- `IndividualeRecord`
- `IndividualeRepository`
- `IndividualeProcedureCaller`
- `IndividualeNormalizer`
- `IndividualeMetadata`

## 13.2 Package
- `it.company.vpo.modules.Individuale`
- `it.company.vpo.modules.EventoContratto`

## 13.3 Metodi
### Meglio evitare nomi ORM-style fuorvianti
Preferire:
- `execute(...)`
- `callProcedure(...)`

invece di:
- `save(...)`
- `findAll(...)`

quando il modulo resta proceduralmente centrato sulla stored procedure.

Se in una fase successiva si vuole esporre un dominio più business-friendly, allora si possono aggiungere facade semantiche sopra.

---

## 14. Testing

## 14.1 Tipi di test da prevedere

### A. Unit test puri
Per:
- normalizzazione campi
- validazione function code
- mapping errori

### B. Contract test modulo per modulo
Verificano equivalenza tra:
- input Java
- output procedura Oracle
- comportamento atteso dal legacy

### C. Golden test su casi reali
Per alcuni moduli campione, creare set di input/output attesi derivati da casi già noti.

### D. Smoke test di generazione
Verifica che ogni modulo generato:
- compili
- instanzi il repository
- registri correttamente la procedura

## 14.2 Migliore priorità test
Ordine migliore:
1. unit test su normalizer
2. contract test su procedura reale
3. regression pack per moduli campione
4. test end-to-end solo dopo

---

## 15. Performance e scalabilità

## Raccomandazioni
- pool JDBC HikariCP
- statement timeout configurabile
- fetch size solo se utile ai casi cursor-based
- no reflection pesante runtime
- no logging serializzato integrale dei payload su grandi volumi

## Casi cursor (`SF`, `SN`, `CC`)
Poiché alcuni codici funzione implicano semantica cursoriale, la scelta migliore è:

### fase 1
mantenere l’interfaccia aderente alla procedura legacy

### fase 2
valutare una astrazione più moderna (paginazione/stateless) solo dopo evidenza che la procedura lo consente

Quindi: **non modernizzare i cursori troppo presto**.

---

## 16. Sicurezza e compliance

- centralizzare le credenziali DB via secret manager / vault
- evitare hardcode in codice generato
- mascherare campi sensibili nei log
- usare auditing tecnico per modulo/procedura
- introdurre correlation id applicativo

---

## 17. Varie ed eventuali

## 17.1 Roadmap consigliata

### Fase 0 — Fondazioni
- definire modello canonico YAML/JSON
- definire core comune
- definire template generator

### Fase 1 — Pilot
- industrializzare `Individuale`
- industrializzare `EventoContratto`
- validare pattern, naming, test, packaging

### Fase 2 — Batch generation
- generare N moduli omogenei
- misurare effort, difetti, eccezioni al pattern

### Fase 3 — Hardening
- logging tecnico
- metriche
- retry policy solo se formalmente ammessa
- health check DB

### Fase 4 — Esposizione servizi
- facade applicative
- eventuale REST/OpenAPI
- rollout per domini funzionali

---

## 17.2 Governance del generatore
Il generatore deve essere trattato come prodotto software a sé:
- versionato
- testato
- con changelog
- con output deterministicamente riproducibile

Ogni modulo generato deve riportare in header:
- versione generatore
- timestamp generazione
- sorgente metadata
- checksum modello canonico

---

## 17.3 Eccezioni al pattern
Non tutti i moduli potranno essere 100% generabili nello stesso modo.

Prevedere una classificazione:
- **Classe A**: generazione completa
- **Classe B**: generazione con custom hook
- **Classe C**: implementazione manuale assistita

Questo evita di forzare nel template moduli anomali.

---

## 18. Decisioni finali raccomandate

## Decisione 1 — Paradigma
**Scegliere un layer procedural-repository e non ORM-centric.**

## Decisione 2 — Tecnologia DB
**Usare `spring-jdbc` con `CallableStatement` esplicita generata.**

## Decisione 3 — Industrializzazione
**Generare il codice da un modello canonico YAML/JSON.**

## Decisione 4 — Struttura
**Separare nettamente core comune, moduli generati e codice manuale.**

## Decisione 5 — Priorità
**Prima equivalenza funzionale con COBOL/PLSQL, poi eventuale API modernization.**

---

## 19. Template minimo di modulo generato

```java
public interface ModuleProcedureRepository<T extends ModuleRecord> {
    StoredProcedureCallResult<T> execute(
        CrudFunctionCode functionCode,
        IoParameters ioParameters,
        T record
    );
}
```

```java
@Service
public class IndividualeService {

    private final IndividualeRepository repository;
    private final FunctionCodeValidator functionCodeValidator;

    public IndividualeService(
            IndividualeRepository repository,
            FunctionCodeValidator functionCodeValidator) {
        this.repository = repository;
        this.functionCodeValidator = functionCodeValidator;
    }

    @Transactional
    public StoredProcedureCallResult<IndividualeRecord> execute(
            CrudFunctionCode functionCode,
            IoParameters ioParameters,
            IndividualeRecord record) {

        functionCodeValidator.validate(functionCode);
        return repository.execute(functionCode, ioParameters, record);
    }
}
```

---

## 20. Conclusione

Per il vostro contesto, la soluzione migliore è costruire un **framework interno di adapter generati**, in cui:

- ogni modulo COBOL viene tradotto in un **modulo Java tipizzato**
- il contratto resta **procedure-first**
- il comportamento COBOL critico viene preservato in modo esplicito
- la produzione delle classi è **industrializzabile**
- Spring Boot 3.x viene usato come contenitore applicativo e non come vincolo ORM

In sintesi:

> **migliore opzione = Spring Boot 3 + spring-jdbc + CallableStatement generata + metadata canonici + code generation deterministica**

È l’approccio più coerente con i moduli campione, il più scalabile sulle molte tabelle VPO e il più sicuro per sostituire progressivamente il COBOL senza alterare la semantica di accesso ai dati.
