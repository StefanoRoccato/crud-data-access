package it.svg.crud.model.dto;

public record IoParameters(
        String functionCode,
        Integer returnCode,
        Integer flagAggiornaDb,
        String flag1,
        String tipoStoricita,
        String flagTrovato,
        Long idTimestampInizioVal,
        Long idTimestampFineVal,
        String idDataInizioVal,
        String idDataFineVal,
        String dataCont,
        Long idRiga,
        Long idLock,
        String flUpdate,
        String concurrentTempUpdate,
        Long timestampApp,
        String sqlerrmc,
        Integer livLog,
        String sessionId
) {}
