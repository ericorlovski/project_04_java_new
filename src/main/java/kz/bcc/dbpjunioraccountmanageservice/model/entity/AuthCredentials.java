package kz.bcc.dbpjunioraccountmanageservice.model.entity;

import jakarta.persistence.*;
import kz.bcc.dbpjunioraccountmanageservice.model.entity.common.BasicTimedEntityLarge;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.BearerTokenType;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Table(name = "auth_credentials")
@Getter
@Setter
public class AuthCredentials extends BasicTimedEntityLarge {

    @Column(name = "basic_auth_string")
    private String basicAuthString;

    @Column(name = "bearer_token")
    private String token;

    @Column(name = "valid_until")
    private ZonedDateTime validUntil;

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "token_type")
    @Enumerated(EnumType.STRING)
    private BearerTokenType tokenType;
}