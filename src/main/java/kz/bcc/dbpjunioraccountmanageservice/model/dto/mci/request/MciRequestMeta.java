package kz.bcc.dbpjunioraccountmanageservice.model.dto.mci.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MciRequestMeta {
    private String version = "1.0";
    private MciRequestCommand command;
    private MciRequestEvent event;

    public MciRequestMeta(String dso) {
        this.command = new MciRequestCommand(dso);
    }
}
