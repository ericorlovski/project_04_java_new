package kz.bcc.dbpjunioraccountmanageservice.model.dto.otp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class OtpRequestPayload {
    @JsonProperty(value = "login")
    private String login;
    @JsonProperty(value = "lang")
    private String lang;
    @JsonProperty(value = "serviceName")
    private String serviceName;
    @JsonProperty(value = "refId")
    private String refId;
    @JsonProperty(value = "otp")
    private String otp;
}