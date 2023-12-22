package kz.bcc.dbpjunioraccountmanageservice.model.dto.mci.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MciResponseWrapper {
    private MciResponseMeta meta;
    private MciResponsePayload payload;
}