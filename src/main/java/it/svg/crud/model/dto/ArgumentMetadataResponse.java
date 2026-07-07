package it.svg.crud.model.dto;

public record ArgumentMetadataResponse(
        String oracleArgumentName,
        String javaName,
        String javaType,
        boolean numeric,
        boolean nullable,
        int position,
        String parameterMode,
        String normalizationPolicy
) {}
