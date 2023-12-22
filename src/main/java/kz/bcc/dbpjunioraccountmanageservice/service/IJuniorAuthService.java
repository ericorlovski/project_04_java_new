package kz.bcc.dbpjunioraccountmanageservice.service;


import kz.bcc.dbpjunioraccountmanageservice.model.dto.juniorAuth.request.GetUserInfoRequest;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.juniorAuth.response.AuthResponse;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.juniorAuth.response.ColvirResponsePayloadV2;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.UserType;

public interface IJuniorAuthService {

    ColvirResponsePayloadV2 getUserDataByPhone (GetUserInfoRequest request);
    GetUserInfoRequest getUserInfoRequest(String phone, UserType type);
    AuthResponse getJuniorAuthToken();
}
