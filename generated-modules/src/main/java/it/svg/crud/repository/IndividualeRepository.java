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

            cs.setObject(2, record.ioReturnCode() == null ? null : record.ioReturnCode());

            cs.registerOutParameter(2, java.sql.Types.BIGINT);

            cs.setObject(3, record.ioIdTimestampInizioVal() == null ? null : record.ioIdTimestampInizioVal());

            cs.registerOutParameter(3, java.sql.Types.BIGINT);

            cs.setObject(4, record.ioIdTimestampFineVal() == null ? null : record.ioIdTimestampFineVal());

            cs.registerOutParameter(4, java.sql.Types.BIGINT);

            cs.setObject(5, record.ioIdDataInizioVal() == null ? null : record.ioIdDataInizioVal());

            cs.registerOutParameter(5, java.sql.Types.VARCHAR);

            cs.setObject(6, record.ioIdDataFineVal() == null ? null : record.ioIdDataFineVal());

            cs.registerOutParameter(6, java.sql.Types.VARCHAR);

            cs.setObject(7, record.ioDataCont() == null ? null : record.ioDataCont());

            cs.registerOutParameter(7, java.sql.Types.VARCHAR);

            cs.setObject(8, record.ioIdRiga() == null ? null : record.ioIdRiga());

            cs.registerOutParameter(8, java.sql.Types.BIGINT);

            cs.setObject(9, record.ioFlagTrovato() == null ? null : record.ioFlagTrovato());

            cs.registerOutParameter(9, java.sql.Types.VARCHAR);

            cs.setObject(10, record.ioIdLock() == null ? null : record.ioIdLock());

            cs.registerOutParameter(10, java.sql.Types.BIGINT);

            cs.setObject(11, record.ioFlag1() == null ? null : record.ioFlag1());

            cs.setObject(12, record.ioFlagAggiornaDb() == null ? null : record.ioFlagAggiornaDb());

            cs.setObject(13, record.ioConcurrentTempUpdate() == null ? null : record.ioConcurrentTempUpdate());

            cs.registerOutParameter(13, java.sql.Types.VARCHAR);

            cs.setObject(14, record.ioFlUpdate() == null ? null : record.ioFlUpdate());

            cs.setObject(15, record.ioTipoStoricita() == null ? null : record.ioTipoStoricita());

            cs.registerOutParameter(15, java.sql.Types.VARCHAR);

            cs.setObject(16, record.sSeqEventoContratto() == null ? null : record.sSeqEventoContratto());

            cs.registerOutParameter(16, java.sql.Types.BIGINT);

            cs.setObject(17, record.sTipoRifEsterno() == null ? null : record.sTipoRifEsterno());

            cs.registerOutParameter(17, java.sql.Types.VARCHAR);

            cs.setObject(18, record.sCodRifEsterno() == null ? null : record.sCodRifEsterno());

            cs.registerOutParameter(18, java.sql.Types.VARCHAR);

            cs.setObject(19, record.sCodSistemaEsterno() == null ? null : record.sCodSistemaEsterno());

            cs.registerOutParameter(19, java.sql.Types.VARCHAR);

            cs.setObject(20, record.sCodRifSistEsterno() == null ? null : record.sCodRifSistEsterno());

            cs.registerOutParameter(20, java.sql.Types.VARCHAR);

            cs.setObject(21, record.sCodRegimeFiscale() == null ? null : record.sCodRegimeFiscale());

            cs.registerOutParameter(21, java.sql.Types.VARCHAR);

            cs.setObject(22, record.sFlagDetraibilita() == null ? null : record.sFlagDetraibilita());

            cs.registerOutParameter(22, java.sql.Types.VARCHAR);

            cs.setObject(23, record.sFlagRinPrestiti() == null ? null : record.sFlagRinPrestiti());

            cs.registerOutParameter(23, java.sql.Types.VARCHAR);

            cs.setObject(24, record.sDataPerfezionamento() == null ? null : record.sDataPerfezionamento());

            cs.registerOutParameter(24, java.sql.Types.DATE);

            cs.setObject(25, record.sCodRapporto() == null ? null : record.sCodRapporto());

            cs.registerOutParameter(25, java.sql.Types.VARCHAR);

            cs.setObject(26, record.sCodStatoContratto() == null ? null : record.sCodStatoContratto());

            cs.registerOutParameter(26, java.sql.Types.VARCHAR);

            cs.setObject(27, record.sDataDecorrenza() == null ? null : record.sDataDecorrenza());

            cs.registerOutParameter(27, java.sql.Types.DATE);

            cs.setObject(28, record.sDataScadenza() == null ? null : record.sDataScadenza());

            cs.registerOutParameter(28, java.sql.Types.DATE);

            cs.setObject(29, record.sCodCoassicurazione() == null ? null : record.sCodCoassicurazione());

            cs.registerOutParameter(29, java.sql.Types.VARCHAR);

            cs.setObject(30, record.sFlagVisitaMedica() == null ? null : record.sFlagVisitaMedica());

            cs.registerOutParameter(30, java.sql.Types.VARCHAR);

            cs.setObject(31, record.sCodComptoTabag() == null ? null : record.sCodComptoTabag());

            cs.registerOutParameter(31, java.sql.Types.VARCHAR);

            cs.setObject(32, record.sFlagOpzioneRendita() == null ? null : record.sFlagOpzioneRendita());

            cs.registerOutParameter(32, java.sql.Types.VARCHAR);

            cs.setObject(33, record.sFlagTrasformazione() == null ? null : record.sFlagTrasformazione());

            cs.registerOutParameter(33, java.sql.Types.VARCHAR);

            cs.setObject(34, record.sNumMesiResidRisc() == null ? null : record.sNumMesiResidRisc());

            cs.registerOutParameter(34, java.sql.Types.BIGINT);

            cs.setObject(35, record.sImpImponProvvTrasf() == null ? null : record.sImpImponProvvTrasf());

            cs.registerOutParameter(35, java.sql.Types.BIGINT);

            cs.setObject(36, record.sCodiceRamo() == null ? null : record.sCodiceRamo());

            cs.registerOutParameter(36, java.sql.Types.VARCHAR);

            cs.setObject(37, record.sSeqConv() == null ? null : record.sSeqConv());

            cs.registerOutParameter(37, java.sql.Types.BIGINT);

            cs.setObject(38, record.sSeqRapporto() == null ? null : record.sSeqRapporto());

            cs.registerOutParameter(38, java.sql.Types.BIGINT);

            cs.setObject(39, record.sTipoIndividuale() == null ? null : record.sTipoIndividuale());

            cs.registerOutParameter(39, java.sql.Types.VARCHAR);

            cs.setObject(40, record.sCodiceCompagniaPvg() == null ? null : record.sCodiceCompagniaPvg());

            cs.registerOutParameter(40, java.sql.Types.VARCHAR);

            cs.setObject(41, record.sIdOperatore() == null ? null : record.sIdOperatore());

            cs.registerOutParameter(41, java.sql.Types.VARCHAR);

            cs.setObject(42, record.sIdTimestampInizioVal() == null ? null : record.sIdTimestampInizioVal());

            cs.registerOutParameter(42, java.sql.Types.BIGINT);

            cs.setObject(43, record.sIdFunzione() == null ? null : record.sIdFunzione());

            cs.registerOutParameter(43, java.sql.Types.VARCHAR);

            cs.setObject(44, record.sIdOperazione() == null ? null : record.sIdOperazione());

            cs.registerOutParameter(44, java.sql.Types.VARCHAR);

            cs.setObject(45, record.sIdStatoElab() == null ? null : record.sIdStatoElab());

            cs.registerOutParameter(45, java.sql.Types.VARCHAR);

            cs.setObject(46, record.sIdLock() == null ? null : record.sIdLock());

            cs.registerOutParameter(46, java.sql.Types.BIGINT);

            cs.setObject(47, record.sIdCommento() == null ? null : record.sIdCommento());

            cs.registerOutParameter(47, java.sql.Types.VARCHAR);

            cs.setObject(48, record.sNumMesiPror() == null ? null : record.sNumMesiPror());

            cs.registerOutParameter(48, java.sql.Types.BIGINT);

            cs.setObject(49, record.sDataRiprPagPremi() == null ? null : record.sDataRiprPagPremi());

            cs.registerOutParameter(49, java.sql.Types.DATE);

            cs.setObject(50, record.sImpPrestazMaxFiscv() == null ? null : record.sImpPrestazMaxFiscv());

            cs.registerOutParameter(50, java.sql.Types.BIGINT);

            cs.setObject(51, record.sTipoIndivLegale() == null ? null : record.sTipoIndivLegale());

            cs.registerOutParameter(51, java.sql.Types.VARCHAR);

            cs.setObject(52, record.sCodiceSistemaProven() == null ? null : record.sCodiceSistemaProven());

            cs.registerOutParameter(52, java.sql.Types.VARCHAR);

            cs.setObject(53, record.sTipoOrigEmiss() == null ? null : record.sTipoOrigEmiss());

            cs.registerOutParameter(53, java.sql.Types.VARCHAR);

            cs.setObject(54, record.sNumAnnoCoorte() == null ? null : record.sNumAnnoCoorte());

            cs.registerOutParameter(54, java.sql.Types.BIGINT);

        }, cs -> {

            // TODO leggere OUT param IO_RETURN_CODE posizione 2

            // TODO leggere OUT param IO_ID_TIMESTAMP_INIZIO_VAL posizione 3

            // TODO leggere OUT param IO_ID_TIMESTAMP_FINE_VAL posizione 4

            // TODO leggere OUT param IO_ID_DATA_INIZIO_VAL posizione 5

            // TODO leggere OUT param IO_ID_DATA_FINE_VAL posizione 6

            // TODO leggere OUT param IO_DATA_CONT posizione 7

            // TODO leggere OUT param IO_ID_RIGA posizione 8

            // TODO leggere OUT param IO_FLAG_TROVATO posizione 9

            // TODO leggere OUT param IO_ID_LOCK posizione 10

            // TODO leggere OUT param IO_CONCURRENT_TEMP_UPDATE posizione 13

            // TODO leggere OUT param IO_TIPO_STORICITA posizione 15

            // TODO leggere OUT param S_SEQ_EVENTO_CONTRATTO posizione 16

            // TODO leggere OUT param S_TIPO_RIF_ESTERNO posizione 17

            // TODO leggere OUT param S_COD_RIF_ESTERNO posizione 18

            // TODO leggere OUT param S_COD_SISTEMA_ESTERNO posizione 19

            // TODO leggere OUT param S_COD_RIF_SIST_ESTERNO posizione 20

            // TODO leggere OUT param S_COD_REGIME_FISCALE posizione 21

            // TODO leggere OUT param S_FLAG_DETRAIBILITA posizione 22

            // TODO leggere OUT param S_FLAG_RIN_PRESTITI posizione 23

            // TODO leggere OUT param S_DATA_PERFEZIONAMENTO posizione 24

            // TODO leggere OUT param S_COD_RAPPORTO posizione 25

            // TODO leggere OUT param S_COD_STATO_CONTRATTO posizione 26

            // TODO leggere OUT param S_DATA_DECORRENZA posizione 27

            // TODO leggere OUT param S_DATA_SCADENZA posizione 28

            // TODO leggere OUT param S_COD_COASSICURAZIONE posizione 29

            // TODO leggere OUT param S_FLAG_VISITA_MEDICA posizione 30

            // TODO leggere OUT param S_COD_COMPTO_TABAG posizione 31

            // TODO leggere OUT param S_FLAG_OPZIONE_RENDITA posizione 32

            // TODO leggere OUT param S_FLAG_TRASFORMAZIONE posizione 33

            // TODO leggere OUT param S_NUM_MESI_RESID_RISC posizione 34

            // TODO leggere OUT param S_IMP_IMPON_PROVV_TRASF posizione 35

            // TODO leggere OUT param S_CODICE_RAMO posizione 36

            // TODO leggere OUT param S_SEQ_CONV posizione 37

            // TODO leggere OUT param S_SEQ_RAPPORTO posizione 38

            // TODO leggere OUT param S_TIPO_INDIVIDUALE posizione 39

            // TODO leggere OUT param S_CODICE_COMPAGNIA_PVG posizione 40

            // TODO leggere OUT param S_ID_OPERATORE posizione 41

            // TODO leggere OUT param S_ID_TIMESTAMP_INIZIO_VAL posizione 42

            // TODO leggere OUT param S_ID_FUNZIONE posizione 43

            // TODO leggere OUT param S_ID_OPERAZIONE posizione 44

            // TODO leggere OUT param S_ID_STATO_ELAB posizione 45

            // TODO leggere OUT param S_ID_LOCK posizione 46

            // TODO leggere OUT param S_ID_COMMENTO posizione 47

            // TODO leggere OUT param S_NUM_MESI_PROR posizione 48

            // TODO leggere OUT param S_DATA_RIPR_PAG_PREMI posizione 49

            // TODO leggere OUT param S_IMP_PRESTAZ_MAX_FISCV posizione 50

            // TODO leggere OUT param S_TIPO_INDIV_LEGALE posizione 51

            // TODO leggere OUT param S_CODICE_SISTEMA_PROVEN posizione 52

            // TODO leggere OUT param S_TIPO_ORIG_EMISS posizione 53

            // TODO leggere OUT param S_NUM_ANNO_COORTE posizione 54

            return new CrudModuleResult<>(ioParameters, record, "VPO01100", "INDIVIDUALE_S", System.currentTimeMillis() - start);
        });
    }
}