package kz.bcc.dbpjunioraccountmanageservice.service;

import kz.bcc.dbpjunioraccountmanageservice.model.dto.*;
import kz.bcc.dbpjunioraccountmanageservice.model.entity.CloseAcc;
import kz.bcc.dbpjunioraccountmanageservice.web.GenericResponse;


public interface ICloseAccountService {

    GenericResponse<Boolean> closeJuniorCardRequest(CloseDto dto);
    GenericResponse<Boolean> rejectRequestCloseCard(HoldDto dto);
    GenericResponse<Boolean> updCloseStatusForClose(ApproveCloseDto dto);
    GenericResponse<JuniorProductsDto> getJuniorProducts (HoldDto dto);
    void closeJuniorCardFunction(CloseAcc closeAcc);
}
