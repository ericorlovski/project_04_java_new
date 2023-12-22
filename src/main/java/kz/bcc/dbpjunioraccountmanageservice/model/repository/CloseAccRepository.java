package kz.bcc.dbpjunioraccountmanageservice.model.repository;

import kz.bcc.dbpjunioraccountmanageservice.model.entity.CloseAcc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CloseAccRepository extends JpaRepository<CloseAcc, Long> {

    Optional<CloseAcc> findFirstByIdnAndActiveOrderByIdDesc(String idn, boolean active);

    @Query(
            value = "SELECT * FROM close_acc_table ca WHERE ca.close_acc_status = 'CLOSE_START' AND ca.active = true ORDER BY ca.id ASC LIMIT 1",
            nativeQuery = true)
    Optional<CloseAcc> getCloseAccActiveEntity();
}