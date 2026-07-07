# Descrizione completa del progetto — CRUD Data Access Generator

## 1. Obiettivo del progetto

Il progetto ha l'obiettivo di costruire un **generatore automatico** che produca il **core backend Java di accesso ai dati** per le CRUD COBOL/Oracle, mantenendo il vincolo architetturale fondamentale: l'accesso al dato deve continuare ad avvenire tramite **stored procedure PL/SQL omonime al modulo COBOL**. Nei moduli campione analizzati (`VPO01100` e `VPO04500`) il COBOL si comporta infatti come una **CRUD shell** che non contiene SQL diretto ma delega completamente alla procedura Oracle, preservando un contratto basato su `IO-PARAMETERS`, record tabellare e codici di ritorno applicativi/Oracle. citeturn1search3turn1search1turn1search2

Il risultato atteso **non** è un microservizio completo, ma un insieme di classi Java riusabili da parte di microservizi o servizi superiori, con focus esclusivo sul **data access layer**. Questa scelta è coerente con quanto definito nel file degli standard Java allegato, dove è presente una struttura layered da microservizio, ma nel tuo caso viene volontariamente adottata solo nella porzione necessaria al backend di accesso dati. citeturn10search9turn1search2

---

## 2. Perimetro funzionale del generatore

Il generatore è volutamente limitato ai **casi banali**, come da assunzioni operative emerse nel confronto:

- solo **procedure standalone**
- **no package**
- **no cursor**
- **no collection**
- **no object type Oracle complessi** citeturn9file11

Questo significa che il generatore è progettato per i moduli CRUD elementari che espongono operazioni equivalenti a `SR`, `UR`, `IR`, `DR` e che seguono sempre lo stesso schema generale di funzionamento. In questo perimetro non viene quindi gestita la complessità delle procedure packaged, dei cursori di fetch, delle collezioni Oracle o di strutture PL/SQL non scalari. citeturn9file11turn1search2

---

## 3. Regole DB acquisite

Il progetto si basa sulle seguenti regole deterministicamente definite:

### 3.1 Connessione DB
La connessione Oracle è stata chiarita e fissata nei seguenti parametri:

- `dbUser = pvgdf`
- `dbPwd = pvgdf`
- `connName = CSDS1.generali.it`
- `dbTns = (DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=dbCSDS1-scan.generali.it)(PORT=1521)))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=CSDS1_CSD_DEFAULT.generali.it)))` citeturn5file89

La modalità di riferimento è compatibile con il pattern PowerShell/SQLcl:

```powershell
& $sqlcl "$dbUser/$dbPwd@$dbTns" "@$sqlTmp"
```

che è stata anche trasposta sia al Python, sia al Java JDBC. citeturn4file87turn5file89

### 3.2 Privilegi metadata disponibili
Sono disponibili i privilegi su:

- `ALL_PROCEDURES`
- `ALL_ARGUMENTS` citeturn6file91

Inoltre la rigenerazione finale assume l'uso anche di `ALL_SOURCE` e `ALL_TAB_COLUMNS`, perché la tabella target e la nullability vengono derivate da lì in base alle tue regole operative. La regola applicativa è stata esplicitamente fornita dall'utente nel corso della conversazione. citeturn7file9turn13file10

### 3.3 Regola owner/schema Oracle
Lo schema Oracle del modulo viene derivato automaticamente dai **primi 3 caratteri del nome modulo**. Ad esempio:

- `VPO01100 -> VPO` citeturn6file91

### 3.4 Regola modulo → tabella
La tabella target del modulo viene ricavata usando una query su `ALL_SOURCE` con owner `VPO_CRUD`:

```sql
select substr(text,8,length(text)-20) as tabella
from ALL_SOURCE
where owner = 'VPO_CRUD'
  and name = '<MODULO>'
  and text like 'TYPE%'
order by line
```

Questa regola è stata assunta come standard del generatore. citeturn7file9

