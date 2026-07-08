package it.svg.crud.constants;

import java.util.List;

public final class EstenOperazioneConstants {
    private EstenOperazioneConstants() {}

    public static final String MODULE_NAME = "VIAT4200";
    public static final String TARGET_TABLE = "ESTEN_OPERAZIONE_S";
    public static final String OWNER = "VIA_CRUD";
    public static final List<String> SUPPORTED_FUNCTION_CODES = List.of(
            "SR",
            "UR",
            "IR",
            "DR"
    );
}