package kz.bcc.dbpjunioraccountmanageservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApproveCloseDto {

    @NotBlank
    private String idn;

    private String payOutAcc;
}