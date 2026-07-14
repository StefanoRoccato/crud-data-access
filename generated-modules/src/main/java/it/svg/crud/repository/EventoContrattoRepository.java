package it.svg.crud.repository;

import it.svg.crud.model.dto.CrudModuleResult;
import it.svg.crud.model.dto.IoParameters;
import it.svg.crud.model.dto.EventoContrattoRecord;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;



@Repository("crudeventoContrattoRepository")

public class EventoContrattoRepository extends AbstractOracleProcedureRepository {



    private static final int S_COD_STATO_EVENTO_LENGTH = 2;

    private static final int S_COD_EVENTO_LENGTH = 5;

    private static final int S_CODICE_COMPAGNIA_PVG_LENGTH = 1;

    private static final int S_TIPO_RAPPORTO_LENGTH = 2;

    private static final int S_ID_OPERAZIONE_LENGTH = 1;

    private static final int S_ID_STATO_ELAB_LENGTH = 1;

    private static final int S_COD_MODAL_ELAB_INTERF_LENGTH = 1;

    private static final int S_COD_DISPVO_LENGTH = 1;




    public EventoContrattoRepository(DataSource dataSource,
                                       MeterRegistry meterRegistry,
                                       CircuitBreakerRegistry circuitBreakerRegistry) {
        super(dataSource, meterRegistry, circuitBreakerRegistry);
    }

