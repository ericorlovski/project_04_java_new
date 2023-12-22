package kz.bcc.dbpjunioraccountmanageservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SentOtpDto {

    @NotBlank
    private String login;
    @NotBlank
    private String lang;
}