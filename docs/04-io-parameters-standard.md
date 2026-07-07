# IO-PARAMETERS standard

Master unico per tutte le CRUD, derivato dal tracciato usato su `VPO01100`.

Campi principali gestiti nello scaffold:
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

Nota: lo scaffold include il DTO completo; la logica funzionale resta demandata ai microservizi che useranno il core backend.
