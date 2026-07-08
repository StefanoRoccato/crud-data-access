package it.svg.crud.service.impl;

import it.svg.crud.constants.EstenOperazioneConstants;
import it.svg.crud.exception.ResourceNotFoundException;
import it.svg.crud.exception.UnsupportedCrudPatternException;
import it.svg.crud.model.dto.CrudModuleResult;
import it.svg.crud.model.dto.IoParameters;
import it.svg.crud.model.dto.EstenOperazioneRecord;
import it.svg.crud.repository.EstenOperazioneRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstenOperazioneServiceImplTest {

    @Mock
    private EstenOperazioneRepository repository;

    @InjectMocks
    private EstenOperazioneServiceImpl service;

    @Test
    @DisplayName("execute - happy path SR returns result")
    void execute_happyPath_returnsResult() {
        IoParameters ioParams = buildIoParameters("SR", 0);
        EstenOperazioneRecord record = buildRecord();
        CrudModuleResult<EstenOperazioneRecord> expected =
                new CrudModuleResult<>(ioParams, record,
                        EstenOperazioneConstants.MODULE_NAME, EstenOperazioneConstants.TARGET_TABLE, 10L);
        when(repository.execute(any(), any())).thenReturn(expected);

        CrudModuleResult<EstenOperazioneRecord> result = service.execute(ioParams, record);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("execute - returnCode 1 throws ResourceNotFoundException")
    void execute_returnCode1_throwsResourceNotFoundException() {
        IoParameters ioParams = buildIoParameters("SR", 1);
        EstenOperazioneRecord record = buildRecord();
        CrudModuleResult<EstenOperazioneRecord> notFoundResult =
                new CrudModuleResult<>(ioParams, record,
                        EstenOperazioneConstants.MODULE_NAME, EstenOperazioneConstants.TARGET_TABLE, 10L);
        when(repository.execute(any(), any())).thenReturn(notFoundResult);

        assertThatThrownBy(() -> service.execute(ioParams, record))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(EstenOperazioneConstants.MODULE_NAME);
    }

    @Test
    @DisplayName("execute - unsupported function code throws UnsupportedCrudPatternException")
    void execute_unsupportedFunctionCode_throwsUnsupportedCrudPatternException() {
        IoParameters ioParams = buildIoParameters("XX", null);
        EstenOperazioneRecord record = buildRecord();

        assertThatThrownBy(() -> service.execute(ioParams, record))
                .isInstanceOf(UnsupportedCrudPatternException.class)
                .hasMessageContaining("XX");
    }

    @Test
    @DisplayName("execute - null function code throws UnsupportedCrudPatternException")
    void execute_nullFunctionCode_throwsUnsupportedCrudPatternException() {
        IoParameters ioParams = buildIoParameters(null, null);
        EstenOperazioneRecord record = buildRecord();

        assertThatThrownBy(() -> service.execute(ioParams, record))
                .isInstanceOf(UnsupportedCrudPatternException.class);
    }

    // ---- builders ----

    private static IoParameters buildIoParameters(String functionCode, Integer returnCode) {
        // IoParameters: functionCode, returnCode, flagAggiornaDb, flag1, tipoStoricita, flagTrovato,
        // idTimestampInizioVal, idTimestampFineVal, idDataInizioVal, idDataFineVal, dataCont,
        // idRiga, idLock, flUpdate, concurrentTempUpdate, timestampApp, sqlerrmc, livLog, sessionId
        return new IoParameters(
                functionCode, returnCode,
                null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null
        );
    }

    private static EstenOperazioneRecord buildRecord() {
        // TODO: populate with representative test values for VIAT4200
        return new EstenOperazioneRecord(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}