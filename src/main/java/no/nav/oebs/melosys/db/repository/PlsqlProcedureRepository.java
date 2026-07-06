package no.nav.oebs.melosys.db.repository;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.sql.DataSource;

import no.nav.oebs.melosys.config.common.logging.LoggingUtils;
import no.nav.oebs.melosys.config.common.mdc.MdcOperations;
import no.nav.oebs.melosys.db.entity.KallLogg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

/**
 * Repository for kall til generelle PL/SQL-prosedyrer som implementerer uttrekk eller forretningslogikken til REST-tjenester.
 */
@Slf4j
@Repository
public class PlsqlProcedureRepository {

	private static final String DATA_IN_PARAM = "data_in";
	private static final String DATA_OUT_PARAM = "data_out";
	private static final String MESSAGE_NO_PARAM = "msg_no";
	private static final String MESSAGE_PARAM = "msg";
	private static final int PROCEDURE_NAME_NO_SCHEMA_TOKENS = 2;
	private static final int PROCEDURE_NAME_WITH_SCHEMA_TOKENS = 3;

	private KallLoggRepository kallLoggRepository;

	private JdbcTemplate jdbcTemplate;

	private ConcurrentMap<String, SimpleJdbcCall> jdbcCallCache = new ConcurrentHashMap<>();

	@Autowired
	public PlsqlProcedureRepository(DataSource dataSource, KallLoggRepository kallLoggRepository) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.setResultsMapCaseInsensitive(true);

