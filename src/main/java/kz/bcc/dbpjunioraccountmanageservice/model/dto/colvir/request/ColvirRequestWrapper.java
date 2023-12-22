package kz.bcc.dbpjunioraccountmanageservice.model.dto.colvir.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ColvirRequestWrapper {
    private ColvirRequestMeta meta;
    private ColvirRequestPayload payload;

    public ColvirRequestWrapper(String dso) {
        this.meta = new ColvirRequestMeta(dso);
    }
}