package it.svg.crud.service;

import it.svg.crud.model.dto.CrudModuleResult;
import it.svg.crud.model.dto.IoParameters;
import it.svg.crud.model.dto.IndividualeRecord;

public interface IndividualeService {
    CrudModuleResult<IndividualeRecord> execute(IoParameters ioParameters, IndividualeRecord record);
}