### 3.5 Nullability e mapping campo → colonna
Il mapping tra argomento Oracle e colonna della tabella è **1:1 per nome**, quindi il generatore usa `ALL_TAB_COLUMNS` per verificare se la colonna sia nullable o meno e applicare poi la normalizzazione di default. Questa informazione è stata fornita direttamente nel dialogo con l'utente e incorporata nel bundle finale. citeturn13file10

---

## 4. Contratto applicativo CRUD

### 4.1 `IO-PARAMETERS`
`IO-PARAMETERS` è stato dichiarato **standard e comune a tutte le CRUD**. Per il generatore, il master di riferimento è il tracciato ricostruito sul modulo `VPO01100`, documentato nel reverse delle specifiche di servizio. Lì risultano campi come `IO-FUNCTION-CODE`, `IO-RETURN-CODE`, `IO-FLAG-AGGIORNA-DB`, `IO-FLAG1`, `IO-TIPO-STORICITA`, `IO-FLAG-TROVATO`, `IO-ID-TIMESTAMP-INIZIO-VAL`, `IO-ID-RIGA`, `IO-ID-LOCK`, `IO-SQLERRMC`, `IO-LIV-LOG`, `IO-SESSION-ID`, ecc. citeturn1search2

Nel bundle finale questo contratto è rappresentato da un DTO Java `IoParameters`. citeturn13file10

### 4.2 Function code supportati
Per il generatore banale il set dei codici funzione è limitato a 4 operazioni:

- `SR` = select single row
- `UR` = update
- `IR` = insert
- `DR` = delete citeturn9file11

Questa scelta riduce drasticamente la complessità e riflette i casi realmente usati nelle CRUD semplici che vuoi coprire nella prima fase. citeturn9file11turn1search2

### 4.3 Regola di normalizzazione
La regola generale fissata per i casi banali è:

- campi numerici **not nullable** → `0`
- tutti gli altri campi → `null` citeturn9file11

Come hai specificato, questi valori sono **segnaposto tecnici** che vengono poi coperti dai valori effettivamente letti dal DB o da scrivere al DB. Il generatore applica quindi una normalizzazione semplice ma coerente con il comportamento atteso per le CRUD banali. citeturn9file11

### 4.4 Return code standard
La tabella dei codici di ritorno valida per tutte le CRUD comprende, tra gli altri:

- `0` → operazione riuscita
- `+1` → record non trovato
- `+4` → warning generico
- `+16` → errore grave / funzione non valida
- `+90019` → funzione non riconosciuta
- `+90021` → errore chiave
- `+90022` → aggiornamento concorrente
- `+90049` → ID riga a zero non ammesso
- `+90050` → nessuna modifica DB (dry run)
- `+90051` → nessun record aggiornato
- `-1 / -20101` → record già esistente
- `-2291 / -20008 / -20110` → parent key non trovata
- `-2292 / -20009` → child record trovato
- `< 0` → errore SQL Oracle generico citeturn1search2

Questa tabella è stata recepita nel progetto finale sia in documentazione sia in una classe constants Java. citeturn13file10

---

## 5. Struttura software generata

### 5.1 Scelta architetturale
In base agli standard Java allegati, un microservizio completo dovrebbe comprendere `controller`, `service`, `service.impl`, `repository`, `model.entity`, `model.dto`, `mapper`, `exception`, `config`, `util`, `constants`, test e OpenAPI. Tuttavia, nel progetto corrente viene adottata **solo la porzione necessaria al core backend**. Gli standard usati come riferimento indicano infatti un’architettura layered, constructor injection, records per DTO, logging, validation e test, ma non obbligano ad avere per forza un controller se il componente non è un microservizio esposto direttamente. citeturn10search9

Per questo il bundle finale genera:

- `config`
- `constants`
- `model.dto`
- `repository`
- `service`
- `service.impl`
- `exception`
- `util`
- documentazione e metadata del modulo. citeturn10search9turn13file10

### 5.2 Package base
Su tua indicazione, il package base reale usato dappertutto è:

