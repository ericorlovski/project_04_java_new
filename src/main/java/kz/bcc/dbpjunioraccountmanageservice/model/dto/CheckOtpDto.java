package kz.bcc.dbpjunioraccountmanageservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckOtpDto {

    @NotBlank
    private String otp;

    @NotBlank
    private String refId;}