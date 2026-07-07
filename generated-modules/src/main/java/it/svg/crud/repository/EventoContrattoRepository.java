package it.svg.crud.repository;

import it.svg.crud.model.dto.CrudModuleResult;
import it.svg.crud.model.dto.IoParameters;
import it.svg.crud.model.dto.EventoContrattoRecord;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class EventoContrattoRepository extends AbstractOracleProcedureRepository {

    public EventoContrattoRepository(DataSource dataSource,
                                       MeterRegistry meterRegistry,
                                       CircuitBreakerRegistry circuitBreakerRegistry) {
        super(dataSource, meterRegistry, circuitBreakerRegistry);
    }

    public CrudModuleResult<EventoContrattoRecord> execute(IoParameters ioParameters, EventoContrattoRecord record) {
        String sql = "{ call VPO04500(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        long start = System.currentTimeMillis();
        return execute(sql, "VPO04500", cs -> {

            cs.setObject(1, record.ioFunctionCode() == null ? null : record.ioFunctionCode());

            cs.setObject(2, record.ioReturnCode() == null ? null : record.ioReturnCode());

            cs.registerOutParameter(2, java.sql.Types.VARCHAR);

            cs.setObject(3, record.ioIdTimestampInizioVal() == null ? null : record.ioIdTimestampInizioVal());

            cs.setObject(4, record.ioIdTimestampFineVal() == null ? null : record.ioIdTimestampFineVal());

            cs.setObject(5, record.ioIdDataInizioVal() == null ? null : record.ioIdDataInizioVal());

            cs.setObject(6, record.ioIdDataFineVal() == null ? null : record.ioIdDataFineVal());

            cs.setObject(7, record.ioIdRiga() == null ? null : record.ioIdRiga());

            cs.registerOutParameter(7, java.sql.Types.VARCHAR);

            cs.setObject(8, record.ioFlagTrovato() == null ? null : record.ioFlagTrovato());

            cs.registerOutParameter(8, java.sql.Types.VARCHAR);

            cs.setObject(9, record.ioIdLock() == null ? null : record.ioIdLock());

            cs.registerOutParameter(9, java.sql.Types.VARCHAR);

            cs.setObject(10, record.ioFlag1() == null ? null : record.ioFlag1());

            cs.setObject(11, record.ioFlagAggiornaDb() == null ? null : record.ioFlagAggiornaDb());

            cs.setObject(12, record.ioConcurrentTempUpdate() == null ? null : record.ioConcurrentTempUpdate());

            cs.registerOutParameter(12, java.sql.Types.VARCHAR);

            cs.setObject(13, record.ioFlUpdate() == null ? null : record.ioFlUpdate());

            cs.setObject(14, record.ioTipoStoricita() == null ? null : record.ioTipoStoricita());

            cs.registerOutParameter(14, java.sql.Types.VARCHAR);

            cs.setObject(15, record.ioTimestampApp() == null ? null : record.ioTimestampApp());

            cs.setObject(16, record.sSeqVariazione() == null ? null : record.sSeqVariazione());

            cs.registerOutParameter(16, java.sql.Types.VARCHAR);

            cs.setObject(17, record.sSeqEventoContratto() == null ? null : record.sSeqEventoContratto());

            cs.registerOutParameter(17, java.sql.Types.VARCHAR);

            cs.setObject(18, record.sCodStatoEvento() == null ? null : record.sCodStatoEvento());

            cs.registerOutParameter(18, java.sql.Types.VARCHAR);

            cs.setObject(19, record.sCodEvento() == null ? null : record.sCodEvento());

            cs.registerOutParameter(19, java.sql.Types.VARCHAR);

            cs.setObject(20, record.sCodiceCompagniaPvg() == null ? null : record.sCodiceCompagniaPvg());

            cs.registerOutParameter(20, java.sql.Types.VARCHAR);

            cs.setObject(21, record.sDataOperazione() == null ? null : record.sDataOperazione());

            cs.registerOutParameter(21, java.sql.Types.VARCHAR);

            cs.setObject(22, record.sDataEffettoEvento() == null ? null : record.sDataEffettoEvento());

            cs.registerOutParameter(22, java.sql.Types.VARCHAR);

            cs.setObject(23, record.sTsRifStato() == null ? null : record.sTsRifStato());

            cs.registerOutParameter(23, java.sql.Types.VARCHAR);

            cs.setObject(24, record.sSeqElaborazione() == null ? null : record.sSeqElaborazione());

            cs.registerOutParameter(24, java.sql.Types.VARCHAR);

            cs.setObject(25, record.sSeqRapporto() == null ? null : record.sSeqRapporto());

            cs.registerOutParameter(25, java.sql.Types.VARCHAR);

            cs.setObject(26, record.sTipoRapporto() == null ? null : record.sTipoRapporto());

            cs.registerOutParameter(26, java.sql.Types.VARCHAR);

            cs.setObject(27, record.sIdOperatore() == null ? null : record.sIdOperatore());

            cs.registerOutParameter(27, java.sql.Types.VARCHAR);

            cs.setObject(28, record.sIdTimestampInizioVal() == null ? null : record.sIdTimestampInizioVal());

            cs.registerOutParameter(28, java.sql.Types.VARCHAR);

            cs.setObject(29, record.sIdFunzione() == null ? null : record.sIdFunzione());

            cs.registerOutParameter(29, java.sql.Types.VARCHAR);

            cs.setObject(30, record.sIdOperazione() == null ? null : record.sIdOperazione());

            cs.registerOutParameter(30, java.sql.Types.VARCHAR);

            cs.setObject(31, record.sIdStatoElab() == null ? null : record.sIdStatoElab());

            cs.registerOutParameter(31, java.sql.Types.VARCHAR);

            cs.setObject(32, record.sIdLock() == null ? null : record.sIdLock());

            cs.registerOutParameter(32, java.sql.Types.VARCHAR);

            cs.setObject(33, record.sIdCommento() == null ? null : record.sIdCommento());

            cs.registerOutParameter(33, java.sql.Types.VARCHAR);

            cs.setObject(34, record.sCodModalElabInterf() == null ? null : record.sCodModalElabInterf());

            cs.registerOutParameter(34, java.sql.Types.VARCHAR);

            cs.setObject(35, record.sCodDispvo() == null ? null : record.sCodDispvo());

            cs.registerOutParameter(35, java.sql.Types.VARCHAR);

        }, cs -> {

            // TODO leggere OUT param IO_RETURN_CODE posizione 2

            // TODO leggere OUT param IO_ID_RIGA posizione 7

            // TODO leggere OUT param IO_FLAG_TROVATO posizione 8

            // TODO leggere OUT param IO_ID_LOCK posizione 9

            // TODO leggere OUT param IO_CONCURRENT_TEMP_UPDATE posizione 12

            // TODO leggere OUT param IO_TIPO_STORICITA posizione 14

            // TODO leggere OUT param S_SEQ_VARIAZIONE posizione 16

            // TODO leggere OUT param S_SEQ_EVENTO_CONTRATTO posizione 17

            // TODO leggere OUT param S_COD_STATO_EVENTO posizione 18

            // TODO leggere OUT param S_COD_EVENTO posizione 19

            // TODO leggere OUT param S_CODICE_COMPAGNIA_PVG posizione 20

            // TODO leggere OUT param S_DATA_OPERAZIONE posizione 21

            // TODO leggere OUT param S_DATA_EFFETTO_EVENTO posizione 22

            // TODO leggere OUT param S_TS_RIF_STATO posizione 23

            // TODO leggere OUT param S_SEQ_ELABORAZIONE posizione 24

            // TODO leggere OUT param S_SEQ_RAPPORTO posizione 25

            // TODO leggere OUT param S_TIPO_RAPPORTO posizione 26

            // TODO leggere OUT param S_ID_OPERATORE posizione 27

            // TODO leggere OUT param S_ID_TIMESTAMP_INIZIO_VAL posizione 28

            // TODO leggere OUT param S_ID_FUNZIONE posizione 29

            // TODO leggere OUT param S_ID_OPERAZIONE posizione 30

            // TODO leggere OUT param S_ID_STATO_ELAB posizione 31

            // TODO leggere OUT param S_ID_LOCK posizione 32

            // TODO leggere OUT param S_ID_COMMENTO posizione 33

            // TODO leggere OUT param S_COD_MODAL_ELAB_INTERF posizione 34

            // TODO leggere OUT param S_COD_DISPVO posizione 35

            return new CrudModuleResult<>(ioParameters, record, "VPO04500", "EVENTO_CONTRATTO", System.currentTimeMillis() - start);
        });
    }
}