package com.jobfinder.controller;

import com.jobfinder.dto.JobSearchResponse;
import com.jobfinder.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
@Tag(name = "Jobs", description = "Endpoints to search, filter, and browse job postings from Manyata Tech Park companies")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    @Operation(summary = "Get all jobs", description = "Returns every job listing in the database")
    public ResponseEntity<JobSearchResponse> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @GetMapping("/search")
    @Operation(summary = "Advanced multi-filter search",
            description = "Search jobs by combining any of: role, company, skill, experience level, and job type. All filters are optional.")
    public ResponseEntity<JobSearchResponse> searchJobs(
            @Parameter(description = "Job title / role keyword", example = "Developer")
            @RequestParam(required = false) String role,
            @Parameter(description = "Company name keyword", example = "IBM")
            @RequestParam(required = false) String company,
            @Parameter(description = "Technology / skill keyword", example = "Java")
            @RequestParam(required = false) String skill,
            @Parameter(description = "Experience level keyword", example = "Senior")
            @RequestParam(required = false) String experience,
            @Parameter(description = "Job type", example = "Full-time")
            @RequestParam(required = false) String jobType) {
        return ResponseEntity.ok(jobService.searchJobs(role, company, skill, experience, jobType));
    }

    @GetMapping("/search/global")
    @Operation(summary = "Global keyword search",
            description = "Searches across job title, skills, company name, and description in one query")
    public ResponseEntity<JobSearchResponse> globalSearch(
            @Parameter(description = "Keyword to search everywhere", example = "Python") @RequestParam String q) {
        return ResponseEntity.ok(jobService.globalSearch(q));
    }

    @GetMapping("/company/{companyName}")
    @Operation(summary = "Jobs by company", description = "Returns all jobs posted by a specific company")
    public ResponseEntity<JobSearchResponse> getJobsByCompany(
            @Parameter(description = "Exact company name", example = "Flipkart") @PathVariable String companyName) {
        return ResponseEntity.ok(jobService.getJobsByCompany(companyName));
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Jobs by role", description = "Returns jobs whose title contains the given keyword")
    public ResponseEntity<JobSearchResponse> getJobsByRole(
            @Parameter(description = "Role keyword", example = "Engineer") @PathVariable String role) {
        return ResponseEntity.ok(jobService.getJobsByRole(role));
    }

    @GetMapping("/skill/{skill}")
    @Operation(summary = "Jobs by skill", description = "Returns jobs requiring the specified technology or skill")
    public ResponseEntity<JobSearchResponse> getJobsBySkill(
            @Parameter(description = "Skill / technology", example = "Kubernetes") @PathVariable String skill) {
        return ResponseEntity.ok(jobService.getJobsBySkill(skill));
    }

    @GetMapping("/experience/{level}")
    @Operation(summary = "Jobs by experience level", description = "Returns jobs matching the given experience level")
    public ResponseEntity<JobSearchResponse> getJobsByExperience(
            @Parameter(description = "Experience level", example = "Mid Level (2-5 years)") @PathVariable String level) {
        return ResponseEntity.ok(jobService.getJobsByExperience(level));
    }

    @GetMapping("/filters")
    @Operation(summary = "Get filter options", description = "Returns all distinct companies, skills, experience levels, and job types for building filter UIs")
    public ResponseEntity<Map<String, Object>> getFilterOptions() {
        return ResponseEntity.ok(jobService.getFilterOptions());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh job listings", description = "Triggers a live scrape from LinkedIn to fetch the latest job postings")
    public ResponseEntity<JobSearchResponse> refreshJobs() {
        return ResponseEntity.ok(jobService.refreshJobs());
    }
}
