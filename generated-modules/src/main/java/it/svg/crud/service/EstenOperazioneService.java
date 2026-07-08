package it.svg.crud.service;

import it.svg.crud.model.dto.CrudModuleResult;
import it.svg.crud.model.dto.IoParameters;
import it.svg.crud.model.dto.EstenOperazioneRecord;

public interface EstenOperazioneService {
    CrudModuleResult<EstenOperazioneRecord> execute(IoParameters ioParameters, EstenOperazioneRecord record);
}