package kz.bcc.dbpjunioraccountmanageservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BlockDto {

    @NotBlank
    private String idn;

    @NotBlank
    private String blockReasonCode;
    
    @NotBlank
    private String parentPhone;
}