```text
it.svg.crud
```

ed è stato adottato il termine `crud` al posto di `vpo` dove possibile. Questa scelta è riportata anche nel riepilogo della rigenerazione finale. citeturn13file10

### 5.3 Tipi di file generati
Per ciascun modulo il generatore Python produce:

- DTO record specifico del modulo
- repository procedural-first Oracle
- service interface
- service implementation
- metadata `.md`
- metadata `.json` del modulo citeturn13file10

La soluzione finale non genera entity JPA né controller REST perché non sono coerenti con lo scopo di questo componente. citeturn10search9turn13file10

---

## 6. Stack tecnologico

### 6.1 Java / Maven
Il progetto finale è predisposto per:

- Java 21
- Spring Boot 3.x
- Maven
- Oracle JDBC
- Spring JDBC
- Validation
- SLF4J / Logback
- JUnit 5 / Testcontainers predisposti nel `pom.xml` citeturn10search9turn13file10

### 6.2 Python generator
Il generatore Python supporta due modalità:

- **direct**: usa `oracledb.connect(...)`
- **sqlcl**: compatibile con lo script PowerShell e con SQLcl, usando script `.sql` temporanei e parsing CSV. citeturn4file87turn13file10

---

## 7. Funzionamento del generatore Python

Il generatore Python (`tools/generate_crud_module.py`) esegue questi passi:

1. deriva l’owner Oracle dai primi 3 caratteri del modulo
2. cerca la procedura su `ALL_PROCEDURES`
3. verifica che non sia un package (in quel caso fallisce, perché fuori perimetro)
4. recupera la tabella target tramite `ALL_SOURCE` owner `VPO_CRUD`
5. recupera gli argomenti via `ALL_ARGUMENTS`
6. recupera la nullability colonne via `ALL_TAB_COLUMNS`
7. applica il mapping 1:1 argomento → colonna
8. applica la normalizzazione:
   - `0` per numerici non nullable
   - `null` per gli altri
9. genera il codice Java e i metadata del modulo. citeturn13file10

Il generatore è pensato per essere lanciato da te manualmente, come hai chiarito nel dialogo. citeturn13file10

---

## 8. File prodotti nel bundle finale

Il bundle finale contiene:

- `README.md`
- documentazione completa in `docs/`
- `pom.xml`
- `application.yml` e file esempio local
- scaffold Java in `src/main/java/it/svg/crud/...`
- templates Jinja2 per la generazione modulo-specifica
- script Python `generate_crud_module.py`
- esempi di lancio shell / PowerShell
- esempio override YAML (`VPO01100`) citeturn13file10

---

## 9. Limiti dichiarati del progetto

Il progetto, nello stato finale attuale, **non** copre:

- package Oracle
- cursor
- collection
- object type complessi
- controller REST
- JPA / Hibernate entity generation
- logiche business
- contract test automatici completi con casi reali
- mapping avanzato dei codici di ritorno in eccezioni business dettagliate. citeturn10search9turn13file10

Questi limiti sono intenzionali e coerenti con il perimetro ridotto del generatore banale. citeturn9file11turn13file10

---

## 10. Evoluzioni possibili

Le evoluzioni naturali del progetto sono:

1. aggiungere supporto ai package
2. aggiungere supporto a cursor/collection
3. generare un `IO-PARAMETERS` ancora più tipizzato/dettagliato se necessario
4. aggiungere mapping errori più raffinato basato sui return code standard
5. produrre test automatici di confronto contro casi reali su moduli campione
6. integrare il core backend generato dentro microservizi completi. citeturn10search9turn13file10

---

## 11. Stato finale

Il progetto è stato rigenerato nella sua forma finale come:

- **Java**
- **Python**
- **Markdown di progetto / definizioni / assunzioni / return code / connessione / struttura**

con bundle finale scaricabile già pronto. Questo stato è riportato anche nel file di riepilogo finale incluso nel bundle stesso. citeturn13file10
