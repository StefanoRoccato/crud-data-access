package it.svg.crud.service;

import it.svg.crud.model.dto.CrudModuleResult;
import it.svg.crud.model.dto.IoParameters;
import it.svg.crud.model.dto.EventoContrattoRecord;

public interface EventoContrattoService {
    CrudModuleResult<EventoContrattoRecord> execute(IoParameters ioParameters, EventoContrattoRecord record);
}