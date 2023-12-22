package kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ColvirResponsePayload {

    @JsonProperty(value = "success")
    private Boolean success;
    @JsonProperty(value = "message")
    private String message;
    @JsonProperty(value = "newcardidn")
    private String newCardIdn;
    @JsonProperty(value = "code")
    private String code;
    @JsonProperty(value = "traceid")
    private String traceid;
}