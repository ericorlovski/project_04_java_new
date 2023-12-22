package kz.bcc.dbpjunioraccountmanageservice.service;

import kz.bcc.dbpjunioraccountmanageservice.model.enums.PushSmsType;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.UserType;
import org.springframework.http.HttpHeaders;

public interface IBankRequestService {
    void sendMessage (String phone, String fullName, UserType userType, String serviceName,
                      String text, PushSmsType type);
    String sendOtp (String login, String lang);
    Boolean checkOtp (String refId, String otp);
    HttpHeaders getRestHeaders();
}
