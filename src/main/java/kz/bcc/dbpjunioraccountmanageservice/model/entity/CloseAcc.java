package kz.bcc.dbpjunioraccountmanageservice.model.entity;

import jakarta.persistence.*;
import kz.bcc.dbpjunioraccountmanageservice.model.entity.common.BasicEntityLarge;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.StatusType;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "close_acc_table")
@Getter
@Setter
public class CloseAcc extends BasicEntityLarge {

    @Column (name = "junior_phone")
    private Long juniorPhone;

    @Column (name = "junior_full_name")
    private String juniorFullName;

    @Column (name = "parent_phone")
    private Long parentPhone;

    @Column (name = "parent_full_name")
    private String parentFullName;

    @Column (name = "idn")
    private String idn;

    @Column (name = "block_reason")
    private String blockReason;

    @Column (name = "acc_balance")
    private String accBalance;

    @Column (name = "close_reason")
    private String closeReason;

    @Column (name = "error_message")
    private String errorMessage;

    @Column (name = "close_acc_status")
    @Enumerated(EnumType.STRING)
    private StatusType closeAccStatus;

    @Column(name = "active")
    private boolean active = true;

    @Column (name = "pay_out_account")
    private String payOutAccount;
}
