package it.svg.crud.repository;

import it.svg.crud.model.dto.CrudModuleResult;
import it.svg.crud.model.dto.IoParameters;
import it.svg.crud.model.dto.IndividualeRecord;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;



@Repository("crudindividualeRepository")

public class IndividualeRepository extends AbstractOracleProcedureRepository {



    private static final int S_TIPO_RIF_ESTERNO_LENGTH = 1;

    private static final int S_COD_SISTEMA_ESTERNO_LENGTH = 2;

    private static final int S_COD_REGIME_FISCALE_LENGTH = 1;

    private static final int S_FLAG_DETRAIBILITA_LENGTH = 1;

    private static final int S_FLAG_RIN_PRESTITI_LENGTH = 1;

    private static final int S_COD_RAPPORTO_LENGTH = 9;

    private static final int S_COD_STATO_CONTRATTO_LENGTH = 1;

    private static final int S_COD_COASSICURAZIONE_LENGTH = 1;

    private static final int S_FLAG_VISITA_MEDICA_LENGTH = 1;

    private static final int S_COD_COMPTO_TABAG_LENGTH = 1;

    private static final int S_FLAG_OPZIONE_RENDITA_LENGTH = 1;

    private static final int S_FLAG_TRASFORMAZIONE_LENGTH = 1;

    private static final int S_CODICE_RAMO_LENGTH = 2;

    private static final int S_TIPO_INDIVIDUALE_LENGTH = 2;

    private static final int S_CODICE_COMPAGNIA_PVG_LENGTH = 1;

    private static final int S_ID_OPERAZIONE_LENGTH = 1;

    private static final int S_ID_STATO_ELAB_LENGTH = 1;

    private static final int S_TIPO_INDIV_LEGALE_LENGTH = 2;

    private static final int S_CODICE_SISTEMA_PROVEN_LENGTH = 1;

    private static final int S_TIPO_ORIG_EMISS_LENGTH = 1;




    public IndividualeRepository(DataSource dataSource,
                                       MeterRegistry meterRegistry,
                                       CircuitBreakerRegistry circuitBreakerRegistry) {
        super(dataSource, meterRegistry, circuitBreakerRegistry);
    }

