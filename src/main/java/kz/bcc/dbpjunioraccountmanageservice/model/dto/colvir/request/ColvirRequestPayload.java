package kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class ColvirRequestPayload {
    @JsonProperty(value = "cardidn")
    private String cardidn;
    @JsonProperty(value = "reasoncode")
    private String reasoncode;
    @JsonProperty(value = "reasondscr")
    private String reasondscr;
    @JsonProperty(value = "traceid")
    private String traceid;
    @JsonProperty(value = "closecard")
    private String closecard;
    @JsonProperty(value = "closeaccfl")
    private String closeaccfl;
    @JsonProperty(value = "payoutacc")
    private String payoutacc;
    @JsonProperty(value = "payoutfl")
    private String payoutfl;
}