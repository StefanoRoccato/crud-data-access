package it.svg.crud.service;

import it.svg.crud.model.dto.ArgumentMetadataResponse;

import java.util.List;

public interface CrudMetadataService {
    List<ArgumentMetadataResponse> getArgumentsForModule(String moduleName);
    String getTargetTableForModule(String moduleName);
}
