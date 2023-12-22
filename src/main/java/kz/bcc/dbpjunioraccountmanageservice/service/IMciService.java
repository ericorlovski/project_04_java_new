package kz.bcc.dbpjunioraccountmanageservice.service;

import kz.bcc.dbpjunioraccountmanageservice.model.dto.mci.request.MciRequestWrapper;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.mci.response.MciResponsePayload;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.mci.response.MciResponseWrapper;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.BankPackage;

public interface IMciService {
    MciResponseWrapper getMciRestTemplate(MciRequestWrapper object, String token);
    MciResponsePayload getJuniorLangByJuniorLogin (String login);
    MciResponsePayload getParentLangByJuniorLogin (String login);
    MciResponsePayload getLanguageByJuniorLogin(String login, BankPackage bankPackage);
    MciResponsePayload pushNotifyAboutCloseAcc(String name, String accountBalance, String closeReason, String juniorPhone);
}
