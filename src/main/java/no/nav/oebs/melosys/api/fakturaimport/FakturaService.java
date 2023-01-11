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
		this.plsqlProcedureRepository = plsqlProcedureRepository;
	}

	public String finnValiderKontoStreng(Integer org_id,
			   String artskonto,
			   String ksted,
			   String produktoppgave,
			   String deloppgave,
			   String fellesoppgave,
			   String statskonto,
			   String kilde,
			   String tilsagnsaar,
			   String fritt_felt_1,
			   String fritt_felt_2,
			   String fullmaktskode,
			   String regnskapsforer,
			   String system) {


	//public List<Kryssvalideringtransaksjon> finnKryssvalideringtransaksjoner(String companycode, String segmentname,
	//															LocalDate lastupdatedate) {

		PlsqlProcedureResult result = executePlsqlProcedure(buildRequest(org_id,
				artskonto,
				ksted,
				produktoppgave,
				deloppgave,
				fellesoppgave,
				statskonto,
				kilde,
				tilsagnsaar,
				fritt_felt_1,
				fritt_felt_2,
				fullmaktskode,
				regnskapsforer,
				system));
		/* if (result.getMessageNumber() < 0) {
			 throwPlsqlException(result);
		/} */

		return result.getData();

		// return getApiResponse(result.getData());
	}

	/**
	 * Bygger et requestobjekt som skal konverteres til JSON.
	 */
	private FakturaRequest buildRequest(Integer org_id,
										String artskonto,
										String ksted,
										String produktoppgave,
										String deloppgave,
										String fellesoppgave,
										String statskonto,
										String kilde,
										String tilsagnsaar,
										String fritt_felt_1,
										String fritt_felt_2,
										String fullmaktskode,
										String regnskapsforer,
										String system) {
		return FakturaRequest.builder()
				.org_id(org_id)
				.artskonto(artskonto)
				.ksted(ksted)
				.produktoppgave(produktoppgave)
				.deloppgave(deloppgave)
			  	.fellesoppgave(fellesoppgave)
			 	.statskonto(statskonto)
				.kilde(kilde)
				.tilsagnsaar(tilsagnsaar)
				.fritt_felt_1(fritt_felt_1)
				.fritt_felt_2(fritt_felt_2)
				.fullmaktskode(fullmaktskode)
				.regnskapsforer(regnskapsforer)
				.system(system)
				.build();
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