    public CrudModuleResult<EventoContrattoRecord> execute(IoParameters ioParameters, EventoContrattoRecord record) {
        String sql = "{ call VPO04500(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        long start = System.currentTimeMillis();
        return execute(sql, "VPO04500", cs -> {

            cs.setObject(1, record.ioFunctionCode() == null ? null : record.ioFunctionCode(), java.sql.Types.VARCHAR);

            cs.setObject(2, record.ioReturnCode() == null ? null : record.ioReturnCode(), java.sql.Types.BIGINT);

            cs.registerOutParameter(2, java.sql.Types.BIGINT);

            cs.setObject(3, record.ioIdTimestampInizioVal() == null ? null : record.ioIdTimestampInizioVal(), java.sql.Types.BIGINT);

            cs.setObject(4, record.ioIdTimestampFineVal() == null ? null : record.ioIdTimestampFineVal(), java.sql.Types.BIGINT);

            cs.setObject(5, record.ioIdDataInizioVal() == null ? null : record.ioIdDataInizioVal(), java.sql.Types.VARCHAR);

            cs.setObject(6, record.ioIdDataFineVal() == null ? null : record.ioIdDataFineVal(), java.sql.Types.VARCHAR);

            cs.setObject(7, record.ioIdRiga() == null ? null : record.ioIdRiga(), java.sql.Types.BIGINT);

            cs.registerOutParameter(7, java.sql.Types.BIGINT);

            cs.setObject(8, record.ioFlagTrovato() == null ? null : record.ioFlagTrovato(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(8, java.sql.Types.VARCHAR);

            cs.setObject(9, record.ioIdLock() == null ? null : record.ioIdLock(), java.sql.Types.BIGINT);

            cs.registerOutParameter(9, java.sql.Types.BIGINT);

            cs.setObject(10, record.ioFlag1() == null ? null : record.ioFlag1(), java.sql.Types.VARCHAR);

            cs.setObject(11, record.ioFlagAggiornaDb() == null ? null : record.ioFlagAggiornaDb(), java.sql.Types.BIGINT);

            cs.setObject(12, record.ioConcurrentTempUpdate() == null ? null : record.ioConcurrentTempUpdate(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(12, java.sql.Types.VARCHAR);

            cs.setObject(13, record.ioFlUpdate() == null ? null : record.ioFlUpdate(), java.sql.Types.VARCHAR);

            cs.setObject(14, record.ioTipoStoricita() == null ? null : record.ioTipoStoricita(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(14, java.sql.Types.VARCHAR);

            cs.setObject(15, record.ioTimestampApp() == null ? null : record.ioTimestampApp(), java.sql.Types.BIGINT);

            cs.setObject(16, record.sSeqVariazione() == null ? null : record.sSeqVariazione(), java.sql.Types.BIGINT);

            cs.registerOutParameter(16, java.sql.Types.BIGINT);

            cs.setObject(17, record.sSeqEventoContratto() == null ? 0 : record.sSeqEventoContratto(), java.sql.Types.BIGINT);

            cs.registerOutParameter(17, java.sql.Types.BIGINT);

            cs.setObject(18, normalizeCharInput(record.sCodStatoEvento(), S_COD_STATO_EVENTO_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(18, java.sql.Types.CHAR);

            cs.setObject(19, normalizeCharInput(record.sCodEvento(), S_COD_EVENTO_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(19, java.sql.Types.CHAR);

            cs.setObject(20, normalizeCharInput(record.sCodiceCompagniaPvg(), S_CODICE_COMPAGNIA_PVG_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(20, java.sql.Types.CHAR);

            cs.setObject(21, record.sDataOperazione() == null ? 0 : record.sDataOperazione(), java.sql.Types.BIGINT);

            cs.registerOutParameter(21, java.sql.Types.BIGINT);

            cs.setObject(22, record.sDataEffettoEvento() == null ? null : record.sDataEffettoEvento(), java.sql.Types.DATE);

            cs.registerOutParameter(22, java.sql.Types.DATE);

            cs.setObject(23, record.sTsRifStato() == null ? null : record.sTsRifStato(), java.sql.Types.BIGINT);

            cs.registerOutParameter(23, java.sql.Types.BIGINT);

            cs.setObject(24, record.sSeqElaborazione() == null ? null : record.sSeqElaborazione(), java.sql.Types.BIGINT);

            cs.registerOutParameter(24, java.sql.Types.BIGINT);

            cs.setObject(25, record.sSeqRapporto() == null ? 0 : record.sSeqRapporto(), java.sql.Types.BIGINT);

            cs.registerOutParameter(25, java.sql.Types.BIGINT);

            cs.setObject(26, normalizeCharInput(record.sTipoRapporto(), S_TIPO_RAPPORTO_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(26, java.sql.Types.CHAR);

            cs.setObject(27, record.sIdOperatore() == null ? null : record.sIdOperatore(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(27, java.sql.Types.VARCHAR);

            cs.setObject(28, record.sIdTimestampInizioVal() == null ? 0 : record.sIdTimestampInizioVal(), java.sql.Types.BIGINT);

            cs.registerOutParameter(28, java.sql.Types.BIGINT);

            cs.setObject(29, record.sIdFunzione() == null ? null : record.sIdFunzione(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(29, java.sql.Types.VARCHAR);

            cs.setObject(30, normalizeCharInput(record.sIdOperazione(), S_ID_OPERAZIONE_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(30, java.sql.Types.CHAR);

            cs.setObject(31, normalizeCharInput(record.sIdStatoElab(), S_ID_STATO_ELAB_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(31, java.sql.Types.CHAR);

            cs.setObject(32, record.sIdLock() == null ? 0 : record.sIdLock(), java.sql.Types.BIGINT);

            cs.registerOutParameter(32, java.sql.Types.BIGINT);

            cs.setObject(33, record.sIdCommento() == null ? null : record.sIdCommento(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(33, java.sql.Types.VARCHAR);

            cs.setObject(34, normalizeCharInput(record.sCodModalElabInterf(), S_COD_MODAL_ELAB_INTERF_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(34, java.sql.Types.CHAR);

            cs.setObject(35, normalizeCharInput(record.sCodDispvo(), S_COD_DISPVO_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(35, java.sql.Types.CHAR);

        }, cs -> {

            try {

            // Lettura parametri IO_ di output (INOUT)

            Long ioReturnCode = cs.getObject(2, Long.class);

            Long ioIdRiga = cs.getObject(7, Long.class);

            String ioFlagTrovato = cs.getString(8);

            Long ioIdLock = cs.getObject(9, Long.class);

            String ioConcurrentTempUpdate = cs.getString(12);

            String ioTipoStoricita = cs.getString(14);

            

            // Lettura parametri S_ di output (OUT/INOUT)

            Long sSeqVariazione = cs.getObject(16, Long.class);

            Long sSeqEventoContratto = cs.getObject(17, Long.class);

            String sCodStatoEvento = cs.getString(18);

            sCodStatoEvento = normalizeCharOutput(sCodStatoEvento, S_COD_STATO_EVENTO_LENGTH);

            String sCodEvento = cs.getString(19);

            sCodEvento = normalizeCharOutput(sCodEvento, S_COD_EVENTO_LENGTH);

            String sCodiceCompagniaPvg = cs.getString(20);

            sCodiceCompagniaPvg = normalizeCharOutput(sCodiceCompagniaPvg, S_CODICE_COMPAGNIA_PVG_LENGTH);

            Long sDataOperazione = cs.getObject(21, Long.class);

            java.sql.Date rawSDataEffettoEvento = cs.getDate(22);

            java.time.LocalDate sDataEffettoEvento = rawSDataEffettoEvento != null ? rawSDataEffettoEvento.toLocalDate() : null;

            Long sTsRifStato = cs.getObject(23, Long.class);

            Long sSeqElaborazione = cs.getObject(24, Long.class);

            Long sSeqRapporto = cs.getObject(25, Long.class);

            String sTipoRapporto = cs.getString(26);

            sTipoRapporto = normalizeCharOutput(sTipoRapporto, S_TIPO_RAPPORTO_LENGTH);

            String sIdOperatore = cs.getString(27);

            Long sIdTimestampInizioVal = cs.getObject(28, Long.class);

            String sIdFunzione = cs.getString(29);

            String sIdOperazione = cs.getString(30);

            sIdOperazione = normalizeCharOutput(sIdOperazione, S_ID_OPERAZIONE_LENGTH);

            String sIdStatoElab = cs.getString(31);

            sIdStatoElab = normalizeCharOutput(sIdStatoElab, S_ID_STATO_ELAB_LENGTH);

            Long sIdLock = cs.getObject(32, Long.class);

            String sIdCommento = cs.getString(33);

            String sCodModalElabInterf = cs.getString(34);

            sCodModalElabInterf = normalizeCharOutput(sCodModalElabInterf, S_COD_MODAL_ELAB_INTERF_LENGTH);

            String sCodDispvo = cs.getString(35);

            sCodDispvo = normalizeCharOutput(sCodDispvo, S_COD_DISPVO_LENGTH);

            

            EventoContrattoRecord outRecord = new EventoContrattoRecord(

                record.ioFunctionCode(),  // 1 - IN only

                ioReturnCode,  // 2

                record.ioIdTimestampInizioVal(),  // 3 - IN only

                record.ioIdTimestampFineVal(),  // 4 - IN only

                record.ioIdDataInizioVal(),  // 5 - IN only

                record.ioIdDataFineVal(),  // 6 - IN only

                ioIdRiga,  // 7

                ioFlagTrovato,  // 8

                ioIdLock,  // 9

                record.ioFlag1(),  // 10 - IN only

                record.ioFlagAggiornaDb(),  // 11 - IN only

                ioConcurrentTempUpdate,  // 12

                record.ioFlUpdate(),  // 13 - IN only

                ioTipoStoricita,  // 14

                record.ioTimestampApp(),  // 15 - IN only

                sSeqVariazione,  // 16

                sSeqEventoContratto,  // 17

                sCodStatoEvento,  // 18

                sCodEvento,  // 19

                sCodiceCompagniaPvg,  // 20

                sDataOperazione,  // 21

                sDataEffettoEvento,  // 22

                sTsRifStato,  // 23

                sSeqElaborazione,  // 24

                sSeqRapporto,  // 25

                sTipoRapporto,  // 26

                sIdOperatore,  // 27

                sIdTimestampInizioVal,  // 28

                sIdFunzione,  // 29

                sIdOperazione,  // 30

                sIdStatoElab,  // 31

                sIdLock,  // 32

                sIdCommento,  // 33

                sCodModalElabInterf,  // 34

                sCodDispvo  // 35

            );

            

            IoParameters updatedIoParams = new IoParameters(

                ioParameters.functionCode(),

                ioReturnCode != null ? ioReturnCode.intValue() : null,

                ioParameters.flagAggiornaDb(),

                ioParameters.flag1(),

                ioTipoStoricita,

                ioFlagTrovato,

                ioParameters.idTimestampInizioVal(),

                ioParameters.idTimestampFineVal(),

                ioParameters.idDataInizioVal(),

                ioParameters.idDataFineVal(),

                ioParameters.dataCont(),

                ioIdRiga,

                ioIdLock,

                ioParameters.flUpdate(),

                ioConcurrentTempUpdate,

                ioParameters.timestampApp(),

                ioParameters.sqlerrmc(),

                ioParameters.livLog(),

                ioParameters.sessionId()

            );

            

            return new CrudModuleResult<>(updatedIoParams, outRecord, "VPO04500", "EVENTO_CONTRATTO", System.currentTimeMillis() - start);

            } catch (java.sql.SQLException e) {

                throw new it.svg.crud.exception.CrudDataAccessException("Error reading OUT parameters from VPO04500", e);

            }

        });
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
