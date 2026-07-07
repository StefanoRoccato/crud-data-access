package it.svg.crud.service.impl;

import it.svg.crud.exception.UnsupportedCrudPatternException;
import it.svg.crud.model.dto.ArgumentMetadataResponse;
import it.svg.crud.service.CrudMetadataService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrudMetadataServiceImpl implements CrudMetadataService {
    @Override
    public List<ArgumentMetadataResponse> getArgumentsForModule(String moduleName) {
        throw new UnsupportedCrudPatternException(
                "getArgumentsForModule not available at runtime — use the Python generator to introspect module: " + moduleName);
    }

    @Override
    public String getTargetTableForModule(String moduleName) {
        throw new UnsupportedCrudPatternException(
                "getTargetTableForModule not available at runtime — use the Python generator to introspect module: " + moduleName);
    }
}
