package kz.bcc.dbpjunioraccountmanageservice.model.dto.mci.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class MciRequestPayload {
    @JsonProperty(value = "LOGIN")
    private String login;
    @JsonProperty(value = "NAME")
    private String name;
    @JsonProperty(value = "ACCOUNTBALANCE")
    private String accountBalance;
    @JsonProperty(value = "CLOSEREASON")
    private String closeReason;
}