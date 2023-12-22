package kz.bcc.dbpjunioraccountmanageservice.model.dto.mci.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MciRequestWrapper {
    private MciRequestMeta meta;
    private MciRequestPayload payload;

    public MciRequestWrapper(String dso) {
        this.meta = new MciRequestMeta(dso);
    }
}