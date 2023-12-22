package kz.bcc.dbpjunioraccountmanageservice.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.bcc.dbpjunioraccountmanageservice.config.AppProperties;
import kz.bcc.dbpjunioraccountmanageservice.web.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Log4j2
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final RestTemplate restTemplate;
    private final AppProperties appProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final List<String> accessList = Arrays.asList("/api/v1/swagger/**", "/v3/api-docs/**");



    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (matchesUrl(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        } else if (!ObjectUtils.isEmpty(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Map<String, Object> userData = validateToken(token);
            if (ObjectUtils.isEmpty(userData)) {
                throw new AccessDeniedException("Forbidden");
            } else {
                createAuthentication(userData);
            }
        } else {
            throw new AccessDeniedException("Forbidden");
        }
        filterChain.doFilter(request, response);

    }

    private Map<String, Object> validateToken (String token) {
        val httpEntity = new HttpEntity<>(headers(token));
        val httpMethod = HttpMethod.POST;
        try {
            ResponseEntity<GenericResponse<Map<String, Object>>> response = restTemplate.exchange(
                    appProperties.getValidateUrl(),
                    httpMethod,
                    httpEntity,
                    new ParameterizedTypeReference<>() {}
            );
            if (response.getStatusCode() == HttpStatus.OK){
                GenericResponse<Map<String, Object>> genericResponse = response.getBody();
                if (!ObjectUtils.isEmpty(genericResponse)
                        &&!ObjectUtils.isEmpty(genericResponse.getResultData())
                        && checkAutResponse(genericResponse.getResultData())) {
                    return genericResponse.getResultData();
                }
            }
            throw new RuntimeException("Failed to validate token. Response code: " + response.getStatusCode());
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException("Failed to validate token. " + e.getMessage());
        }
    }

    private HttpHeaders headers(String token) {
        val headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    private boolean checkAutResponse(Map<String, Object> map) {
        return !ObjectUtils.isEmpty(map.get("sub"))
                && !ObjectUtils.isEmpty(map.get("roles"));
    }

    private void createAuthentication(Map<String, Object> userData) {
        if (userData != null) {
            String username = (String) userData.get("sub");
            List<?> roles = (List<?>) userData.get("roles");
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .filter(role -> role instanceof String)
                    .map(role -> new SimpleGrantedAuthority((String) role))
                    .collect(Collectors.toList());
            Authentication authentication = new UsernamePasswordAuthenticationToken(getUserDetails(username, authorities), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            throw new BadCredentialsException("Authentication failed");
        }

    }

    private UserDetails getUserDetails(String username, List<SimpleGrantedAuthority> authorities) {
        return new User(
                username,
                "",
                true,
                true,
                true,
                true,
                authorities
        );
    }

    private boolean matchesUrl(String requestURI) {
        for (String pattern : accessList) {
            if (antPathMatcher.match(pattern, requestURI)) {
                return true;
            }
        }
        return false;
    }


}
