package com.jobfinder.scheduler;

import com.jobfinder.service.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CompanyUpdateScheduler {

    private static final Logger log = LoggerFactory.getLogger(CompanyUpdateScheduler.class);

    private final CompanyService companyService;

    public CompanyUpdateScheduler(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * Runs every 7 days (604800000 ms) to refresh the company list.
     * The initial delay is set to 10 seconds to allow the app to start up first.
     */
    @Scheduled(
            fixedRateString = "${company.refresh.interval-ms:604800000}",
            initialDelay = 10000
    )
    public void scheduledRefresh() {
        log.info("=== Scheduled weekly company data refresh triggered ===");
        companyService.refreshCompanies();
        log.info("=== Scheduled refresh completed ===");
    }
}