		this.kallLoggRepository = kallLoggRepository;
	}

	/**
	 * Eksekverer spesifisert SQL/PL-prosedyre som har både input- og outputdata.
	 * <p>
	 * Formatet til prosedyren skal være:
	 * <p>
	 * <code>pakkenavn.prosedyrenavn(id VARCHAR2, data_in CLOB, data_out CLOB, msg_no NUMBER, msg VARCHAR2)</code>.
	 * 
	 * @param procedureName
	 *            navn på PL/SQL-prosedyren på formatet <code>pakkenavn.prosedyrenavn</code>.
	 * @param dataIn
	 *            inputdata til prosedyren.
	 * @return Resultatet fra prosedyren.
	 * @throws SQLException
	 *             dersom det oppstår en feil relatert til clob-parameteren som returneres fra prosedyren.
	 */
	public PlsqlProcedureResult executeInOutProcedure(String procedureName, String dataIn) {
		PlsqlProcedureResult result = null;
		Exception exception = null;
		long startTime = System.currentTimeMillis();

		try {

			validateProcedureName(procedureName);

			SimpleJdbcCall jdbcCall = getJdbcCall(procedureName, //
					new SqlParameter(DATA_IN_PARAM, Types.CLOB), //
					new SqlOutParameter(MESSAGE_NO_PARAM, Types.NUMERIC), //
					new SqlOutParameter(MESSAGE_PARAM, Types.VARCHAR));

			SqlParameterSource inParams = new MapSqlParameterSource() //
					.addValue(DATA_IN_PARAM, dataIn);

			result = executeProcedure(jdbcCall, inParams);

			return result;
		} catch (Exception e) {
			exception = e;
			log.error("EN FEIL HAR OPPSTÅTT");
			throw e;
		} finally {
			long endTime = System.currentTimeMillis();
			log.info("*************************");
			log.info("Resultatet av kallet er {}", PlsqlProcedureResult.getMessage(result));
			log.info("Logger prosedyrekall: ");
			logProcedureCall(procedureName, dataIn, result, endTime - startTime, exception);
		}
	}

	/**
	 * Eksekverer spesifisert SQL/PL-prosedyre som har både input- og outputdata.
	 * <p>
	 * Formatet til prosedyren skal være:
	 * <p>
	 * <code>pakkenavn.prosedyrenavn(id VARCHAR2, data_out CLOB, msg_no NUMBER, msg VARCHAR2)</code>.
	 *
	 * @param procedureName
	 *            navn på PL/SQL-prosedyren på formatet <code>pakkenavn.prosedyrenavn</code>
	 *            inputdata til prosedyren.
	 * @return Resultatet fra prosedyren.
	 * @throws SQLException
	 *             dersom det oppstår en feil relatert til clob-parameteren som returneres fra prosedyren.
	 */
	public PlsqlProcedureResult executeOutProcedure(String procedureName) {
		PlsqlProcedureResult result = null;
		Exception exception = null;
		long startTime = System.currentTimeMillis();

		try {

			validateProcedureName(procedureName);

			SimpleJdbcCall jdbcCall = getJdbcCall(procedureName, //
					new SqlOutParameter(DATA_OUT_PARAM, Types.CLOB),
					new SqlOutParameter(MESSAGE_NO_PARAM, Types.NUMERIC), //
					new SqlOutParameter(MESSAGE_PARAM, Types.VARCHAR));

			result = executeOutProcedure(jdbcCall);

			return result;
		} catch (Exception e) {
			exception = e;
			log.error("EN FEIL HAR OPPSTÅTT");
			throw e;
		} finally {
			long endTime = System.currentTimeMillis();
			log.info("*************************");
			log.info("Resultatet av kallet er {}", PlsqlProcedureResult.getMessage(result));
			log.info("Logger prosedyrekall: ");
			logProcedureCall(procedureName,"", result, endTime - startTime, exception);
		}
	}

	/**
	 * Sjekker at formatet på prosedyrenavnet er korrekt. Kaster en <code>IllegalArgumentException</code> dersom ikke.
	 */
	private void validateProcedureName(String procedureName) {
		int tokenCount = procedureName.split("\\.").length;
		if (tokenCount != PROCEDURE_NAME_NO_SCHEMA_TOKENS && tokenCount != PROCEDURE_NAME_WITH_SCHEMA_TOKENS) {
			throw new IllegalArgumentException(
					"Feil format på PL/SQL-prosedyrenavnet '" + procedureName
							+ "'; skal ha format 'pakkenavn.prosedyrenavn' eller 'schema.pakkenavn.prosedyrenavn'");
		}
	}

	/**
	 * Returnerer et SimpleJdbcCall-objekt; enten fra cache eller et nytt objekt.
	 */
	private SimpleJdbcCall getJdbcCall(String procedureName, SqlParameter... declaredParameters) {
		SimpleJdbcCall jdbcCall = jdbcCallCache.get(procedureName);
		if (jdbcCall == null) {
			String[] tokens = procedureName.split("\\.");
			String packageName = tokens[tokens.length - 2];
			String procedure = tokens[tokens.length - 1];

			SimpleJdbcCall jdbcCallBuilder = new SimpleJdbcCall(jdbcTemplate) //
					.withCatalogName(packageName) //
					.withProcedureName(procedure) //
					.withoutProcedureColumnMetaDataAccess();

			if (tokens.length == PROCEDURE_NAME_WITH_SCHEMA_TOKENS) {
				jdbcCallBuilder = jdbcCallBuilder.withSchemaName(tokens[0]);
			}

			jdbcCall = jdbcCallBuilder.declareParameters(declaredParameters);

			jdbcCallCache.put(procedureName, jdbcCall);

			log.debug("Oppretter og cacher SimpleJdbcCall-objekt for '" + procedureName + "'");
		} else {
			log.debug("Gjenbruker cachet SimpleJdbcCall-objekt for '" + procedureName + "'");
		}
		return jdbcCall;
	}

	/**
	 * Eksekverer PL/SQL-prosedyren gitt ved SimpleJdbcCall-objektet og dekoder utparametere.
	 */
	private PlsqlProcedureResult executeProcedure(SimpleJdbcCall jdbcCall, SqlParameterSource inParams) {
		Map<String, Object> outParams = jdbcCall.execute(inParams);

		Clob dataOut = (Clob) outParams.get(DATA_OUT_PARAM);
		BigDecimal messageNumber = (BigDecimal) outParams.get(MESSAGE_NO_PARAM);
		String message = (String) outParams.get(MESSAGE_PARAM);

		return new PlsqlProcedureResult(dataOut, messageNumber, message);
	}

	/**
	 * Eksekverer PL/SQL-prosedyren gitt ved SimpleJdbcCall-objektet og dekoder utparametere.
	 */
	private PlsqlProcedureResult executeOutProcedure(SimpleJdbcCall jdbcCall) {
		Map<String, Object> outParams = jdbcCall.execute();

		Clob dataOut = (Clob) outParams.get(DATA_OUT_PARAM);
		BigDecimal messageNumber = (BigDecimal) outParams.get(MESSAGE_NO_PARAM);
		String message = (String) outParams.get(MESSAGE_PARAM);

		return new PlsqlProcedureResult(dataOut, messageNumber, message);
	}
	/**
	 * Logger PL/SQL-prosedyrekallet til kalloggen.
	 */
	private void logProcedureCall(String procedureName, String dataIn, PlsqlProcedureResult result, long executionTime,
			Exception exception) {

		String correlationId = MdcOperations.get(MdcOperations.MDC_CORRELATION_ID);

		KallLogg kallLogg = KallLogg.builder() //
				.korrelasjonId(MdcOperations.generateCorrelationId())
				.tidspunkt(LocalDateTime.now(ZoneId.systemDefault())) //
				.type(KallLogg.TYPE_PLSQL) //
				.kallRetning(KallLogg.RETNING_UT) //
				.operation(procedureName) //
				.status(exception != null //
						? Integer.valueOf(PlsqlMessageCodes.EXCEPTION) //
						: PlsqlProcedureResult.getMessageNumber(result)) //
				.kalltid(executionTime) //
				.request(LoggingUtils.maskIfFnr(dataIn)) //
				.response(result != null ? result.getData() : null) //
				.logginfo(exception != null //
						? LoggingUtils.formatExceptionAsString(exception) //
						: PlsqlProcedureResult.getMessage(result))  //
				.build();

		log.debug("Correlation ID:  '" + correlationId + "'");
		saveKallLogg(kallLogg);
	}

	public void saveKallLogg(KallLogg kallLogg) {
		try {
			log.info("lagrer KallLogg {}", kallLogg);
			kallLoggRepository.save(kallLogg);
		} catch (Exception e) {
			log.error("Feil ved logging av kalloggdata til databasen; feilmelding=" + e.getMessage(), e);
		}
	}

	public String generateAndSetCorrelationId() {
		String correlationId = MdcOperations.generateCorrelationId();
		MdcOperations.put(MdcOperations.MDC_CORRELATION_ID, correlationId);
		return correlationId;
	}
}
