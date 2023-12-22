package kz.bcc.dbpjunioraccountmanageservice.job;

import jakarta.transaction.Transactional;
import kz.bcc.dbpjunioraccountmanageservice.model.repository.ReissueRepository;
import kz.bcc.dbpjunioraccountmanageservice.service.IReissueAccountService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Setter
@Getter
public class CheckReissueStatusJob {

    private final ReissueRepository reissueRepository;

    private final IReissueAccountService accountManageService;

    @Scheduled(cron = "1 * * * * *")
    @Transactional(rollbackOn = Exception.class)
    public void checkReissueStatusJob() {
        val reissue = reissueRepository.getReissueActiveEntity();

        reissue.ifPresent(accountManageService::getReissueCardStatusFromColvir);
    }
}
