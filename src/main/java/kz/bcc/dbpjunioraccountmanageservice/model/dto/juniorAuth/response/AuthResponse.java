package kz.bcc.dbpjunioraccountmanageservice.model.dto.juniorAuth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResponse {
    @JsonProperty("token")
    private String token;

    @JsonProperty("validUntil")
    private String validUntil;
}

