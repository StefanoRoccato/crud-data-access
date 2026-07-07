package it.svg.crud.util;

public final class ValueNormalizer {
    private ValueNormalizer() {}

    public static Object normalize(Object value, boolean numeric, boolean nullable) {
        if (value != null) {
            return value;
        }
        if (numeric && !nullable) {
            return 0;
        }
        return null;
    }
}
