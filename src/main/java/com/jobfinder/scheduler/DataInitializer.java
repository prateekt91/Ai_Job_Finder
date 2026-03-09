package com.jobfinder.scheduler;

import com.jobfinder.service.CompanyService;
import com.jobfinder.service.JobService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CompanyService companyService;
    private final JobService jobService;

    public DataInitializer(CompanyService companyService, JobService jobService) {
        this.companyService = companyService;
        this.jobService = jobService;
    }

    @Override
    public void run(String... args) {
        companyService.initializeIfEmpty();
        jobService.initializeIfEmpty();
    }
}
