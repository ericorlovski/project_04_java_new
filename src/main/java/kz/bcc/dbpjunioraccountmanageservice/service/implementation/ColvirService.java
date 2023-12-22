package kz.bcc.dbpjunioraccountmanageservice.service.implementation;

import kz.bcc.dbpjunioraccountmanageservice.config.AppProperties;
import kz.bcc.dbpjunioraccountmanageservice.exception.AuthErrorException;
import kz.bcc.dbpjunioraccountmanageservice.exception.RequestErrorException;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.request.ColvirRequestWrapper;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.response.ColvirResponsePayload;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.response.ColvirResponseWrapper;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.keycloak.AuthDto;
import kz.bcc.dbpjunioraccountmanageservice.model.entity.AuthCredentials;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.BearerTokenType;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.MessageCode;
import kz.bcc.dbpjunioraccountmanageservice.model.repository.AuthCredentialsRepository;
import kz.bcc.dbpjunioraccountmanageservice.service.IBankRequestService;
import kz.bcc.dbpjunioraccountmanageservice.service.IColvirService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class ColvirService implements IColvirService {

    private final RestTemplate restTemplate;

    private final AuthCredentialsRepository authRepository;

    private final IBankRequestService bankRequestService;

    private final AppProperties appProperties;

    @Override
    public String getAuthToken(BearerTokenType type) {

        java.util.Optional<AuthCredentials> token = java.util.Optional.empty();
        if (Objects.equals(type, BearerTokenType.COMMON)) {
            token = authRepository.findTopByActiveTrueAndTokenType(BearerTokenType.COMMON);
        } else if (Objects.equals(type, BearerTokenType.MCI)) {
            token = authRepository.findTopByActiveTrueAndTokenType(BearerTokenType.MCI);
        }
        if (token.isPresent()) {
            if (token.get().getValidUntil().isAfter(ZonedDateTime.now())) {
                return token.get().getToken();
            } else {
                token.get().setActive(false);
                authRepository.save(token.get());
            }
        }

        String tokenUrl =  appProperties.getKeycloakUrl() + appProperties.getKeycloakRealm() + "/protocol/openid-connect/token";

        HttpHeaders headers = bankRequestService.getRestHeaders();
        headers.setBasicAuth(appProperties.getKeycloakClientId(), appProperties.getKeycloakClientSecret());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type","client_credentials");

        val httpEntity = new HttpEntity<>(map, headers);
        val httpMethod = HttpMethod.POST;

        val response = restTemplate.exchange(tokenUrl, httpMethod, httpEntity, AuthDto.class);

        if (response.getBody() == null || response.getBody().getAccessToken() == null ||
                response.getBody().getAccessToken().isBlank())
            throw new AuthErrorException(MessageCode.KEYCLOAK_ERROR.getDescription());

        saveToken(appProperties.getKeycloakClientSecret(), response.getBody().getAccessToken(), type, response.getBody().getExpiresIn());
        return response.getBody().getAccessToken();
    }

    private void saveToken(String basicAuthString, String tokenString, BearerTokenType type, int expiresIn) {

        val validUntil = ZonedDateTime.now(ZoneId.of("Asia/Almaty"))
                .plus(expiresIn, ChronoUnit.SECONDS);

        val entity = new AuthCredentials();
        entity.setToken(tokenString);
        entity.setBasicAuthString(basicAuthString);
        entity.setValidUntil(validUntil);
        entity.setTokenType(type);

        authRepository.save(entity);
    }

    @Override
    public ColvirResponsePayload getColvirRestTemplate(ColvirRequestWrapper object, String token) {
        val httpEntity = getRequestEntity(object, token);
        val httpMethod = HttpMethod.POST;

        val response = restTemplate.exchange(appProperties.getColvirAdapterExecUrl(),
                httpMethod, httpEntity, ColvirResponseWrapper.class);

        if (response.getBody() == null || response.getBody().getPayload() == null) {
            log.info("Error request to the system COLVIR, with idn: {}, response null", object.getPayload().getCardidn());
            throw new RequestErrorException(MessageCode.COLVIR_REQUEST_ERROR.getDescription());
        }

        log.info("Successful request to the system COLVIR, with idn: {}, response {}", object.getPayload().getCardidn(), response);
        return response.getBody().getPayload();
    }

    private HttpEntity<Object> getRequestEntity(Object object, String token) {
        val headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        headers.add("Accept", "*/*");
        headers.add("Accept-Encoding", "gzip, deflate, br");
        return new HttpEntity<>(object, headers);
    }
}
