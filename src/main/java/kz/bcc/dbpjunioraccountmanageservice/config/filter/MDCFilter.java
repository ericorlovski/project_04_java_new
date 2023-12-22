package kz.bcc.dbpjunioraccountmanageservice.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.jboss.logging.MDC;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Log4j2
public class MDCFilter extends OncePerRequestFilter {

    private static final String MDC_KEY = "traceId";

    private static final String AUTH_TRACE_ID_KEY = "auth-trace-id";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) {

        try {
            String traceId = request.getHeader(AUTH_TRACE_ID_KEY);
            if (ObjectUtils.isEmpty(traceId)) {
                traceId = UUID.randomUUID().toString();
            }
            MDC.put(MDC_KEY, traceId);
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException e) {
            log.error("ExternalParamFilter", e);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
