package kz.bcc.dbpjunioraccountmanageservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CloseDto {

    @NotBlank
    private String idn;

    @NotBlank
    private String accBalance;
    
    @NotBlank
    private String juniorPhone;

    @NotBlank
    private String closeReason;

    private String payOutAcc;
}