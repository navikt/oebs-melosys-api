package no.nav.oebs.melosys.db.repository;

import no.nav.oebs.melosys.db.entity.KallLogg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Grensesnitt for repository som håndterer dataaksess mot KallLogg.
 */
@Repository
@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface KallLoggRepository extends JpaRepository<KallLogg, Integer>, KallLoggRepositoryCustom {

}
