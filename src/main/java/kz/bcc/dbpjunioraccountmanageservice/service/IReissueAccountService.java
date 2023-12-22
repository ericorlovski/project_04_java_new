package kz.bcc.dbpjunioraccountmanageservice.service;

import kz.bcc.dbpjunioraccountmanageservice.model.dto.*;
import kz.bcc.dbpjunioraccountmanageservice.model.entity.Reissue;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.BankPackage;
import kz.bcc.dbpjunioraccountmanageservice.web.GenericResponse;

import java.util.List;

public interface IReissueAccountService {

    GenericResponse<CheckHoldDto> getCardHoldStatus (HoldDto dto);
    GenericResponse<Boolean> reissueJuniorCard (BlockDto dto);
    GenericResponse<Boolean> getReissueCardStatus (CheckReissueDto dto);
    void getReissueCardStatusFromColvir (Reissue reissue);
    GenericResponse<Boolean> unblockJuniorCard (String idn, String unblockReasonDscr);
    GenericResponse<String> sendOtpFunction (SentOtpDto dto);
    GenericResponse<Boolean> checkOtpFunction (CheckOtpDto dto);
    GenericResponse<List<ReissueCardStatusDto>> getReissueJuniorCardStatusForBcc (ReissueCardStatusDto dto);
    Boolean performColvirRequest(BlockDto dto, BankPackage colvirPackage, String method, Reissue reissue);
}
