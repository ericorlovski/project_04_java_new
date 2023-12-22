package kz.bcc.dbpjunioraccountmanageservice.model.dto.mci.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class MciRequestCommand {
    private String source = "pkg";
    private String dso;
    private int dsoType = 1;

    public MciRequestCommand(String dso) {
        this.dso = dso;
    }
}