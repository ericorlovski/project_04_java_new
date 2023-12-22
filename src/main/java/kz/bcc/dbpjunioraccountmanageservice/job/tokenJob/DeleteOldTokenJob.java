package kz.bcc.dbpjunioraccountmanageservice.job.tokenJob;

import kz.bcc.dbpjunioraccountmanageservice.model.repository.AuthCredentialsRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Setter
@Getter
public class DeleteOldTokenJob {

    private final AuthCredentialsRepository authRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void deleteOldTokensJob() {
        authRepository.deleteOldTokens();
    }

}
