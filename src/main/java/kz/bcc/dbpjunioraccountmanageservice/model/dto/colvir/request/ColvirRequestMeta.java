package kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ColvirRequestMeta {
    private String version = "1.0";
    private ColvirRequestCommand command;
    private ColvirRequestEvent event;

    public ColvirRequestMeta(String dso) {
        this.command = new ColvirRequestCommand(dso);
    }
}
