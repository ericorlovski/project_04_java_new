package kz.bcc.dbpjunioraccountmanageservice.model.repository;

import kz.bcc.dbpjunioraccountmanageservice.model.entity.AuthCredentials;
import kz.bcc.dbpjunioraccountmanageservice.model.enums.BearerTokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AuthCredentialsRepository extends JpaRepository<AuthCredentials, Long> {
    Optional<AuthCredentials> findTopByActiveTrue();

    @Modifying
    @Query(value = "DELETE FROM auth_credentials ac WHERE ac.active = false " +
            "AND ac.updated_at < NOW() - INTERVAL '24 HOURS'", nativeQuery = true)
    void deleteOldTokens();

    Optional<AuthCredentials> findTopByActiveTrueAndTokenType(BearerTokenType tokenType);
}