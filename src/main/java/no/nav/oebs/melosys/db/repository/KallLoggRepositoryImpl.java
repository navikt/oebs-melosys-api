package no.nav.oebs.melosys.db.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import no.nav.oebs.melosys.db.entity.KallLogg;
import org.springframework.stereotype.Repository;

/**
 * Implementasjonsklasse for {@link KallLoggRepositoryCustom}.
 */
@Repository
public class KallLoggRepositoryImpl implements KallLoggRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void pingKallLogg() {
		entityManager.createQuery("SELECT k FROM KallLogg k WHERE kall_logg_id = 0", KallLogg.class) //
				.getResultList();
	}
}
