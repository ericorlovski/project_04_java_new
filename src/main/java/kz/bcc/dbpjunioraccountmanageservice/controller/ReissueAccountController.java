package kz.bcc.dbpjunioraccountmanageservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.*;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.MessageCode;
import kz.bcc.dbpjunioraccountmanageservice.service.IReissueAccountService;
import kz.bcc.dbpjunioraccountmanageservice.web.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v2/junior-acc")
@RequiredArgsConstructor
@Log4j2
public class ReissueAccountController {

    private final IReissueAccountService reissueAccountService;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @Operation(
            summary = "Endpoint for check card hold status from Colvir",
            description = "Request for check card hold status from Colvir"
    )
    @RequestMapping(value = "/get-hold-status", method = RequestMethod.POST)
    public GenericResponse<CheckHoldDto> getHoldStatus (@RequestBody @Valid HoldDto dto) {

        try {
            return reissueAccountService.getCardHoldStatus(dto);
        } catch (Exception exc) {
            return GenericResponse.error(1, exc.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @Operation(
            summary = "Endpoint for block and reissue junior card",
            description = "Request for block and reissue junior card"
    )
    @RequestMapping(value = "/get-reissue-card", method = RequestMethod.POST)
    public GenericResponse<Boolean> getReissueCard (@RequestBody @Valid BlockDto dto) {

        try {
            return reissueAccountService.reissueJuniorCard(dto);
        } catch (Exception exc) {
            return GenericResponse.error(1, exc.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @Operation(
            summary = "Endpoint for check reissue status",
            description = "Request for check reissue status"
    )
    @RequestMapping(value = "/get-reissue-status", method = RequestMethod.POST)
    public GenericResponse<Boolean> getReissueStatus (@RequestBody @Valid CheckReissueDto dto) {

        try {
            return reissueAccountService.getReissueCardStatus(dto);
        } catch (Exception exc) {
            return GenericResponse.error(MessageCode.JUNIOR_PHONE_NULL);
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @Operation(
            summary = "Endpoint for receiving a list of cards to be reissued by calling the parent",
            description = "Request for receiving a list of cards to be reissued by calling the parent"
    )
    @RequestMapping(value = "/get-reissue-card-list", method = RequestMethod.POST)
    public GenericResponse<List<ReissueCardStatusDto>> getReissueCardList (@RequestBody @Valid ReissueCardStatusDto dto) {

        try {
            return reissueAccountService.getReissueJuniorCardStatusForBcc(dto);
        } catch (Exception exc) {
            return GenericResponse.error(MessageCode.PARENT_PHONE_NULL);
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @Operation(
            summary = "Endpoint for sent otp code",
            description = "Request for sent otp code"
    )
    @RequestMapping(value = "/sent-otp", method = RequestMethod.POST)
    public GenericResponse<String> sentJuniorOtp (@RequestBody @Valid SentOtpDto dto) {

        try {
            return reissueAccountService.sendOtpFunction(dto);
        } catch (Exception exc) {
            return GenericResponse.error(1, exc.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @Operation(
            summary = "Endpoint for check otp code",
            description = "Request for check otp code"
    )
    @RequestMapping(value = "/check-otp", method = RequestMethod.POST)
    public GenericResponse<Boolean> checkJuniorOtp (@RequestBody @Valid CheckOtpDto dto) {

        try {
            return reissueAccountService.checkOtpFunction(dto);
        } catch (Exception exc) {
            return GenericResponse.error(1, exc.getMessage());
        }
    }
}