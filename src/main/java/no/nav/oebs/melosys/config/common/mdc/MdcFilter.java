package no.nav.oebs.melosys.config.common.mdc;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Servletfilter som setter MDC-verdier (Mapped Diagnostics Context). MDC-verdiene fjernes igjen ved utgangen av filteret.
 */
public class MdcFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			generateAndSetInternalCorrelationId();

			filterChain.doFilter(request, response);
		} finally {
			MdcOperations.remove(MdcOperations.MDC_CORRELATION_ID);
		}
	}

	private void generateAndSetInternalCorrelationId() {
		String correlationId = MdcOperations.generateCorrelationId();

		MdcOperations.put(MdcOperations.MDC_CORRELATION_ID, correlationId);
	}
}

