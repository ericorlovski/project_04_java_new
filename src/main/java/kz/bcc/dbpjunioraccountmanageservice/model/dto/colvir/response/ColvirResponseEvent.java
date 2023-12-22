package kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ColvirResponseEvent {
    private String id;
    private String parent;
    private String previous;
    private ZonedDateTime time;
}