package kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ColvirResponseWrapper {
    private ColvirResponseMeta meta;
    private ColvirResponsePayload payload;
}