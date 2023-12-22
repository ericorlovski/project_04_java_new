package kz.bcc.dbpjunioraccountmanageservice.model.dto;

import lombok.Data;

@Data
public class PushDto {

    private String phone;

    private String fullName;

    private String userType;

    private String serviceName;

    private String text;

    private String type;
}