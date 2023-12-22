package kz.bcc.dbpjunioraccountmanageservice.model.dto.juniorAuth.request;

import kz.bcc.dbpjunioraccountmanageservice.model.enums.UserType;
import lombok.Data;


@Data
public class GetUserInfoRequest {
    private String phone;
    private UserType type;

}
