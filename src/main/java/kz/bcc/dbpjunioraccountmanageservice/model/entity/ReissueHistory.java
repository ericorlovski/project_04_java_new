package kz.bcc.dbpjunioraccountmanageservice.model.entity;

import jakarta.persistence.*;
import kz.bcc.dbpjunioraccountmanageservice.model.entity.common.BasicEntityLarge;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.StatusType;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "reissue_history")
@Getter
@Setter
public class ReissueHistory extends BasicEntityLarge {

    @Column (name = "old_idn")
    private String oldIdn;

    @Column (name = "reissue_status")
    @Enumerated(EnumType.STRING)
    private StatusType reissueStatus;

    @Column (name = "result_description")
    private String resultDescription;

    @Column(name = "active")
    private boolean active = true;

    @Column (name = "reissue_id")
    private Long reissueId;
}
