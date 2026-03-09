package com.jobfinder.scheduler;

import com.jobfinder.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobUpdateScheduler {

    private static final Logger log = LoggerFactory.getLogger(JobUpdateScheduler.class);

    private final JobService jobService;

    public JobUpdateScheduler(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Runs every 7 days to refresh job listings.
     * Initial delay of 30 seconds to let the app + company data initialize first.
     */
    @Scheduled(
            fixedRateString = "${job.refresh.interval-ms:604800000}",
            initialDelay = 30000
    )
    public void scheduledJobRefresh() {
        log.info("=== Scheduled weekly job listings refresh triggered ===");
        jobService.refreshJobs();
        log.info("=== Scheduled job refresh completed ===");
    }
}
