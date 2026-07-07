# VPO04500 metadata

- Owner: `VPO_CRUD`
- Callable name: `VPO04500`
- Target table: `EVENTO_CONTRATTO`
- Supported function codes: `SR, UR, IR, DR`
- Scope: only banal CRUD generation

## Arguments
| Position | Oracle argument | Java name | Java type | Numeric | Nullable | Mode | Normalization |
|---|---|---|---|---|---|---|---|
| 1 | IO_FUNCTION_CODE | ioFunctionCode | String | False | True | IN | NULL_OTHERWISE |
| 2 | IO_RETURN_CODE | ioReturnCode | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 3 | IO_ID_TIMESTAMP_INIZIO_VAL | ioIdTimestampInizioVal | Long | True | True | IN | NULL_OTHERWISE |
| 4 | IO_ID_TIMESTAMP_FINE_VAL | ioIdTimestampFineVal | Long | True | True | IN | NULL_OTHERWISE |
| 5 | IO_ID_DATA_INIZIO_VAL | ioIdDataInizioVal | String | False | True | IN | NULL_OTHERWISE |
| 6 | IO_ID_DATA_FINE_VAL | ioIdDataFineVal | String | False | True | IN | NULL_OTHERWISE |
| 7 | IO_ID_RIGA | ioIdRiga | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 8 | IO_FLAG_TROVATO | ioFlagTrovato | String | False | True | IN/OUT | NULL_OTHERWISE |
| 9 | IO_ID_LOCK | ioIdLock | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 10 | IO_FLAG1 | ioFlag1 | String | False | True | IN | NULL_OTHERWISE |
| 11 | IO_FLAG_AGGIORNA_DB | ioFlagAggiornaDb | Long | True | True | IN | NULL_OTHERWISE |
| 12 | IO_CONCURRENT_TEMP_UPDATE | ioConcurrentTempUpdate | String | False | True | IN/OUT | NULL_OTHERWISE |
| 13 | IO_FL_UPDATE | ioFlUpdate | String | False | True | IN | NULL_OTHERWISE |
| 14 | IO_TIPO_STORICITA | ioTipoStoricita | String | False | True | IN/OUT | NULL_OTHERWISE |
| 15 | IO_TIMESTAMP_APP | ioTimestampApp | Long | True | True | IN | NULL_OTHERWISE |
| 16 | S_SEQ_VARIAZIONE | sSeqVariazione | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 17 | S_SEQ_EVENTO_CONTRATTO | sSeqEventoContratto | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 18 | S_COD_STATO_EVENTO | sCodStatoEvento | String | False | True | IN/OUT | NULL_OTHERWISE |
| 19 | S_COD_EVENTO | sCodEvento | String | False | True | IN/OUT | NULL_OTHERWISE |
| 20 | S_CODICE_COMPAGNIA_PVG | sCodiceCompagniaPvg | String | False | True | IN/OUT | NULL_OTHERWISE |
| 21 | S_DATA_OPERAZIONE | sDataOperazione | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 22 | S_DATA_EFFETTO_EVENTO | sDataEffettoEvento | java.time.LocalDate | False | True | IN/OUT | NULL_OTHERWISE |
| 23 | S_TS_RIF_STATO | sTsRifStato | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 24 | S_SEQ_ELABORAZIONE | sSeqElaborazione | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 25 | S_SEQ_RAPPORTO | sSeqRapporto | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 26 | S_TIPO_RAPPORTO | sTipoRapporto | String | False | True | IN/OUT | NULL_OTHERWISE |
| 27 | S_ID_OPERATORE | sIdOperatore | String | False | True | IN/OUT | NULL_OTHERWISE |
| 28 | S_ID_TIMESTAMP_INIZIO_VAL | sIdTimestampInizioVal | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 29 | S_ID_FUNZIONE | sIdFunzione | String | False | True | IN/OUT | NULL_OTHERWISE |
| 30 | S_ID_OPERAZIONE | sIdOperazione | String | False | True | IN/OUT | NULL_OTHERWISE |
| 31 | S_ID_STATO_ELAB | sIdStatoElab | String | False | True | IN/OUT | NULL_OTHERWISE |
| 32 | S_ID_LOCK | sIdLock | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 33 | S_ID_COMMENTO | sIdCommento | String | False | True | IN/OUT | NULL_OTHERWISE |
| 34 | S_COD_MODAL_ELAB_INTERF | sCodModalElabInterf | String | False | True | IN/OUT | NULL_OTHERWISE |
| 35 | S_COD_DISPVO | sCodDispvo | String | False | True | IN/OUT | NULL_OTHERWISE |
