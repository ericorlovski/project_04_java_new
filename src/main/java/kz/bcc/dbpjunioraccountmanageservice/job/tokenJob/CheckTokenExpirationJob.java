package kz.bcc.dbpjunioraccountmanageservice.job.tokenJob;

import kz.bcc.dbpjunioraccountmanageservice.model.repository.AuthCredentialsRepository;
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
public class CheckTokenExpirationJob {

    private final AuthCredentialsRepository authRepository;

    @Scheduled(cron = "0 * * * * *")
    public void checkTokensForExpiration() {
        val entity = authRepository.findTopByActiveTrue();

        if (entity.isPresent()) {
            val now = System.currentTimeMillis();
            val then = entity.get().getValidUntil().toInstant().toEpochMilli();

            if ((then - now) < 60000) {
                entity.get().setActive(false);
                authRepository.save(entity.get());
            }
        }
    }
}
