package it.svg.crud.repository;

import it.svg.crud.model.dto.CrudModuleResult;
import it.svg.crud.model.dto.IoParameters;
import it.svg.crud.model.dto.EstenOperazioneRecord;

import it.svg.crud.exception.CrudDataAccessException;
import it.svg.crud.exception.ResourceNotFoundException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;



@Repository("crudestenOperazioneRepository")

public class EstenOperazioneRepository {



    private static final int FLAG_CLI_LENGTH = 1;

    private static final int FLAG_CTR_LENGTH = 1;

    private static final int FLAG_GAR_LENGTH = 1;

    private static final int FLAG_TRN_LENGTH = 1;

    private static final int FLAG_OPZ_LENGTH = 1;

    private static final int COD_OPERAZIONE_LENGTH = 5;

    private static final int CODICE_COMPAGNIA_PVG_LENGTH = 1;

    private static final int ID_OPERAZIONE_LENGTH = 1;

    private static final int ID_STATO_ELAB_LENGTH = 1;

    private static final int FLAG_PRENOTAZIONE_LENGTH = 1;

    private static final int FLAG_ABIL_STM_RICH_LENGTH = 1;

    private static final int FLAG_CIRCOLARITA_LENGTH = 1;

    private static final int COD_UNITA_MIS_GIORNI_LENGTH = 1;

    private static final int FLAG_ABIL_STM_RVL_PRVS_LENGTH = 1;

    private static final int FLAG_ESEC_DIFF_LENGTH = 1;

    private static final int FLAG_COLLETTIVO_LENGTH = 1;

    private static final int FLAG_POLIZZA_LENGTH = 1;

    private static final int FLAG_POSIZIONE_LENGTH = 1;




    private final DataSource dataSource;

    public EstenOperazioneRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public CrudModuleResult<EstenOperazioneRecord> execute(IoParameters ioParameters, EstenOperazioneRecord record) {
        String sql = "SELECT FLAG_CLI, FLAG_CTR, FLAG_GAR, FLAG_TRN, FLAG_OPZ, COD_OPERAZIONE, CODICE_COMPAGNIA_PVG, ID_OPERATORE, ID_TIMESTAMP_INIZIO_VAL, ID_FUNZIONE, ID_OPERAZIONE, ID_STATO_ELAB, ID_LOCK, ID_COMMENTO, FLAG_PRENOTAZIONE, DENOM_DMN_ALGO_PREN, FLAG_ABIL_STM_RICH, FLAG_CIRCOLARITA, COD_UNITA_MIS_GIORNI, FLAG_ABIL_STM_RVL_PRVS, FLAG_ESEC_DIFF, FLAG_COLLETTIVO, FLAG_POLIZZA, FLAG_POSIZIONE FROM ESTEN_OPERAZIONE WHERE COD_OPERAZIONE = ? AND CODICE_COMPAGNIA_PVG = ?";
        long start = System.currentTimeMillis();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {


            ps.setObject(1, normalizeCharInput(record.codOperazione(), COD_OPERAZIONE_LENGTH), java.sql.Types.CHAR);



            ps.setObject(2, normalizeCharInput(record.codiceCompagniaPvg(), CODICE_COMPAGNIA_PVG_LENGTH), java.sql.Types.CHAR);



            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new ResourceNotFoundException("VIAT4200: record not found");
                }

                EstenOperazioneRecord outRecord = new EstenOperazioneRecord(


                        normalizeCharOutput(rs.getString("FLAG_CLI"), FLAG_CLI_LENGTH)

,


                        normalizeCharOutput(rs.getString("FLAG_CTR"), FLAG_CTR_LENGTH)

,


                        normalizeCharOutput(rs.getString("FLAG_GAR"), FLAG_GAR_LENGTH)

,


                        normalizeCharOutput(rs.getString("FLAG_TRN"), FLAG_TRN_LENGTH)

,


                        normalizeCharOutput(rs.getString("FLAG_OPZ"), FLAG_OPZ_LENGTH)

,


                        normalizeCharOutput(rs.getString("COD_OPERAZIONE"), COD_OPERAZIONE_LENGTH)

,


                        normalizeCharOutput(rs.getString("CODICE_COMPAGNIA_PVG"), CODICE_COMPAGNIA_PVG_LENGTH)

,


                        rs.getString("ID_OPERATORE")

,

                        rs.getObject("ID_TIMESTAMP_INIZIO_VAL", Long.class)
,


                        rs.getString("ID_FUNZIONE")

,


                        normalizeCharOutput(rs.getString("ID_OPERAZIONE"), ID_OPERAZIONE_LENGTH)

,


                        normalizeCharOutput(rs.getString("ID_STATO_ELAB"), ID_STATO_ELAB_LENGTH)

,

                        rs.getObject("ID_LOCK", Long.class)
,


                        rs.getString("ID_COMMENTO")

,


                        normalizeCharOutput(rs.getString("FLAG_PRENOTAZIONE"), FLAG_PRENOTAZIONE_LENGTH)

,


                        rs.getString("DENOM_DMN_ALGO_PREN")

,


                        normalizeCharOutput(rs.getString("FLAG_ABIL_STM_RICH"), FLAG_ABIL_STM_RICH_LENGTH)

,


                        normalizeCharOutput(rs.getString("FLAG_CIRCOLARITA"), FLAG_CIRCOLARITA_LENGTH)

,


                        normalizeCharOutput(rs.getString("COD_UNITA_MIS_GIORNI"), COD_UNITA_MIS_GIORNI_LENGTH)

,


                        normalizeCharOutput(rs.getString("FLAG_ABIL_STM_RVL_PRVS"), FLAG_ABIL_STM_RVL_PRVS_LENGTH)

,


                        normalizeCharOutput(rs.getString("FLAG_ESEC_DIFF"), FLAG_ESEC_DIFF_LENGTH)

,


                        normalizeCharOutput(rs.getString("FLAG_COLLETTIVO"), FLAG_COLLETTIVO_LENGTH)

,


                        normalizeCharOutput(rs.getString("FLAG_POLIZZA"), FLAG_POLIZZA_LENGTH)

,


                        normalizeCharOutput(rs.getString("FLAG_POSIZIONE"), FLAG_POSIZIONE_LENGTH)


                );

                return new CrudModuleResult<>(ioParameters, outRecord, "VIAT4200", "ESTEN_OPERAZIONE_S", System.currentTimeMillis() - start);
            }
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CrudDataAccessException("Select execution failed for VIAT4200", ex);
        }
    }


    private static String normalizeCharInput(String value, int length) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.length() >= length) {
            return trimmed.substring(0, length);
        }
        return trimmed + " ".repeat(length - trimmed.length());
    }

    private static String normalizeCharOutput(String value, int length) {
        if (value == null) {
            return null;
        }

        if (value.length() >= length) {
            return value.substring(0, length);
        }
        return value + " ".repeat(length - value.length());
    }

}
