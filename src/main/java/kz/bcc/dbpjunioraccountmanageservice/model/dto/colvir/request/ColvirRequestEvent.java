package kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ColvirRequestEvent {
    private String id;
    private ZonedDateTime time;
}