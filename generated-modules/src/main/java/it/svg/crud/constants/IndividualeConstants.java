package it.svg.crud.constants;

import java.util.List;

public final class IndividualeConstants {
    private IndividualeConstants() {}

    public static final String MODULE_NAME = "VPO01100";
    public static final String TARGET_TABLE = "INDIVIDUALE_S";
    public static final String OWNER = "VPO_CRUD";
    public static final List<String> SUPPORTED_FUNCTION_CODES = List.of(
            "SR",
            "UR",
            "IR",
            "DR"
    );
}