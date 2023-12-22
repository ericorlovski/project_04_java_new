package kz.bcc.dbpjunioraccountmanageservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReissueCardStatusDto {

    @NotBlank
    private String parentPhone;

    private String juniorPhone;

    private String idn;
}