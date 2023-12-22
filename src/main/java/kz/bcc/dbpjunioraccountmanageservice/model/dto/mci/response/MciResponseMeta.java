package kz.bcc.dbpjunioraccountmanageservice.model.dto.mci.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MciResponseMeta {
    private MciResponseEvent event;
    private String version;
}
