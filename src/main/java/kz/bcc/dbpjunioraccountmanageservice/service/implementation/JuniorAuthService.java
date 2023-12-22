package kz.bcc.dbpjunioraccountmanageservice.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.bcc.dbpjunioraccountmanageservice.config.AppProperties;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.juniorAuth.request.AuthenticationRequest;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.juniorAuth.request.GetUserInfoRequest;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.juniorAuth.response.AuthResponse;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.juniorAuth.response.ColvirResponsePayloadV2;
import kz.bcc.dbpjunioraccountmanageservice.model.entity.AuthCredentials;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.BearerTokenType;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.UserType;
import kz.bcc.dbpjunioraccountmanageservice.model.repository.AuthCredentialsRepository;
import kz.bcc.dbpjunioraccountmanageservice.service.IJuniorAuthService;
import kz.bcc.dbpjunioraccountmanageservice.web.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
@Log4j2
@RequiredArgsConstructor
public class JuniorAuthService implements IJuniorAuthService {

    private final AppProperties appProperties;

    private final RestTemplate restTemplate;

    private final AuthCredentialsRepository authRepository;

    @Override
    public ColvirResponsePayloadV2 getUserDataByPhone (GetUserInfoRequest request ) {
        AuthResponse authDto = getJuniorAuthToken();
        return getUserData(Objects.requireNonNull(authDto).getToken(), request);
    }

    private ColvirResponsePayloadV2 getUserData(String token, GetUserInfoRequest request ) {
        val httpEntity = new HttpEntity<>(request, headers(token));
        val httpMethod = HttpMethod.POST;
        log.info("getUserData request: {}, url {}, ", request, appProperties.getUserDataFromAuthServerUrl());
        val response = restTemplate.exchange(appProperties.getUserDataFromAuthServerUrl(),
                httpMethod, httpEntity, GenericResponse.class);
        log.info("getUserData response: {}", response);
        if (response.getStatusCode() == HttpStatus.OK
                && !ObjectUtils.isEmpty(response.getBody())
                && !ObjectUtils.isEmpty(response.getBody().getResultData())) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(response.getBody().getResultData(), ColvirResponsePayloadV2.class);
        } else {
            throw new RuntimeException("Failed check response. StatusCode: "+ response.getStatusCode() +
                    ", !isEmpty(Body): "+ !ObjectUtils.isEmpty(response.getBody()) +
                    " !isEmpty(ResultData): " +  !ObjectUtils.isEmpty(response.getBody().getResultData()));
        }
    }

    @Override
    public AuthResponse getJuniorAuthToken (){

        val token = authRepository.findTopByActiveTrueAndTokenType(BearerTokenType.JUNIOR_AUTH);

        if (token.isPresent()){
            if (token.get().getValidUntil().isAfter(ZonedDateTime.now())) {
                val authResponse = new AuthResponse();
                authResponse.setToken(token.get().getToken());
                authResponse.setValidUntil(String.valueOf(token.get().getValidUntil()));
                return authResponse;
            } else {
                token.get().setActive(false);
                authRepository.save(token.get());
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        AuthenticationRequest request = new AuthenticationRequest(
                (appProperties.getAuthServerAdminLogin()), (appProperties.getAuthServerAdminPassword()));
        HttpEntity<AuthenticationRequest> requestEntity = new HttpEntity<>(request, headers);
        val responseEntity = restTemplate.exchange(
                (appProperties.getAuthServerTokenUrl()),
                HttpMethod.POST,
                requestEntity,
                GenericResponse.class
        );
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            ObjectMapper objectMapper = new ObjectMapper();
            AuthResponse response = objectMapper.convertValue(Objects.requireNonNull(responseEntity.getBody()).getResultData(), AuthResponse.class);

            saveToken(appProperties.getAuthServerAdminLogin(), response.getToken(), response.getValidUntil());
            return response;
        }

        return null;
    }

    private void saveToken(String basicAuthString, String tokenString, String validUntil) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        ZonedDateTime zoneValidUntil = ZonedDateTime.parse(validUntil, formatter);

        val entity = new AuthCredentials();
        entity.setToken(tokenString);
        entity.setBasicAuthString(basicAuthString);
        entity.setValidUntil(zoneValidUntil);
        entity.setTokenType(BearerTokenType.JUNIOR_AUTH);

        authRepository.save(entity);
    }

    private HttpHeaders headers(String token) {
        val headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", "*/*");
        headers.setBearerAuth(token);
        return headers;
    }

    @Override
    public GetUserInfoRequest getUserInfoRequest(String phone, UserType type) {
        GetUserInfoRequest request = new GetUserInfoRequest();
        request.setPhone(phone);
        request.setType(type);
        return request;
    }
}