    public CrudModuleResult<IndividualeRecord> execute(IoParameters ioParameters, IndividualeRecord record) {
        String sql = "{ call VPO01100(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        long start = System.currentTimeMillis();
        return execute(sql, "VPO01100", cs -> {

            cs.setObject(1, record.ioFunctionCode() == null ? null : record.ioFunctionCode(), java.sql.Types.VARCHAR);

            cs.setObject(2, record.ioReturnCode() == null ? null : record.ioReturnCode(), java.sql.Types.BIGINT);

            cs.registerOutParameter(2, java.sql.Types.BIGINT);

            cs.setObject(3, record.ioIdTimestampInizioVal() == null ? null : record.ioIdTimestampInizioVal(), java.sql.Types.BIGINT);

            cs.registerOutParameter(3, java.sql.Types.BIGINT);

            cs.setObject(4, record.ioIdTimestampFineVal() == null ? null : record.ioIdTimestampFineVal(), java.sql.Types.BIGINT);

            cs.registerOutParameter(4, java.sql.Types.BIGINT);

            cs.setObject(5, record.ioIdDataInizioVal() == null ? null : record.ioIdDataInizioVal(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(5, java.sql.Types.VARCHAR);

            cs.setObject(6, record.ioIdDataFineVal() == null ? null : record.ioIdDataFineVal(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(6, java.sql.Types.VARCHAR);

            cs.setObject(7, record.ioDataCont() == null ? null : record.ioDataCont(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(7, java.sql.Types.VARCHAR);

            cs.setObject(8, record.ioIdRiga() == null ? null : record.ioIdRiga(), java.sql.Types.BIGINT);

            cs.registerOutParameter(8, java.sql.Types.BIGINT);

            cs.setObject(9, record.ioFlagTrovato() == null ? null : record.ioFlagTrovato(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(9, java.sql.Types.VARCHAR);

            cs.setObject(10, record.ioIdLock() == null ? null : record.ioIdLock(), java.sql.Types.BIGINT);

            cs.registerOutParameter(10, java.sql.Types.BIGINT);

            cs.setObject(11, record.ioFlag1() == null ? null : record.ioFlag1(), java.sql.Types.VARCHAR);

            cs.setObject(12, record.ioFlagAggiornaDb() == null ? null : record.ioFlagAggiornaDb(), java.sql.Types.BIGINT);

            cs.setObject(13, record.ioConcurrentTempUpdate() == null ? null : record.ioConcurrentTempUpdate(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(13, java.sql.Types.VARCHAR);

            cs.setObject(14, record.ioFlUpdate() == null ? null : record.ioFlUpdate(), java.sql.Types.VARCHAR);

            cs.setObject(15, record.ioTipoStoricita() == null ? null : record.ioTipoStoricita(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(15, java.sql.Types.VARCHAR);

            cs.setObject(16, record.sSeqEventoContratto() == null ? 0 : record.sSeqEventoContratto(), java.sql.Types.BIGINT);

            cs.registerOutParameter(16, java.sql.Types.BIGINT);

            cs.setObject(17, normalizeCharInput(record.sTipoRifEsterno(), S_TIPO_RIF_ESTERNO_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(17, java.sql.Types.CHAR);

            cs.setObject(18, record.sCodRifEsterno() == null ? null : record.sCodRifEsterno(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(18, java.sql.Types.VARCHAR);

            cs.setObject(19, normalizeCharInput(record.sCodSistemaEsterno(), S_COD_SISTEMA_ESTERNO_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(19, java.sql.Types.CHAR);

            cs.setObject(20, record.sCodRifSistEsterno() == null ? null : record.sCodRifSistEsterno(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(20, java.sql.Types.VARCHAR);

            cs.setObject(21, normalizeCharInput(record.sCodRegimeFiscale(), S_COD_REGIME_FISCALE_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(21, java.sql.Types.CHAR);

            cs.setObject(22, normalizeCharInput(record.sFlagDetraibilita(), S_FLAG_DETRAIBILITA_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(22, java.sql.Types.CHAR);

            cs.setObject(23, normalizeCharInput(record.sFlagRinPrestiti(), S_FLAG_RIN_PRESTITI_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(23, java.sql.Types.CHAR);

            cs.setObject(24, record.sDataPerfezionamento() == null ? null : record.sDataPerfezionamento(), java.sql.Types.DATE);

            cs.registerOutParameter(24, java.sql.Types.DATE);

            cs.setObject(25, normalizeCharInput(record.sCodRapporto(), S_COD_RAPPORTO_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(25, java.sql.Types.CHAR);

            cs.setObject(26, normalizeCharInput(record.sCodStatoContratto(), S_COD_STATO_CONTRATTO_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(26, java.sql.Types.CHAR);

            cs.setObject(27, record.sDataDecorrenza() == null ? null : record.sDataDecorrenza(), java.sql.Types.DATE);

            cs.registerOutParameter(27, java.sql.Types.DATE);

            cs.setObject(28, record.sDataScadenza() == null ? null : record.sDataScadenza(), java.sql.Types.DATE);

            cs.registerOutParameter(28, java.sql.Types.DATE);

            cs.setObject(29, normalizeCharInput(record.sCodCoassicurazione(), S_COD_COASSICURAZIONE_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(29, java.sql.Types.CHAR);

            cs.setObject(30, normalizeCharInput(record.sFlagVisitaMedica(), S_FLAG_VISITA_MEDICA_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(30, java.sql.Types.CHAR);

            cs.setObject(31, normalizeCharInput(record.sCodComptoTabag(), S_COD_COMPTO_TABAG_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(31, java.sql.Types.CHAR);

            cs.setObject(32, normalizeCharInput(record.sFlagOpzioneRendita(), S_FLAG_OPZIONE_RENDITA_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(32, java.sql.Types.CHAR);

            cs.setObject(33, normalizeCharInput(record.sFlagTrasformazione(), S_FLAG_TRASFORMAZIONE_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(33, java.sql.Types.CHAR);

            cs.setObject(34, record.sNumMesiResidRisc() == null ? null : record.sNumMesiResidRisc(), java.sql.Types.INTEGER);

            cs.registerOutParameter(34, java.sql.Types.INTEGER);

            cs.setObject(35, record.sImpImponProvvTrasf() == null ? null : record.sImpImponProvvTrasf(), java.sql.Types.NUMERIC);

            cs.registerOutParameter(35, java.sql.Types.NUMERIC);

            cs.setObject(36, normalizeCharInput(record.sCodiceRamo(), S_CODICE_RAMO_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(36, java.sql.Types.CHAR);

            cs.setObject(37, record.sSeqConv() == null ? null : record.sSeqConv(), java.sql.Types.BIGINT);

            cs.registerOutParameter(37, java.sql.Types.BIGINT);

            cs.setObject(38, record.sSeqRapporto() == null ? 0 : record.sSeqRapporto(), java.sql.Types.BIGINT);

            cs.registerOutParameter(38, java.sql.Types.BIGINT);

            cs.setObject(39, normalizeCharInput(record.sTipoIndividuale(), S_TIPO_INDIVIDUALE_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(39, java.sql.Types.CHAR);

            cs.setObject(40, normalizeCharInput(record.sCodiceCompagniaPvg(), S_CODICE_COMPAGNIA_PVG_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(40, java.sql.Types.CHAR);

            cs.setObject(41, record.sIdOperatore() == null ? null : record.sIdOperatore(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(41, java.sql.Types.VARCHAR);

            cs.setObject(42, record.sIdTimestampInizioVal() == null ? 0 : record.sIdTimestampInizioVal(), java.sql.Types.BIGINT);

            cs.registerOutParameter(42, java.sql.Types.BIGINT);

            cs.setObject(43, record.sIdFunzione() == null ? null : record.sIdFunzione(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(43, java.sql.Types.VARCHAR);

            cs.setObject(44, normalizeCharInput(record.sIdOperazione(), S_ID_OPERAZIONE_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(44, java.sql.Types.CHAR);

            cs.setObject(45, normalizeCharInput(record.sIdStatoElab(), S_ID_STATO_ELAB_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(45, java.sql.Types.CHAR);

            cs.setObject(46, record.sIdLock() == null ? 0 : record.sIdLock(), java.sql.Types.BIGINT);

            cs.registerOutParameter(46, java.sql.Types.BIGINT);

            cs.setObject(47, record.sIdCommento() == null ? null : record.sIdCommento(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(47, java.sql.Types.VARCHAR);

            cs.setObject(48, record.sNumMesiPror() == null ? null : record.sNumMesiPror(), java.sql.Types.INTEGER);

            cs.registerOutParameter(48, java.sql.Types.INTEGER);

            cs.setObject(49, record.sDataRiprPagPremi() == null ? null : record.sDataRiprPagPremi(), java.sql.Types.DATE);

            cs.registerOutParameter(49, java.sql.Types.DATE);

            cs.setObject(50, record.sImpPrestazMaxFiscv() == null ? null : record.sImpPrestazMaxFiscv(), java.sql.Types.NUMERIC);

            cs.registerOutParameter(50, java.sql.Types.NUMERIC);

            cs.setObject(51, normalizeCharInput(record.sTipoIndivLegale(), S_TIPO_INDIV_LEGALE_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(51, java.sql.Types.CHAR);

            cs.setObject(52, normalizeCharInput(record.sCodiceSistemaProven(), S_CODICE_SISTEMA_PROVEN_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(52, java.sql.Types.CHAR);

            cs.setObject(53, normalizeCharInput(record.sTipoOrigEmiss(), S_TIPO_ORIG_EMISS_LENGTH), java.sql.Types.CHAR);

            cs.registerOutParameter(53, java.sql.Types.CHAR);

            cs.setObject(54, record.sNumAnnoCoorte() == null ? null : record.sNumAnnoCoorte(), java.sql.Types.INTEGER);

            cs.registerOutParameter(54, java.sql.Types.INTEGER);

        }, cs -> {

            try {

            // Lettura parametri IO_ di output (INOUT)

            Long ioReturnCode = cs.getObject(2, Long.class);

            Long ioIdTimestampInizioVal = cs.getObject(3, Long.class);

            Long ioIdTimestampFineVal = cs.getObject(4, Long.class);

            String ioIdDataInizioVal = cs.getString(5);

            String ioIdDataFineVal = cs.getString(6);

            String ioDataCont = cs.getString(7);

            Long ioIdRiga = cs.getObject(8, Long.class);

            String ioFlagTrovato = cs.getString(9);

            Long ioIdLock = cs.getObject(10, Long.class);

            String ioConcurrentTempUpdate = cs.getString(13);

            String ioTipoStoricita = cs.getString(15);

            

            // Lettura parametri S_ di output (OUT/INOUT)

            Long sSeqEventoContratto = cs.getObject(16, Long.class);

            String sTipoRifEsterno = cs.getString(17);

            sTipoRifEsterno = normalizeCharOutput(sTipoRifEsterno, S_TIPO_RIF_ESTERNO_LENGTH);

            String sCodRifEsterno = cs.getString(18);

            String sCodSistemaEsterno = cs.getString(19);

            sCodSistemaEsterno = normalizeCharOutput(sCodSistemaEsterno, S_COD_SISTEMA_ESTERNO_LENGTH);

            String sCodRifSistEsterno = cs.getString(20);

            String sCodRegimeFiscale = cs.getString(21);

            sCodRegimeFiscale = normalizeCharOutput(sCodRegimeFiscale, S_COD_REGIME_FISCALE_LENGTH);

            String sFlagDetraibilita = cs.getString(22);

            sFlagDetraibilita = normalizeCharOutput(sFlagDetraibilita, S_FLAG_DETRAIBILITA_LENGTH);

            String sFlagRinPrestiti = cs.getString(23);

            sFlagRinPrestiti = normalizeCharOutput(sFlagRinPrestiti, S_FLAG_RIN_PRESTITI_LENGTH);

            java.sql.Date rawSDataPerfezionamento = cs.getDate(24);

            java.time.LocalDate sDataPerfezionamento = rawSDataPerfezionamento != null ? rawSDataPerfezionamento.toLocalDate() : null;

            String sCodRapporto = cs.getString(25);

            sCodRapporto = normalizeCharOutput(sCodRapporto, S_COD_RAPPORTO_LENGTH);

            String sCodStatoContratto = cs.getString(26);

            sCodStatoContratto = normalizeCharOutput(sCodStatoContratto, S_COD_STATO_CONTRATTO_LENGTH);

            java.sql.Date rawSDataDecorrenza = cs.getDate(27);

            java.time.LocalDate sDataDecorrenza = rawSDataDecorrenza != null ? rawSDataDecorrenza.toLocalDate() : null;

            java.sql.Date rawSDataScadenza = cs.getDate(28);

            java.time.LocalDate sDataScadenza = rawSDataScadenza != null ? rawSDataScadenza.toLocalDate() : null;

            String sCodCoassicurazione = cs.getString(29);

            sCodCoassicurazione = normalizeCharOutput(sCodCoassicurazione, S_COD_COASSICURAZIONE_LENGTH);

            String sFlagVisitaMedica = cs.getString(30);

            sFlagVisitaMedica = normalizeCharOutput(sFlagVisitaMedica, S_FLAG_VISITA_MEDICA_LENGTH);

            String sCodComptoTabag = cs.getString(31);

            sCodComptoTabag = normalizeCharOutput(sCodComptoTabag, S_COD_COMPTO_TABAG_LENGTH);

            String sFlagOpzioneRendita = cs.getString(32);

            sFlagOpzioneRendita = normalizeCharOutput(sFlagOpzioneRendita, S_FLAG_OPZIONE_RENDITA_LENGTH);

            String sFlagTrasformazione = cs.getString(33);

            sFlagTrasformazione = normalizeCharOutput(sFlagTrasformazione, S_FLAG_TRASFORMAZIONE_LENGTH);

            Integer sNumMesiResidRisc = cs.getObject(34, Integer.class);

            java.math.BigDecimal sImpImponProvvTrasf = cs.getObject(35, java.math.BigDecimal.class);

            String sCodiceRamo = cs.getString(36);

            sCodiceRamo = normalizeCharOutput(sCodiceRamo, S_CODICE_RAMO_LENGTH);

            Long sSeqConv = cs.getObject(37, Long.class);

            Long sSeqRapporto = cs.getObject(38, Long.class);

            String sTipoIndividuale = cs.getString(39);

            sTipoIndividuale = normalizeCharOutput(sTipoIndividuale, S_TIPO_INDIVIDUALE_LENGTH);

            String sCodiceCompagniaPvg = cs.getString(40);

            sCodiceCompagniaPvg = normalizeCharOutput(sCodiceCompagniaPvg, S_CODICE_COMPAGNIA_PVG_LENGTH);

            String sIdOperatore = cs.getString(41);

            Long sIdTimestampInizioVal = cs.getObject(42, Long.class);

            String sIdFunzione = cs.getString(43);

            String sIdOperazione = cs.getString(44);

            sIdOperazione = normalizeCharOutput(sIdOperazione, S_ID_OPERAZIONE_LENGTH);

            String sIdStatoElab = cs.getString(45);

            sIdStatoElab = normalizeCharOutput(sIdStatoElab, S_ID_STATO_ELAB_LENGTH);

            Long sIdLock = cs.getObject(46, Long.class);

            String sIdCommento = cs.getString(47);

            Integer sNumMesiPror = cs.getObject(48, Integer.class);

            java.sql.Date rawSDataRiprPagPremi = cs.getDate(49);

            java.time.LocalDate sDataRiprPagPremi = rawSDataRiprPagPremi != null ? rawSDataRiprPagPremi.toLocalDate() : null;

            java.math.BigDecimal sImpPrestazMaxFiscv = cs.getObject(50, java.math.BigDecimal.class);

            String sTipoIndivLegale = cs.getString(51);

            sTipoIndivLegale = normalizeCharOutput(sTipoIndivLegale, S_TIPO_INDIV_LEGALE_LENGTH);

            String sCodiceSistemaProven = cs.getString(52);

            sCodiceSistemaProven = normalizeCharOutput(sCodiceSistemaProven, S_CODICE_SISTEMA_PROVEN_LENGTH);

            String sTipoOrigEmiss = cs.getString(53);

            sTipoOrigEmiss = normalizeCharOutput(sTipoOrigEmiss, S_TIPO_ORIG_EMISS_LENGTH);

            Integer sNumAnnoCoorte = cs.getObject(54, Integer.class);

            

            IndividualeRecord outRecord = new IndividualeRecord(

                record.ioFunctionCode(),  // 1 - IN only

                ioReturnCode,  // 2

                ioIdTimestampInizioVal,  // 3

                ioIdTimestampFineVal,  // 4

                ioIdDataInizioVal,  // 5

                ioIdDataFineVal,  // 6

                ioDataCont,  // 7

                ioIdRiga,  // 8

                ioFlagTrovato,  // 9

                ioIdLock,  // 10

                record.ioFlag1(),  // 11 - IN only

                record.ioFlagAggiornaDb(),  // 12 - IN only

                ioConcurrentTempUpdate,  // 13

                record.ioFlUpdate(),  // 14 - IN only

                ioTipoStoricita,  // 15

                sSeqEventoContratto,  // 16

                sTipoRifEsterno,  // 17

                sCodRifEsterno,  // 18

                sCodSistemaEsterno,  // 19

                sCodRifSistEsterno,  // 20

                sCodRegimeFiscale,  // 21

                sFlagDetraibilita,  // 22

                sFlagRinPrestiti,  // 23

                sDataPerfezionamento,  // 24

                sCodRapporto,  // 25

                sCodStatoContratto,  // 26

                sDataDecorrenza,  // 27

                sDataScadenza,  // 28

                sCodCoassicurazione,  // 29

                sFlagVisitaMedica,  // 30

                sCodComptoTabag,  // 31

                sFlagOpzioneRendita,  // 32

                sFlagTrasformazione,  // 33

                sNumMesiResidRisc,  // 34

                sImpImponProvvTrasf,  // 35

                sCodiceRamo,  // 36

                sSeqConv,  // 37

                sSeqRapporto,  // 38

                sTipoIndividuale,  // 39

                sCodiceCompagniaPvg,  // 40

                sIdOperatore,  // 41

                sIdTimestampInizioVal,  // 42

                sIdFunzione,  // 43

                sIdOperazione,  // 44

                sIdStatoElab,  // 45

                sIdLock,  // 46

                sIdCommento,  // 47

                sNumMesiPror,  // 48

                sDataRiprPagPremi,  // 49

                sImpPrestazMaxFiscv,  // 50

                sTipoIndivLegale,  // 51

                sCodiceSistemaProven,  // 52

                sTipoOrigEmiss,  // 53

                sNumAnnoCoorte  // 54

            );

            

            IoParameters updatedIoParams = new IoParameters(

                ioParameters.functionCode(),

                ioReturnCode != null ? ioReturnCode.intValue() : null,

                ioParameters.flagAggiornaDb(),

                ioParameters.flag1(),

                ioTipoStoricita,

                ioFlagTrovato,

                ioIdTimestampInizioVal,

                ioIdTimestampFineVal,

                ioIdDataInizioVal,

                ioIdDataFineVal,

                ioDataCont,

                ioIdRiga,

                ioIdLock,

                ioParameters.flUpdate(),

                ioConcurrentTempUpdate,

                ioParameters.timestampApp(),

                ioParameters.sqlerrmc(),

                ioParameters.livLog(),

                ioParameters.sessionId()

            );

            

            return new CrudModuleResult<>(updatedIoParams, outRecord, "VPO01100", "INDIVIDUALE_S", System.currentTimeMillis() - start);

            } catch (java.sql.SQLException e) {

                throw new it.svg.crud.exception.CrudDataAccessException("Error reading OUT parameters from VPO01100", e);

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
