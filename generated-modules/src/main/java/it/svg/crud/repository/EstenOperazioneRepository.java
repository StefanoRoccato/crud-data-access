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

    private final DataSource dataSource;

    public EstenOperazioneRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public CrudModuleResult<EstenOperazioneRecord> execute(IoParameters ioParameters, EstenOperazioneRecord record) {
        String sql = "SELECT FLAG_CLI, FLAG_CTR, FLAG_GAR, FLAG_TRN, FLAG_OPZ, COD_OPERAZIONE, CODICE_COMPAGNIA_PVG, ID_OPERATORE, ID_TIMESTAMP_INIZIO_VAL, ID_FUNZIONE, ID_OPERAZIONE, ID_STATO_ELAB, ID_LOCK, ID_COMMENTO, FLAG_PRENOTAZIONE, DENOM_DMN_ALGO_PREN, FLAG_ABIL_STM_RICH, FLAG_CIRCOLARITA, COD_UNITA_MIS_GIORNI, FLAG_ABIL_STM_RVL_PRVS, FLAG_ESEC_DIFF, FLAG_COLLETTIVO, FLAG_POLIZZA, FLAG_POSIZIONE FROM ESTEN_OPERAZIONE WHERE COD_OPERAZIONE = ? AND CODICE_COMPAGNIA_PVG = ?";
        long start = System.currentTimeMillis();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, record.codOperazione());

            ps.setObject(2, record.codiceCompagniaPvg());


            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new ResourceNotFoundException("VIAT4200: record not found");
                }

                EstenOperazioneRecord outRecord = new EstenOperazioneRecord(

                        rs.getString("FLAG_CLI")
,

                        rs.getString("FLAG_CTR")
,

                        rs.getString("FLAG_GAR")
,

                        rs.getString("FLAG_TRN")
,

                        rs.getString("FLAG_OPZ")
,

                        rs.getString("COD_OPERAZIONE")
,

                        rs.getString("CODICE_COMPAGNIA_PVG")
,

                        rs.getString("ID_OPERATORE")
,

                        rs.getObject("ID_TIMESTAMP_INIZIO_VAL", Long.class)
,

                        rs.getString("ID_FUNZIONE")
,

                        rs.getString("ID_OPERAZIONE")
,

                        rs.getString("ID_STATO_ELAB")
,

                        rs.getObject("ID_LOCK", Long.class)
,

                        rs.getString("ID_COMMENTO")
,

                        rs.getString("FLAG_PRENOTAZIONE")
,

                        rs.getString("DENOM_DMN_ALGO_PREN")
,

                        rs.getString("FLAG_ABIL_STM_RICH")
,

                        rs.getString("FLAG_CIRCOLARITA")
,

                        rs.getString("COD_UNITA_MIS_GIORNI")
,

                        rs.getString("FLAG_ABIL_STM_RVL_PRVS")
,

                        rs.getString("FLAG_ESEC_DIFF")
,

                        rs.getString("FLAG_COLLETTIVO")
,

                        rs.getString("FLAG_POLIZZA")
,

                        rs.getString("FLAG_POSIZIONE")

                );

                return new CrudModuleResult<>(ioParameters, outRecord, "VIAT4200", "ESTEN_OPERAZIONE_S", System.currentTimeMillis() - start);
            }
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CrudDataAccessException("Select execution failed for VIAT4200", ex);
        }
    }
}
