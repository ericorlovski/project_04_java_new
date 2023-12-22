package kz.bcc.dbpjunioraccountmanageservice.model.dto.mci.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MciResponsePayload {

    @JsonProperty(value = "STATUS")
    private Boolean status;

    @JsonProperty(value = "LANG")
    private String lang;
}