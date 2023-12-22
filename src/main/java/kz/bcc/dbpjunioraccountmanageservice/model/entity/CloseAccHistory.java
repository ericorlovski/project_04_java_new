package kz.bcc.dbpjunioraccountmanageservice.model.entity;

import jakarta.persistence.*;
import kz.bcc.dbpjunioraccountmanageservice.model.entity.common.BasicEntityLarge;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.StatusType;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "close_acc_history")
@Getter
@Setter
public class CloseAccHistory extends BasicEntityLarge {

    @Column (name = "close_acc_id")
    private Long closeAccId;

    @Column (name = "idn")
    private String idn;

    @Column (name = "close_acc_status")
    @Enumerated(EnumType.STRING)
    private StatusType closeAccStatus;

    @Column (name = "result_description")
    private String resultDescription;

    @Column(name = "active")
    private boolean active = true;
}
