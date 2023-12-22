package kz.bcc.dbpjunioraccountmanageservice.service;

import kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.request.ColvirRequestWrapper;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.response.ColvirResponsePayload;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.BearerTokenType;

public interface IColvirService {
    String getAuthToken(BearerTokenType type);
    ColvirResponsePayload getColvirRestTemplate(ColvirRequestWrapper object, String token);
}
