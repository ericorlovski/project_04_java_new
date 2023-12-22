package kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ColvirRequestCommand {
    private String source = "pkg";
    private String dso;
    private int dsoType = 1;

    public ColvirRequestCommand(String dso) {
        this.dso = dso;
    }
}