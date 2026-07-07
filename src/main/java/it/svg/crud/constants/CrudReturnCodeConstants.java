package it.svg.crud.constants;

public final class CrudReturnCodeConstants {
    private CrudReturnCodeConstants() {}

    public static final int IO_SUCCESSFUL_RC = 0;
    public static final int IO_ERROR_NOT_FOUND = 1;
    public static final int IO_WARNING_RC = 4;
    public static final int IO_SEVERE_RC = 16;
    public static final int IO_INVALID_FUNCTION = 90019;
    public static final int IO_ERR_KEY = 90021;
    public static final int IO_CONCURRENT_UPDATE_RC = 90022;
    public static final int IO_ID_RIGA_A_ZERO = 90049;
    public static final int IO_NO_MODIFY_DB = 90050;
    public static final int IO_NO_UPDATE_FOUND = 90051;

    public static boolean isSqlError(int code) {
        return code < 0;
    }
}
