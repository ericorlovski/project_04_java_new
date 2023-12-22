package kz.bcc.dbpjunioraccountmanageservice.model.entity;

import jakarta.persistence.*;
import kz.bcc.dbpjunioraccountmanageservice.model.entity.common.BasicEntityLarge;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.StatusType;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "reissue_table")
@Getter
@Setter
public class Reissue extends BasicEntityLarge {

    @Column (name = "junior_phone")
    private Long juniorPhone;

    @Column (name = "junior_full_name")
    private String juniorFullName;

    @Column (name = "parent_phone")
    private Long parentPhone;

    @Column (name = "parent_full_name")
    private String parentFullName;

    @Column (name = "old_idn")
    private String oldIdn;

    @Column (name = "new_idn")
    private String newIdn;

    @Column (name = "block_reason")
    private String blockReason;

    @Column(name = "active")
    private boolean active = true;

    @Column (name = "error_message")
    private String errorMessage;

    @Column (name = "reissue_status")
    @Enumerated(EnumType.STRING)
    private StatusType reissueStatus;
}
