package it.svg.crud.model.dto;

public record CrudModuleResult<T>(
        IoParameters ioParameters,
        T record,
        String procedureName,
        String tableName,
        long elapsedMillis
) {}
