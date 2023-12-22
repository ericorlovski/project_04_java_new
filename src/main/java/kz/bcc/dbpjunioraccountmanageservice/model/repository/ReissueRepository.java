package kz.bcc.dbpjunioraccountmanageservice.model.repository;

import kz.bcc.dbpjunioraccountmanageservice.model.entity.Reissue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReissueRepository extends JpaRepository<Reissue, Long> {

    Optional<Reissue> findFirstByOldIdnAndActiveOrderByIdDesc(String oldIdn, boolean active);
    Optional<Reissue> findFirstByJuniorPhoneAndActiveOrderByIdDesc(Long parentPhone, boolean active);
    List<Reissue> findAllByParentPhoneAndActiveOrderByIdAsc(Long parentPhone, boolean active);
    @Query(
            value = "SELECT * FROM reissue_table rt WHERE rt.reissue_status != 'BLOCK_CARD' AND rt.active = true ORDER BY rt.id ASC LIMIT 1",
            nativeQuery = true)
    Optional<Reissue> getReissueActiveEntity();
}