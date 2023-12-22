package kz.bcc.dbpjunioraccountmanageservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckReissueDto {

    @NotBlank
    private String juniorPhone;
}