# Return code standard CRUD

| Codice | 88-level | Significato |
|---|---|---|
| 0 | IO-SUCCESSFUL-RC | Operazione riuscita |
| +1 | IO-ERROR-NOT-FOUND | Record non trovato |
| +4 | IO-WARNING-RC | Warning generico |
| +16 | IO-SEVERE-RC | Errore grave (funzione non valida) |
| +90019 | IO-INVALID-FUNCTION | Codice funzione non riconosciuto |
| +90021 | IO-ERR-KEY | Errore chiave |
| +90022 | IO-CONCURRENT-UPDATE-RC | Aggiornamento concorrente |
| +90049 | IO-ID-RIGA-A-ZERO | ID riga a zero non ammesso |
| +90050 | IO-NO-MODIFY-DB | Nessuna modifica DB (dry run) |
| +90051 | IO-NO-UPDATE-FOUND | Nessun record aggiornato |
| -1 / -20101 | IO-ROW-ALREADY-EXISTS-RC | Record già esistente |
| -2291 / -20008 / -20110 | IO-SQL-PARENT-KEY-NOT-FOUND | FK violation (parent not found) |
| -2292 / -20009 | IO-SQL-CHILD-RECORD-FOUND | FK violation (child found) |
| < 0 | IO-SQL-ERROR-RC | Errore SQL Oracle generico |
