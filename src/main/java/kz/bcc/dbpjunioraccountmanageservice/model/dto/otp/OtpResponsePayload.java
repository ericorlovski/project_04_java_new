package kz.bcc.dbpjunioraccountmanageservice.model.dto.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpResponsePayload {

    @JsonProperty(value = "refId")
    private String refId;
}