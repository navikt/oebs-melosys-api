package no.nav.oebs.melosys.api.fakturaimport;

import lombok.extern.slf4j.Slf4j;


import no.nav.oebs.melosys.api.common.utils.ObjektMaps;
import no.nav.oebs.melosys.api.fakturaimport.model.FakturaRequest;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Serviceklasse for Segmenter API som kaller PL/SQL-prosedyren og håndterer JSON-transformeringen.
 */
@Slf4j
@Service
@Transactional(noRollbackFor = { Exception.class })
public class FakturaService extends ObjektMaps {

	private static final String PLSQL_PROCEDURE = "xxrtv_restapi_oebs_ve_v1.xxrtv_hent_kstreng";

	private PlsqlProcedureRepository plsqlProcedureRepository;

	public FakturaService(PlsqlProcedureRepository plsqlProcedureRepository, ObjectMapper objectMapper) {
		super(objectMapper);
		log.info("Oppretter Faktura service...");
		this.plsqlProcedureRepository = plsqlProcedureRepository;
	}

	/**
	 * Kaller PL/SQL-prosedyren som utfører forretningslogikken til operasjonen.
	 * @param request
	 */
	private PlsqlProcedureResult executePlsqlProcedure(FakturaRequest request) {

		return plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, toJson(request));
	}

	/**
	 * Konverterer en respons-JSON til et responsobjekt som API'et skal returnere.
	 */
	/*private List<Leverandortransaksjon> getApiResponse(String json) {
		if (json == null) {
			throw new TechnicalPlsqlException("Uventet null-verdi istedenfor JSON-objekt fra " + PLSQL_PROCEDURE);
		}

		return toObject(json, new TypeReference<List<Leverandortransaksjon>>() {
		});
	}*/
}
