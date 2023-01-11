package no.nav.oebs.melosys.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

 import no.nav.oebs.melosys.config.common.logging.HttpLoggingFilter;
import no.nav.oebs.melosys.config.common.mdc.MdcFilter;
import no.nav.oebs.melosys.db.repository.KallLoggRepository;

/**
 * Konfigurasjonsklasse som oppretter servletfiltre for HTTP logging og MDC (Mapped Diagnostics Context).
 * <p>
 * Filterene tilordnes høyest og nesthøyest prioritet slik at de kjøres tidlig i filterkjeden. (Høyeste prioritet har laveste
 * verdi.)
 */
@Configuration
public class HttpLoggingConfig {
	@Bean
	public FilterRegistrationBean<MdcFilter> mdcFilterRegistrationBean() {
		FilterRegistrationBean<MdcFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new MdcFilter());
		registrationBean.addUrlPatterns("/api/*");
		registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<HttpLoggingFilter> httpLoggingFilterRegistrationBean(KallLoggRepository kallLoggRepository) {
		FilterRegistrationBean<HttpLoggingFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new HttpLoggingFilter(kallLoggRepository));
		registrationBean.addUrlPatterns("/api/*");
		registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
		return registrationBean;
	}
}
