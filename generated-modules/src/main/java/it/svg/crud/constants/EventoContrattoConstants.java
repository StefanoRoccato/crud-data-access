package it.svg.crud.constants;

import java.util.List;

public final class EventoContrattoConstants {
    private EventoContrattoConstants() {}

    public static final String MODULE_NAME = "VPO04500";
    public static final String TARGET_TABLE = "EVENTO_CONTRATTO";
    public static final String OWNER = "VPO_CRUD";
    public static final List<String> SUPPORTED_FUNCTION_CODES = List.of(
            "SR",
            "UR",
            "IR",
            "DR"
    );
}