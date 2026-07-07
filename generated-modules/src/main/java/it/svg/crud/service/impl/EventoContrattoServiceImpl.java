package it.svg.crud.service.impl;

import it.svg.crud.constants.EventoContrattoConstants;
import it.svg.crud.exception.ResourceNotFoundException;
import it.svg.crud.exception.UnsupportedCrudPatternException;
import it.svg.crud.model.dto.CrudModuleResult;
import it.svg.crud.model.dto.IoParameters;
import it.svg.crud.model.dto.EventoContrattoRecord;
import it.svg.crud.repository.EventoContrattoRepository;
import it.svg.crud.service.EventoContrattoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventoContrattoServiceImpl implements EventoContrattoService {

    private static final Logger log = LoggerFactory.getLogger(EventoContrattoServiceImpl.class);

    private final EventoContrattoRepository repository;

    public EventoContrattoServiceImpl(EventoContrattoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public CrudModuleResult<EventoContrattoRecord> execute(IoParameters ioParameters, EventoContrattoRecord record) {
        if (ioParameters.functionCode() == null
                || !EventoContrattoConstants.SUPPORTED_FUNCTION_CODES.contains(ioParameters.functionCode())) {
            throw new UnsupportedCrudPatternException(
                    "Function code not supported: [" + ioParameters.functionCode()
                    + "]. Supported codes for VPO04500: " + EventoContrattoConstants.SUPPORTED_FUNCTION_CODES);
        }

        MDC.put("module", EventoContrattoConstants.MODULE_NAME);
        MDC.put("functionCode", ioParameters.functionCode());
        try {
            log.info("Executing CRUD module [{}] functionCode=[{}]",
                    EventoContrattoConstants.MODULE_NAME, ioParameters.functionCode());

            CrudModuleResult<EventoContrattoRecord> result = repository.execute(ioParameters, record);

            Integer returnCode = result.ioParameters().returnCode();
            if (returnCode != null && returnCode == 1) {
                throw new ResourceNotFoundException(
                        EventoContrattoConstants.MODULE_NAME + ": record not found (returnCode=1)");
            }

            log.info("CRUD module [{}] completed successfully returnCode=[{}]",
                    EventoContrattoConstants.MODULE_NAME, returnCode);
            return result;
        } finally {
            MDC.remove("module");
            MDC.remove("functionCode");
        }
    }
}