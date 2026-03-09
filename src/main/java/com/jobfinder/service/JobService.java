package com.jobfinder.service;

import com.jobfinder.dto.JobDto;
import com.jobfinder.dto.JobSearchResponse;
import com.jobfinder.model.Job;
import com.jobfinder.repository.CompanyRepository;
import com.jobfinder.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);
    private static final String LOCATION = "Manyata Embassy Business Park, Nagavara, Bengaluru-560045";

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final JobDataProvider jobDataProvider;

    private LocalDateTime lastRefreshed;

    public JobService(JobRepository jobRepository, CompanyRepository companyRepository,
                      JobDataProvider jobDataProvider) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.jobDataProvider = jobDataProvider;
    }

    public JobSearchResponse getAllJobs() {
        List<Job> jobs = jobRepository.findAll();
        return buildResponse(jobs, Map.of());
    }

    public JobSearchResponse searchJobs(String role, String company, String skill,
                                        String experience, String jobType) {
        List<Job> jobs = jobRepository.searchJobs(
                blankToNull(role),
                blankToNull(company),
                blankToNull(skill),
                blankToNull(experience),
                blankToNull(jobType)
        );

        Map<String, String> filters = new LinkedHashMap<>();
        if (role != null && !role.isBlank()) filters.put("role", role);
        if (company != null && !company.isBlank()) filters.put("company", company);
        if (skill != null && !skill.isBlank()) filters.put("skill", skill);
        if (experience != null && !experience.isBlank()) filters.put("experience", experience);
        if (jobType != null && !jobType.isBlank()) filters.put("jobType", jobType);

        return buildResponse(jobs, filters);
    }

    public JobSearchResponse globalSearch(String keyword) {
        List<Job> jobs = jobRepository.globalSearch(keyword);
        return buildResponse(jobs, Map.of("keyword", keyword));
    }

    public JobSearchResponse getJobsByCompany(String companyName) {
        List<Job> jobs = jobRepository.findByCompanyNameIgnoreCase(companyName);
        return buildResponse(jobs, Map.of("company", companyName));
    }

    public JobSearchResponse getJobsByRole(String role) {
        List<Job> jobs = jobRepository.findByTitleContainingIgnoreCase(role);
        return buildResponse(jobs, Map.of("role", role));
    }

    public JobSearchResponse getJobsBySkill(String skill) {
        List<Job> jobs = jobRepository.findBySkill(skill);
        return buildResponse(jobs, Map.of("skill", skill));
    }

    public JobSearchResponse getJobsByExperience(String experience) {
        List<Job> jobs = jobRepository.findByExperienceLevelIgnoreCase(experience);
        return buildResponse(jobs, Map.of("experience", experience));
    }

    public Map<String, Object> getFilterOptions() {
        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("companies", jobRepository.findDistinctCompanyNames());
        filters.put("experienceLevels", jobRepository.findDistinctExperienceLevels());
        filters.put("jobTypes", jobRepository.findDistinctJobTypes());

        Set<String> allSkills = new TreeSet<>();
        jobRepository.findAll().forEach(job -> {
            if (job.getSkills() != null && !job.getSkills().isBlank()) {
                Arrays.stream(job.getSkills().split("\\s*,\\s*"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .forEach(allSkills::add);
            }
        });
        filters.put("skills", allSkills);

        return filters;
    }

    @Transactional
    public JobSearchResponse refreshJobs() {
        log.info("Refreshing job listings...");

        List<String> companyNames = companyRepository.findAll().stream()
                .map(c -> c.getName())
                .toList();
        jobDataProvider.setCompanyNames(companyNames);

        List<Job> latestJobs = jobDataProvider.fetchLatestJobs();

        int added = 0;
        int updated = 0;
        int skipped = 0;

        for (Job incoming : latestJobs) {
            String fp = incoming.getFingerprint();
            var existing = jobRepository.findByFingerprint(fp);

            if (existing.isPresent()) {
                Job e = existing.get();
                if (incoming.getDescription() != null) e.setDescription(incoming.getDescription());
                if (incoming.getSalary() != null) e.setSalary(incoming.getSalary());
                if (incoming.getSkills() != null) e.setSkills(incoming.getSkills());
                if (incoming.getExperienceLevel() != null) e.setExperienceLevel(incoming.getExperienceLevel());
                jobRepository.save(e);
                updated++;
            } else {
                try {
                    jobRepository.save(incoming);
                    added++;
                } catch (Exception ex) {
                    skipped++;
                    log.debug("Skipped duplicate job: {}", incoming.getTitle());
                }
            }
        }

        lastRefreshed = LocalDateTime.now();
        log.info("Job refresh complete. Added: {}, Updated: {}, Skipped: {}, Total: {}",
                added, updated, skipped, jobRepository.count());

        return getAllJobs();
    }

    @Transactional
    public void initializeIfEmpty() {
        if (jobRepository.count() == 0) {
            log.info("Jobs table is empty. Seeding initial job data...");
            refreshJobs();
        } else {
            lastRefreshed = LocalDateTime.now();
            log.info("Jobs table already has {} job listings.", jobRepository.count());
        }
    }

    private JobSearchResponse buildResponse(List<Job> jobs, Map<String, String> filters) {
        List<JobDto> dtos = jobs.stream()
                .map(JobDto::fromEntity)
                .toList();
        return new JobSearchResponse(dtos.size(), LOCATION, lastRefreshed, filters, dtos);
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
