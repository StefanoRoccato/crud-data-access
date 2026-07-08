package it.svg.crud.service.impl;

import it.svg.crud.constants.EstenOperazioneConstants;
import it.svg.crud.exception.ResourceNotFoundException;
import it.svg.crud.exception.UnsupportedCrudPatternException;
import it.svg.crud.model.dto.CrudModuleResult;
import it.svg.crud.model.dto.IoParameters;
import it.svg.crud.model.dto.EstenOperazioneRecord;
import it.svg.crud.repository.EstenOperazioneRepository;
import it.svg.crud.service.EstenOperazioneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstenOperazioneServiceImpl implements EstenOperazioneService {

    private static final Logger log = LoggerFactory.getLogger(EstenOperazioneServiceImpl.class);

    private final EstenOperazioneRepository repository;

    public EstenOperazioneServiceImpl(EstenOperazioneRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public CrudModuleResult<EstenOperazioneRecord> execute(IoParameters ioParameters, EstenOperazioneRecord record) {
        if (ioParameters.functionCode() == null
                || !EstenOperazioneConstants.SUPPORTED_FUNCTION_CODES.contains(ioParameters.functionCode())) {
            throw new UnsupportedCrudPatternException(
                    "Function code not supported: [" + ioParameters.functionCode()
                    + "]. Supported codes for VIAT4200: " + EstenOperazioneConstants.SUPPORTED_FUNCTION_CODES);
        }

        MDC.put("module", EstenOperazioneConstants.MODULE_NAME);
        MDC.put("functionCode", ioParameters.functionCode());
        try {
            log.info("Executing CRUD module [{}] functionCode=[{}]",
                    EstenOperazioneConstants.MODULE_NAME, ioParameters.functionCode());

            CrudModuleResult<EstenOperazioneRecord> result = repository.execute(ioParameters, record);

            Integer returnCode = result.ioParameters().returnCode();
            if (returnCode != null && returnCode == 1) {
                throw new ResourceNotFoundException(
                        EstenOperazioneConstants.MODULE_NAME + ": record not found (returnCode=1)");
            }

            log.info("CRUD module [{}] completed successfully returnCode=[{}]",
                    EstenOperazioneConstants.MODULE_NAME, returnCode);
            return result;
        } finally {
            MDC.remove("module");
            MDC.remove("functionCode");
        }
    }
}