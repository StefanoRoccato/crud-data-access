package it.svg.crud.model.dto;

public record EstenOperazioneRecord(
        String flagCli, 
        String flagCtr, 
        String flagGar, 
        String flagTrn, 
        String flagOpz, 
        String codOperazione, 
        String codiceCompagniaPvg, 
        String idOperatore, 
        Long idTimestampInizioVal, 
        String idFunzione, 
        String idOperazione, 
        String idStatoElab, 
        Long idLock, 
        String idCommento, 
        String flagPrenotazione, 
        String denomDmnAlgoPren, 
        String flagAbilStmRich, 
        String flagCircolarita, 
        String codUnitaMisGiorni, 
        String flagAbilStmRvlPrvs, 
        String flagEsecDiff, 
        String flagCollettivo, 
        String flagPolizza, 
        String flagPosizione
) {}