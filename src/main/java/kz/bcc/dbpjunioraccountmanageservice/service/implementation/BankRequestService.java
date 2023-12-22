package kz.bcc.dbpjunioraccountmanageservice.service.implementation;

import kz.bcc.dbpjunioraccountmanageservice.config.AppProperties;
import kz.bcc.dbpjunioraccountmanageservice.exception.RequestErrorException;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.PushDto;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.otp.OtpRequestPayload;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.*;
import kz.bcc.dbpjunioraccountmanageservice.service.IBankRequestService;
import kz.bcc.dbpjunioraccountmanageservice.service.IJuniorAuthService;
import kz.bcc.dbpjunioraccountmanageservice.web.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
@Log4j2
public class BankRequestService implements IBankRequestService {

    private final RestTemplate restTemplate;

    private final IJuniorAuthService authService;

    private final AppProperties appProperties;

    @Override
    public void sendMessage (String phone, String fullName, UserType userType, String serviceName,
                             String text, PushSmsType type) { //Метод отправления пуша в приложение

        PushDto payload = getPushEntity(phone, fullName, userType, serviceName, text, type);

        val httpEntity = getAuthRestEntity(payload);
        val httpMethod = HttpMethod.POST;

        val response = restTemplate.exchange(appProperties.getNotifyExecUrl(), httpMethod, httpEntity, GenericResponse.class);
        log.info("Successful request to the system UMG, with login: {}, response: {}, method sendMessage" , phone, response);

        if (response.getBody() == null || response.getBody().getResultData() == null)
            throw new RequestErrorException(MessageCode.NOTIFY_REQUEST_ERROR.getDescription());
    }

    private PushDto getPushEntity (String phone, String fullName, UserType userType, String serviceName,
                                   String text, PushSmsType type) {

        val dto = new PushDto();
        dto.setPhone(phone);
        dto.setFullName(fullName);
        dto.setUserType(userType.getName());
        dto.setServiceName(serviceName);
        dto.setText(text);
        dto.setType(type.name());

        return dto;
    }

    //Запрос на генерацию OTP кода в сервис Notify
    public String sendOtp (String login, String lang) {

        OtpRequestPayload payload = new OtpRequestPayload(login, lang, PushSmsDescCode.SERVICE_NAME.getDescription(),null, null);

        val httpEntity = getAuthRestEntity(payload);
        val httpMethod = HttpMethod.POST;

        val response = restTemplate.exchange(appProperties.getNotifySentOtp(), httpMethod, httpEntity, GenericResponse.class);
        log.info("Successful request to notify system, with login: {}, response: {}, method sendOtp" , login, response);

        if (response.getBody() == null || response.getBody().getErrorCode() != 0)
            throw new RequestErrorException(MessageCode.NOTIFY_REQUEST_ERROR.getDescription());

        return response.getBody().getResultData().toString();
    }

    //Запрос на проверку OTP кода в сервис Notify
    public Boolean checkOtp (String refId, String otp) {

        OtpRequestPayload payload = new OtpRequestPayload(null, null,null, refId, otp);

        val httpEntity = getAuthRestEntity(payload);
        val httpMethod = HttpMethod.POST;

        val response = restTemplate.exchange(appProperties.getNotifyCheckOtp(), httpMethod, httpEntity, GenericResponse.class);
        log.info("Successful request to notify system, with refId: {}, response: {}, method checkOtp" , refId, response);

        if (response.getBody() == null || response.getBody().getErrorCode() != 0)
            throw new RequestErrorException(MessageCode.NOTIFY_REQUEST_ERROR.getDescription());

        return Boolean.valueOf(response.getBody().getResultData().toString());
    }

    private HttpEntity<Object> getAuthRestEntity (Object payload) {

        String token = authService.getJuniorAuthToken().getToken(); //запрос токена в JuniorAuth

        HttpHeaders headers = getRestHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        return new HttpEntity<>(payload, headers);
    }

    @Override
    public HttpHeaders getRestHeaders() {
        val headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setConnection("keep-alive");
        headers.add("Accept", "*/*");
        headers.add("Accept-Encoding", "gzip, deflate, br");

        return headers;
    }
}
