package it.svg.crud.service.impl;

import it.svg.crud.constants.IndividualeConstants;
import it.svg.crud.exception.ResourceNotFoundException;
import it.svg.crud.exception.UnsupportedCrudPatternException;
import it.svg.crud.model.dto.CrudModuleResult;
import it.svg.crud.model.dto.IoParameters;
import it.svg.crud.model.dto.IndividualeRecord;
import it.svg.crud.repository.IndividualeRepository;
import it.svg.crud.service.IndividualeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IndividualeServiceImpl implements IndividualeService {

    private static final Logger log = LoggerFactory.getLogger(IndividualeServiceImpl.class);

    private final IndividualeRepository repository;

    public IndividualeServiceImpl(IndividualeRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public CrudModuleResult<IndividualeRecord> execute(IoParameters ioParameters, IndividualeRecord record) {
        if (ioParameters.functionCode() == null
                || !IndividualeConstants.SUPPORTED_FUNCTION_CODES.contains(ioParameters.functionCode())) {
            throw new UnsupportedCrudPatternException(
                    "Function code not supported: [" + ioParameters.functionCode()
                    + "]. Supported codes for VPO01100: " + IndividualeConstants.SUPPORTED_FUNCTION_CODES);
        }

        MDC.put("module", IndividualeConstants.MODULE_NAME);
        MDC.put("functionCode", ioParameters.functionCode());
        try {
            log.info("Executing CRUD module [{}] functionCode=[{}]",
                    IndividualeConstants.MODULE_NAME, ioParameters.functionCode());

            CrudModuleResult<IndividualeRecord> result = repository.execute(ioParameters, record);

            Integer returnCode = result.ioParameters().returnCode();
            if (returnCode != null && returnCode == 1) {
                throw new ResourceNotFoundException(
                        IndividualeConstants.MODULE_NAME + ": record not found (returnCode=1)");
            }

            log.info("CRUD module [{}] completed successfully returnCode=[{}]",
                    IndividualeConstants.MODULE_NAME, returnCode);
            return result;
        } finally {
            MDC.remove("module");
            MDC.remove("functionCode");
        }
    }
}