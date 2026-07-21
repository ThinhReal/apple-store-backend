package com.thinhreal.applestore.scheduler;

import com.thinhreal.applestore.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "${app.refresh-token.cleanup-cron:0 0 2 * * *}")
    @Transactional
    public void deleteExpiredRefreshTokens() {
        Instant now = Instant.now();
        long deletedCount = refreshTokenRepository.deleteByExpiryDateBefore(now);

        if (deletedCount > 0) {
            log.info("Removed {} expired refresh token(s) (expiry_date < {})", deletedCount, now);
        } else {
            log.debug("No expired refresh tokens to remove (expiry_date < {})", now);
        }
    }
}
