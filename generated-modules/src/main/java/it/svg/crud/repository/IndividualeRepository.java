package it.svg.crud.repository;

import it.svg.crud.model.dto.CrudModuleResult;
import it.svg.crud.model.dto.IoParameters;
import it.svg.crud.model.dto.IndividualeRecord;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository("crudIndividualeRepository")
public class IndividualeRepository extends AbstractOracleProcedureRepository {

    public IndividualeRepository(DataSource dataSource,
                                       MeterRegistry meterRegistry,
                                       CircuitBreakerRegistry circuitBreakerRegistry) {
        super(dataSource, meterRegistry, circuitBreakerRegistry);
    }

    public CrudModuleResult<IndividualeRecord> execute(IoParameters ioParameters, IndividualeRecord record) {
        String sql = "{ call VPO01100(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        long start = System.currentTimeMillis();
        return execute(sql, "VPO01100", cs -> {

            cs.setObject(1, record.ioFunctionCode() == null ? null : record.ioFunctionCode());

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

            cs.setObject(11, record.ioFlag1() == null ? null : record.ioFlag1());

            cs.setObject(12, record.ioFlagAggiornaDb() == null ? null : record.ioFlagAggiornaDb());

            cs.setObject(13, record.ioConcurrentTempUpdate() == null ? null : record.ioConcurrentTempUpdate(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(13, java.sql.Types.VARCHAR);

            cs.setObject(14, record.ioFlUpdate() == null ? null : record.ioFlUpdate());

            cs.setObject(15, record.ioTipoStoricita() == null ? null : record.ioTipoStoricita(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(15, java.sql.Types.VARCHAR);

            cs.setObject(16, record.sSeqEventoContratto() == null ? null : record.sSeqEventoContratto(), java.sql.Types.BIGINT);

            cs.registerOutParameter(16, java.sql.Types.BIGINT);

            cs.setObject(17, record.sTipoRifEsterno() == null ? null : record.sTipoRifEsterno(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(17, java.sql.Types.VARCHAR);

            cs.setObject(18, record.sCodRifEsterno() == null ? null : record.sCodRifEsterno(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(18, java.sql.Types.VARCHAR);

            cs.setObject(19, record.sCodSistemaEsterno() == null ? null : record.sCodSistemaEsterno(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(19, java.sql.Types.VARCHAR);

            cs.setObject(20, record.sCodRifSistEsterno() == null ? null : record.sCodRifSistEsterno(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(20, java.sql.Types.VARCHAR);

            cs.setObject(21, record.sCodRegimeFiscale() == null ? null : record.sCodRegimeFiscale(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(21, java.sql.Types.VARCHAR);

            cs.setObject(22, record.sFlagDetraibilita() == null ? null : record.sFlagDetraibilita(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(22, java.sql.Types.VARCHAR);

            cs.setObject(23, record.sFlagRinPrestiti() == null ? null : record.sFlagRinPrestiti(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(23, java.sql.Types.VARCHAR);

            cs.setObject(24, record.sDataPerfezionamento() == null ? null : record.sDataPerfezionamento(), java.sql.Types.DATE);

            cs.registerOutParameter(24, java.sql.Types.DATE);

            cs.setObject(25, record.sCodRapporto() == null ? null : record.sCodRapporto(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(25, java.sql.Types.VARCHAR);

            cs.setObject(26, record.sCodStatoContratto() == null ? null : record.sCodStatoContratto(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(26, java.sql.Types.VARCHAR);

            cs.setObject(27, record.sDataDecorrenza() == null ? null : record.sDataDecorrenza(), java.sql.Types.DATE);

            cs.registerOutParameter(27, java.sql.Types.DATE);

            cs.setObject(28, record.sDataScadenza() == null ? null : record.sDataScadenza(), java.sql.Types.DATE);

            cs.registerOutParameter(28, java.sql.Types.DATE);

            cs.setObject(29, record.sCodCoassicurazione() == null ? null : record.sCodCoassicurazione(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(29, java.sql.Types.VARCHAR);

            cs.setObject(30, record.sFlagVisitaMedica() == null ? null : record.sFlagVisitaMedica(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(30, java.sql.Types.VARCHAR);

            cs.setObject(31, record.sCodComptoTabag() == null ? null : record.sCodComptoTabag(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(31, java.sql.Types.VARCHAR);

            cs.setObject(32, record.sFlagOpzioneRendita() == null ? null : record.sFlagOpzioneRendita(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(32, java.sql.Types.VARCHAR);

            cs.setObject(33, record.sFlagTrasformazione() == null ? null : record.sFlagTrasformazione(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(33, java.sql.Types.VARCHAR);

            cs.setObject(34, record.sNumMesiResidRisc() == null ? null : record.sNumMesiResidRisc(), java.sql.Types.BIGINT);

            cs.registerOutParameter(34, java.sql.Types.BIGINT);

            cs.setObject(35, record.sImpImponProvvTrasf() == null ? null : record.sImpImponProvvTrasf(), java.sql.Types.BIGINT);

            cs.registerOutParameter(35, java.sql.Types.BIGINT);

            cs.setObject(36, record.sCodiceRamo() == null ? null : record.sCodiceRamo(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(36, java.sql.Types.VARCHAR);

            cs.setObject(37, record.sSeqConv() == null ? null : record.sSeqConv(), java.sql.Types.BIGINT);

            cs.registerOutParameter(37, java.sql.Types.BIGINT);

            cs.setObject(38, record.sSeqRapporto() == null ? null : record.sSeqRapporto(), java.sql.Types.BIGINT);

            cs.registerOutParameter(38, java.sql.Types.BIGINT);

            cs.setObject(39, record.sTipoIndividuale() == null ? null : record.sTipoIndividuale(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(39, java.sql.Types.VARCHAR);

            cs.setObject(40, record.sCodiceCompagniaPvg() == null ? null : record.sCodiceCompagniaPvg(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(40, java.sql.Types.VARCHAR);

            cs.setObject(41, record.sIdOperatore() == null ? null : record.sIdOperatore(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(41, java.sql.Types.VARCHAR);

            cs.setObject(42, record.sIdTimestampInizioVal() == null ? null : record.sIdTimestampInizioVal(), java.sql.Types.BIGINT);

            cs.registerOutParameter(42, java.sql.Types.BIGINT);

            cs.setObject(43, record.sIdFunzione() == null ? null : record.sIdFunzione(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(43, java.sql.Types.VARCHAR);

            cs.setObject(44, record.sIdOperazione() == null ? null : record.sIdOperazione(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(44, java.sql.Types.VARCHAR);

            cs.setObject(45, record.sIdStatoElab() == null ? null : record.sIdStatoElab(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(45, java.sql.Types.VARCHAR);

            cs.setObject(46, record.sIdLock() == null ? null : record.sIdLock(), java.sql.Types.BIGINT);

            cs.registerOutParameter(46, java.sql.Types.BIGINT);

            cs.setObject(47, record.sIdCommento() == null ? null : record.sIdCommento(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(47, java.sql.Types.VARCHAR);

            cs.setObject(48, record.sNumMesiPror() == null ? null : record.sNumMesiPror(), java.sql.Types.BIGINT);

            cs.registerOutParameter(48, java.sql.Types.BIGINT);

            cs.setObject(49, record.sDataRiprPagPremi() == null ? null : record.sDataRiprPagPremi(), java.sql.Types.DATE);

            cs.registerOutParameter(49, java.sql.Types.DATE);

            cs.setObject(50, record.sImpPrestazMaxFiscv() == null ? null : record.sImpPrestazMaxFiscv(), java.sql.Types.BIGINT);

            cs.registerOutParameter(50, java.sql.Types.BIGINT);

            cs.setObject(51, record.sTipoIndivLegale() == null ? null : record.sTipoIndivLegale(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(51, java.sql.Types.VARCHAR);

            cs.setObject(52, record.sCodiceSistemaProven() == null ? null : record.sCodiceSistemaProven(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(52, java.sql.Types.VARCHAR);

            cs.setObject(53, record.sTipoOrigEmiss() == null ? null : record.sTipoOrigEmiss(), java.sql.Types.VARCHAR);

            cs.registerOutParameter(53, java.sql.Types.VARCHAR);

            cs.setObject(54, record.sNumAnnoCoorte() == null ? null : record.sNumAnnoCoorte(), java.sql.Types.BIGINT);

            cs.registerOutParameter(54, java.sql.Types.BIGINT);

        }, cs -> {
            try {
            // Lettura parametri IO_ di output (INOUT)
            Long ioReturnCode             = cs.getObject(2, Long.class);
            Long ioIdTimestampInizioVal   = cs.getObject(3, Long.class);
            Long ioIdTimestampFineVal     = cs.getObject(4, Long.class);
            String ioIdDataInizioVal      = cs.getString(5);
            String ioIdDataFineVal        = cs.getString(6);
            String ioDataCont             = cs.getString(7);
            Long ioIdRiga                 = cs.getObject(8, Long.class);
            String ioFlagTrovato          = cs.getString(9);
            Long ioIdLock                 = cs.getObject(10, Long.class);
            String ioConcurrentTempUpdate = cs.getString(13);
            String ioTipoStoricita        = cs.getString(15);

            // Lettura parametri S_ di output (OUT/INOUT)
            Long sSeqEventoContratto      = cs.getObject(16, Long.class);
            String sTipoRifEsterno        = cs.getString(17);
            String sCodRifEsterno         = cs.getString(18);
            String sCodSistemaEsterno     = cs.getString(19);
            String sCodRifSistEsterno     = cs.getString(20);
            String sCodRegimeFiscale      = cs.getString(21);
            String sFlagDetraibilita      = cs.getString(22);
            String sFlagRinPrestiti       = cs.getString(23);
            java.sql.Date rawDataPerfez   = cs.getDate(24);
            java.time.LocalDate sDataPerfezionamento = rawDataPerfez != null ? rawDataPerfez.toLocalDate() : null;
            String sCodRapporto           = cs.getString(25);
            String sCodStatoContratto     = cs.getString(26);
            java.sql.Date rawDataDecorr   = cs.getDate(27);
            java.time.LocalDate sDataDecorrenza = rawDataDecorr != null ? rawDataDecorr.toLocalDate() : null;
            java.sql.Date rawDataScad     = cs.getDate(28);
            java.time.LocalDate sDataScadenza = rawDataScad != null ? rawDataScad.toLocalDate() : null;
            String sCodCoassicurazione    = cs.getString(29);
            String sFlagVisitaMedica      = cs.getString(30);
            String sCodComptoTabag        = cs.getString(31);
            String sFlagOpzioneRendita    = cs.getString(32);
            String sFlagTrasformazione    = cs.getString(33);
            Long sNumMesiResidRisc        = cs.getObject(34, Long.class);
            Long sImpImponProvvTrasf      = cs.getObject(35, Long.class);
            String sCodiceRamo            = cs.getString(36);
            Long sSeqConv                 = cs.getObject(37, Long.class);
            Long sSeqRapporto             = cs.getObject(38, Long.class);
            String sTipoIndividuale       = cs.getString(39);
            String sCodiceCompagniaPvg    = cs.getString(40);
            String sIdOperatore           = cs.getString(41);
            Long sIdTimestampInizioVal    = cs.getObject(42, Long.class);
            String sIdFunzione            = cs.getString(43);
            String sIdOperazione          = cs.getString(44);
            String sIdStatoElab           = cs.getString(45);
            Long sIdLock                  = cs.getObject(46, Long.class);
            String sIdCommento            = cs.getString(47);
            Long sNumMesiPror             = cs.getObject(48, Long.class);
            java.sql.Date rawDataRipr     = cs.getDate(49);
            java.time.LocalDate sDataRiprPagPremi = rawDataRipr != null ? rawDataRipr.toLocalDate() : null;
            Long sImpPrestazMaxFiscv      = cs.getObject(50, Long.class);
            String sTipoIndivLegale       = cs.getString(51);
            String sCodiceSistemaProven   = cs.getString(52);
            String sTipoOrigEmiss         = cs.getString(53);
            Long sNumAnnoCoorte           = cs.getObject(54, Long.class);

            IndividualeRecord outRecord = new IndividualeRecord(
                record.ioFunctionCode(),     // 1  - IN only
                ioReturnCode,               // 2
                ioIdTimestampInizioVal,     // 3
                ioIdTimestampFineVal,       // 4
                ioIdDataInizioVal,          // 5
                ioIdDataFineVal,            // 6
                ioDataCont,                 // 7
                ioIdRiga,                   // 8
                ioFlagTrovato,              // 9
                ioIdLock,                   // 10
                record.ioFlag1(),           // 11 - IN only
                record.ioFlagAggiornaDb(),  // 12 - IN only
                ioConcurrentTempUpdate,     // 13
                record.ioFlUpdate(),        // 14 - IN only
                ioTipoStoricita,            // 15
                sSeqEventoContratto,        // 16
                sTipoRifEsterno,            // 17
                sCodRifEsterno,             // 18
                sCodSistemaEsterno,         // 19
                sCodRifSistEsterno,         // 20
                sCodRegimeFiscale,          // 21
                sFlagDetraibilita,          // 22
                sFlagRinPrestiti,           // 23
                sDataPerfezionamento,       // 24
                sCodRapporto,               // 25
                sCodStatoContratto,         // 26
                sDataDecorrenza,            // 27
                sDataScadenza,              // 28
                sCodCoassicurazione,        // 29
                sFlagVisitaMedica,          // 30
                sCodComptoTabag,            // 31
                sFlagOpzioneRendita,        // 32
                sFlagTrasformazione,        // 33
                sNumMesiResidRisc,          // 34
                sImpImponProvvTrasf,        // 35
                sCodiceRamo,                // 36
                sSeqConv,                   // 37
                sSeqRapporto,               // 38
                sTipoIndividuale,           // 39
                sCodiceCompagniaPvg,        // 40
                sIdOperatore,               // 41
                sIdTimestampInizioVal,      // 42
                sIdFunzione,               // 43
                sIdOperazione,              // 44
                sIdStatoElab,              // 45
                sIdLock,                   // 46
                sIdCommento,               // 47
                sNumMesiPror,              // 48
                sDataRiprPagPremi,         // 49
                sImpPrestazMaxFiscv,       // 50
                sTipoIndivLegale,          // 51
                sCodiceSistemaProven,      // 52
                sTipoOrigEmiss,            // 53
                sNumAnnoCoorte             // 54
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
}
