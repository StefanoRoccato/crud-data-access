# VPO01100 metadata

- Owner: `VPO_CRUD`
- Callable name: `VPO01100`
- Target table: `INDIVIDUALE_S`
- Generation strategy: `PROCEDURE`
- Supported function codes: `SR, UR, IR, DR`
- Scope: only banal CRUD generation



## Arguments
| Position | Oracle argument | Java name | Java type | Numeric | Nullable | Mode | Normalization |
|---|---|---|---|---|---|---|---|
| 1 | IO_FUNCTION_CODE | ioFunctionCode | String | False | True | IN | NULL_OTHERWISE |
| 2 | IO_RETURN_CODE | ioReturnCode | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 3 | IO_ID_TIMESTAMP_INIZIO_VAL | ioIdTimestampInizioVal | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 4 | IO_ID_TIMESTAMP_FINE_VAL | ioIdTimestampFineVal | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 5 | IO_ID_DATA_INIZIO_VAL | ioIdDataInizioVal | String | False | True | IN/OUT | NULL_OTHERWISE |
| 6 | IO_ID_DATA_FINE_VAL | ioIdDataFineVal | String | False | True | IN/OUT | NULL_OTHERWISE |
| 7 | IO_DATA_CONT | ioDataCont | String | False | True | IN/OUT | NULL_OTHERWISE |
| 8 | IO_ID_RIGA | ioIdRiga | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 9 | IO_FLAG_TROVATO | ioFlagTrovato | String | False | True | IN/OUT | NULL_OTHERWISE |
| 10 | IO_ID_LOCK | ioIdLock | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 11 | IO_FLAG1 | ioFlag1 | String | False | True | IN | NULL_OTHERWISE |
| 12 | IO_FLAG_AGGIORNA_DB | ioFlagAggiornaDb | Long | True | True | IN | NULL_OTHERWISE |
| 13 | IO_CONCURRENT_TEMP_UPDATE | ioConcurrentTempUpdate | String | False | True | IN/OUT | NULL_OTHERWISE |
| 14 | IO_FL_UPDATE | ioFlUpdate | String | False | True | IN | NULL_OTHERWISE |
| 15 | IO_TIPO_STORICITA | ioTipoStoricita | String | False | True | IN/OUT | NULL_OTHERWISE |
| 16 | S_SEQ_EVENTO_CONTRATTO | sSeqEventoContratto | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 17 | S_TIPO_RIF_ESTERNO | sTipoRifEsterno | String | False | True | IN/OUT | NULL_OTHERWISE |
| 18 | S_COD_RIF_ESTERNO | sCodRifEsterno | String | False | True | IN/OUT | NULL_OTHERWISE |
| 19 | S_COD_SISTEMA_ESTERNO | sCodSistemaEsterno | String | False | True | IN/OUT | NULL_OTHERWISE |
| 20 | S_COD_RIF_SIST_ESTERNO | sCodRifSistEsterno | String | False | True | IN/OUT | NULL_OTHERWISE |
| 21 | S_COD_REGIME_FISCALE | sCodRegimeFiscale | String | False | True | IN/OUT | NULL_OTHERWISE |
| 22 | S_FLAG_DETRAIBILITA | sFlagDetraibilita | String | False | True | IN/OUT | NULL_OTHERWISE |
| 23 | S_FLAG_RIN_PRESTITI | sFlagRinPrestiti | String | False | True | IN/OUT | NULL_OTHERWISE |
| 24 | S_DATA_PERFEZIONAMENTO | sDataPerfezionamento | java.time.LocalDate | False | True | IN/OUT | NULL_OTHERWISE |
| 25 | S_COD_RAPPORTO | sCodRapporto | String | False | True | IN/OUT | NULL_OTHERWISE |
| 26 | S_COD_STATO_CONTRATTO | sCodStatoContratto | String | False | True | IN/OUT | NULL_OTHERWISE |
| 27 | S_DATA_DECORRENZA | sDataDecorrenza | java.time.LocalDate | False | True | IN/OUT | NULL_OTHERWISE |
| 28 | S_DATA_SCADENZA | sDataScadenza | java.time.LocalDate | False | True | IN/OUT | NULL_OTHERWISE |
| 29 | S_COD_COASSICURAZIONE | sCodCoassicurazione | String | False | True | IN/OUT | NULL_OTHERWISE |
| 30 | S_FLAG_VISITA_MEDICA | sFlagVisitaMedica | String | False | True | IN/OUT | NULL_OTHERWISE |
| 31 | S_COD_COMPTO_TABAG | sCodComptoTabag | String | False | True | IN/OUT | NULL_OTHERWISE |
| 32 | S_FLAG_OPZIONE_RENDITA | sFlagOpzioneRendita | String | False | True | IN/OUT | NULL_OTHERWISE |
| 33 | S_FLAG_TRASFORMAZIONE | sFlagTrasformazione | String | False | True | IN/OUT | NULL_OTHERWISE |
| 34 | S_NUM_MESI_RESID_RISC | sNumMesiResidRisc | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 35 | S_IMP_IMPON_PROVV_TRASF | sImpImponProvvTrasf | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 36 | S_CODICE_RAMO | sCodiceRamo | String | False | True | IN/OUT | NULL_OTHERWISE |
| 37 | S_SEQ_CONV | sSeqConv | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 38 | S_SEQ_RAPPORTO | sSeqRapporto | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 39 | S_TIPO_INDIVIDUALE | sTipoIndividuale | String | False | True | IN/OUT | NULL_OTHERWISE |
| 40 | S_CODICE_COMPAGNIA_PVG | sCodiceCompagniaPvg | String | False | True | IN/OUT | NULL_OTHERWISE |
| 41 | S_ID_OPERATORE | sIdOperatore | String | False | True | IN/OUT | NULL_OTHERWISE |
| 42 | S_ID_TIMESTAMP_INIZIO_VAL | sIdTimestampInizioVal | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 43 | S_ID_FUNZIONE | sIdFunzione | String | False | True | IN/OUT | NULL_OTHERWISE |
| 44 | S_ID_OPERAZIONE | sIdOperazione | String | False | True | IN/OUT | NULL_OTHERWISE |
| 45 | S_ID_STATO_ELAB | sIdStatoElab | String | False | True | IN/OUT | NULL_OTHERWISE |
| 46 | S_ID_LOCK | sIdLock | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 47 | S_ID_COMMENTO | sIdCommento | String | False | True | IN/OUT | NULL_OTHERWISE |
| 48 | S_NUM_MESI_PROR | sNumMesiPror | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 49 | S_DATA_RIPR_PAG_PREMI | sDataRiprPagPremi | java.time.LocalDate | False | True | IN/OUT | NULL_OTHERWISE |
| 50 | S_IMP_PRESTAZ_MAX_FISCV | sImpPrestazMaxFiscv | Long | True | True | IN/OUT | NULL_OTHERWISE |
| 51 | S_TIPO_INDIV_LEGALE | sTipoIndivLegale | String | False | True | IN/OUT | NULL_OTHERWISE |
| 52 | S_CODICE_SISTEMA_PROVEN | sCodiceSistemaProven | String | False | True | IN/OUT | NULL_OTHERWISE |
| 53 | S_TIPO_ORIG_EMISS | sTipoOrigEmiss | String | False | True | IN/OUT | NULL_OTHERWISE |
| 54 | S_NUM_ANNO_COORTE | sNumAnnoCoorte | Long | True | True | IN/OUT | NULL_OTHERWISE |
