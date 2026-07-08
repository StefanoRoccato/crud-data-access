# VIAT4200 metadata

- Owner: `VIA_CRUD`
- Callable name: `VIAT4200`
- Target table: `ESTEN_OPERAZIONE_S`
- Generation strategy: `SELECT`
- Supported function codes: `SR, UR, IR, DR`
- Scope: only banal CRUD generation


## Select fallback

- Select owner: `VIA`
- Short name: `VIAT42`
- Select table: `ESTEN_OPERAZIONE`
- Select SQL: `SELECT FLAG_CLI, FLAG_CTR, FLAG_GAR, FLAG_TRN, FLAG_OPZ, COD_OPERAZIONE, CODICE_COMPAGNIA_PVG, ID_OPERATORE, ID_TIMESTAMP_INIZIO_VAL, ID_FUNZIONE, ID_OPERAZIONE, ID_STATO_ELAB, ID_LOCK, ID_COMMENTO, FLAG_PRENOTAZIONE, DENOM_DMN_ALGO_PREN, FLAG_ABIL_STM_RICH, FLAG_CIRCOLARITA, COD_UNITA_MIS_GIORNI, FLAG_ABIL_STM_RVL_PRVS, FLAG_ESEC_DIFF, FLAG_COLLETTIVO, FLAG_POLIZZA, FLAG_POSIZIONE FROM ESTEN_OPERAZIONE WHERE COD_OPERAZIONE = ? AND CODICE_COMPAGNIA_PVG = ?`
- Condition columns: `COD_OPERAZIONE, CODICE_COMPAGNIA_PVG`


## Arguments
| Position | Oracle argument | Java name | Java type | Numeric | Nullable | Mode | Normalization |
|---|---|---|---|---|---|---|---|
| 1 | FLAG_CLI | flagCli | String | False | True | OUT | NULL_OTHERWISE |
| 2 | FLAG_CTR | flagCtr | String | False | True | OUT | NULL_OTHERWISE |
| 3 | FLAG_GAR | flagGar | String | False | True | OUT | NULL_OTHERWISE |
| 4 | FLAG_TRN | flagTrn | String | False | True | OUT | NULL_OTHERWISE |
| 5 | FLAG_OPZ | flagOpz | String | False | True | OUT | NULL_OTHERWISE |
| 6 | COD_OPERAZIONE | codOperazione | String | False | False | IN | NULL_OTHERWISE |
| 7 | CODICE_COMPAGNIA_PVG | codiceCompagniaPvg | String | False | False | IN | NULL_OTHERWISE |
| 8 | ID_OPERATORE | idOperatore | String | False | False | OUT | NULL_OTHERWISE |
| 9 | ID_TIMESTAMP_INIZIO_VAL | idTimestampInizioVal | Long | True | False | OUT | ZERO_IF_NOT_NULLABLE_NUMERIC |
| 10 | ID_FUNZIONE | idFunzione | String | False | False | OUT | NULL_OTHERWISE |
| 11 | ID_OPERAZIONE | idOperazione | String | False | False | OUT | NULL_OTHERWISE |
| 12 | ID_STATO_ELAB | idStatoElab | String | False | True | OUT | NULL_OTHERWISE |
| 13 | ID_LOCK | idLock | Long | True | False | OUT | ZERO_IF_NOT_NULLABLE_NUMERIC |
| 14 | ID_COMMENTO | idCommento | String | False | True | OUT | NULL_OTHERWISE |
| 15 | FLAG_PRENOTAZIONE | flagPrenotazione | String | False | True | OUT | NULL_OTHERWISE |
| 16 | DENOM_DMN_ALGO_PREN | denomDmnAlgoPren | String | False | True | OUT | NULL_OTHERWISE |
| 17 | FLAG_ABIL_STM_RICH | flagAbilStmRich | String | False | True | OUT | NULL_OTHERWISE |
| 18 | FLAG_CIRCOLARITA | flagCircolarita | String | False | True | OUT | NULL_OTHERWISE |
| 19 | COD_UNITA_MIS_GIORNI | codUnitaMisGiorni | String | False | True | OUT | NULL_OTHERWISE |
| 20 | FLAG_ABIL_STM_RVL_PRVS | flagAbilStmRvlPrvs | String | False | True | OUT | NULL_OTHERWISE |
| 21 | FLAG_ESEC_DIFF | flagEsecDiff | String | False | True | OUT | NULL_OTHERWISE |
| 22 | FLAG_COLLETTIVO | flagCollettivo | String | False | True | OUT | NULL_OTHERWISE |
| 23 | FLAG_POLIZZA | flagPolizza | String | False | True | OUT | NULL_OTHERWISE |
| 24 | FLAG_POSIZIONE | flagPosizione | String | False | True | OUT | NULL_OTHERWISE |
