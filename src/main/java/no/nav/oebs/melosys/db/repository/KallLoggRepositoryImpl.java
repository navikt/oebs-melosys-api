package no.nav.oebs.melosys.db.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
		entityManager.createQuery("SELECT k FROM KallLogg k WHERE k.id is null", KallLogg.class) //
				.getResultList();
	}
}
