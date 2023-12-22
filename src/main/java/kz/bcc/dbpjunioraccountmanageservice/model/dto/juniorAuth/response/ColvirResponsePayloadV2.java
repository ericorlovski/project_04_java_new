package kz.bcc.dbpjunioraccountmanageservice.model.dto.juniorAuth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ColvirResponsePayloadV2 {

    @JsonProperty(value = "BDATE")
    private String birthdate;

    @JsonProperty(value = "FNAME")
    private String firstName;

    @JsonProperty(value = "LNAME")
    private String lastName;

    @JsonProperty(value = "PNAME")
    private String middleName;

    @JsonProperty(value = "RBS")
    private String rbs;

    @JsonProperty(value = "RBS_LIST")
    private List<String> rbsList;

    @JsonProperty(value = "PHONE")
    private String phone;

    @JsonProperty(value = "childPhone")
    private String childPhone;


    @JsonProperty(value = "STATUS")
    private Integer status;

    @JsonProperty(value = "IIN")
    private String iin;

    @JsonProperty(value = "xPARENT")
    private ColvirResponsePayloadV2 parent;

    @JsonProperty(value = "XCHILD")
    private List<ColvirResponsePayloadV2> childs;

    @JsonProperty(value = "PARENT_PHONE")
    private String parentPhone;

    @JsonProperty(value = "UPDATED_AT")
    private String updated_at;
}