package it.svg.crud.util;

public final class OracleTypeMapper {
    private OracleTypeMapper() {}

    public static String toJavaType(String oracleType, Integer precision, Integer scale) {
        if (oracleType == null) return "String";
        return switch (oracleType.toUpperCase()) {
            case "VARCHAR2", "CHAR", "NVARCHAR2", "NCHAR", "CLOB" -> "String";
            case "DATE" -> "java.time.LocalDate";
            default -> {
                if (oracleType.equalsIgnoreCase("NUMBER")) {
                    if (scale != null && scale > 0) yield "java.math.BigDecimal";
                    if (precision != null && precision <= 9) yield "Integer";
                    yield "Long";
                }
                yield "String";
            }
        };
    }

    public static boolean isNumeric(String oracleType) {
        return oracleType != null && oracleType.equalsIgnoreCase("NUMBER");
    }
}
