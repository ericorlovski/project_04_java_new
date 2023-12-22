package kz.bcc.dbpjunioraccountmanageservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kz.bcc.dbpjunioraccountmanageservice.model.dto.*;
import kz.bcc.dbpjunioraccountmanageservice.service.ICloseAccountService;
import kz.bcc.dbpjunioraccountmanageservice.web.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2/close-junior-acc")
@RequiredArgsConstructor
@Log4j2
public class CloseAccountController {

    private final ICloseAccountService closeAccountService;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @Operation(
            summary = "Endpoint for checking the availability of children's products",
            description = "Request for checking the availability of children's products"
    )
    @RequestMapping(value = "/get-junior-products", method = RequestMethod.POST)
    public GenericResponse<JuniorProductsDto> getJuniorProducts (@RequestBody @Valid HoldDto dto) {

        try {
            return closeAccountService.getJuniorProducts(dto);
        } catch (Exception exc) {
            return GenericResponse.error(1, exc.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @Operation(
            summary = "Endpoint for parental rejection of Junior card closure",
            description = "Request for parental rejection of Junior card closure"
    )
    @RequestMapping(value = "/reject-close-request", method = RequestMethod.POST)
    public GenericResponse<Boolean> rejectCloseRequest (@RequestBody @Valid HoldDto dto) {

        try {
            return closeAccountService.rejectRequestCloseCard(dto);
        } catch (Exception exc) {
            return GenericResponse.error(1, exc.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @Operation(
            summary = "Endpoint on a child's request to close a Junior card",
            description = "Request on a child's request to close a Junior card"
    )
    @RequestMapping(value = "/junior-close-request", method = RequestMethod.POST)
    public GenericResponse<Boolean> getJuniorCloseRequest (@RequestBody @Valid CloseDto dto) {

        try {
            return closeAccountService.closeJuniorCardRequest(dto);
        } catch (Exception exc) {
            return GenericResponse.error(1, exc.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @Operation(
            summary = "Endpoint for updating an application for the status of sending a request to Colvir",
            description = "Request for updating an application for the status of sending a request to Colvir"
    )
    @RequestMapping(value = "/get-close-card", method = RequestMethod.POST)
    public GenericResponse<Boolean> getCloseCard (@RequestBody @Valid ApproveCloseDto dto) {

        try {
            return closeAccountService.updCloseStatusForClose(dto);
        } catch (Exception exc) {
            return GenericResponse.error(1, exc.getMessage());
        }
    }
}