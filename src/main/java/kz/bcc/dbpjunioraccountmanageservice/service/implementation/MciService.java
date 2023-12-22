package kz.bcc.dbpjunioraccountmanageservice.service.implementation;

import kz.bcc.dbpjunioraccountmanageservice.config.AppProperties;
import kz.bcc.dbpjunioraccountmanageservice.exception.RequestErrorException;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.mci.request.MciRequestPayload;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.mci.request.MciRequestWrapper;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.mci.response.MciResponsePayload;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.mci.response.MciResponseWrapper;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.*;
import kz.bcc.dbpjunioraccountmanageservice.service.IColvirService;
import kz.bcc.dbpjunioraccountmanageservice.service.IMciService;
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
public class MciService implements IMciService {

    private final RestTemplate restTemplate;

    private final AppProperties appProperties;

    private final IColvirService colvirService;

    private HttpEntity<Object> getRequestEntity(Object object, String token) {
        val headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        headers.add("Accept", "*/*");
        headers.add("Accept-Encoding", "gzip, deflate, br");
        return new HttpEntity<>(object, headers);
    }

    @Override
    public MciResponseWrapper getMciRestTemplate(MciRequestWrapper object, String token) {
        val httpEntity = getRequestEntity(object, token);
        val httpMethod = HttpMethod.POST;

        val response = restTemplate.exchange(appProperties.getMciExecUrl(),
                httpMethod, httpEntity, MciResponseWrapper.class);
        log.info("Successful request to MCI, with login: {}", object.getPayload().getLogin());

        if (response.getBody() == null || response.getBody().getPayload() == null){
            log.error("getMciRestTemplate: {}", response);
            throw new RequestErrorException(MessageCode.MCI_REQUEST_ERROR.getDescription());
        }
        return response.getBody();
    }

    @Override
    public MciResponsePayload getLanguageByJuniorLogin(String login, BankPackage bankPackage) { //Полчения языка приложения в MCI

        String token = colvirService.getAuthToken(BearerTokenType.MCI);
        log.info("Successful request a token for MCI, with login: {}, method getLanguageByJuniorLogin", login);

        val object = new MciRequestWrapper(bankPackage.getDescription());
        String phone = login.substring(login.length() - 10);

        val payload = new MciRequestPayload(phone, null,null, null);
        object.setPayload(payload);

        try {
            val response = getMciRestTemplate(object, token);
            return response.getPayload();
        } catch (Exception e) {
            MciResponsePayload defaultLang = new MciResponsePayload();
            defaultLang.setLang("ru");
            return defaultLang;
        }
    }

    @Override
    public MciResponsePayload pushNotifyAboutCloseAcc(String name, String accountBalance, String closeReason, String juniorPhone) {

        String token = colvirService.getAuthToken(BearerTokenType.MCI);
        log.info("Successful request a token for MCI, with phone: {}, method pushNotifyAboutCloseAcc", juniorPhone);

        String phone = juniorPhone.substring(juniorPhone.length() - 10);

        val object = new MciRequestWrapper(BankPackage.MCI_PUSH_CLOSE_NOTIFY.getDescription());
        val payload = new MciRequestPayload(phone, name, accountBalance,  closeReason);
        object.setPayload(payload);

        try {
            val response = getMciRestTemplate(object, token);
            return response.getPayload();
        } catch (Exception e) {
            throw new RequestErrorException(MessageCode.MCI_REQUEST_ERROR.getDescription());
        }
    }

    @Override
    public MciResponsePayload getJuniorLangByJuniorLogin(String login) {
        val apiMethod = BankPackage.MCI_GET_JUNIOR_LANG;
        log.info("Start request to MCI, with login: {}, method getJuniorLang", login);
        return getLanguageByJuniorLogin(login, apiMethod);
    }

    @Override
    public MciResponsePayload getParentLangByJuniorLogin(String login) {
        val apiMethod = BankPackage.MCI_GET_PARENT_LANG;
        log.info("Start request to MCI, with login: {}, method getParentLang", login);
        return getLanguageByJuniorLogin(login, apiMethod);
    }
}
