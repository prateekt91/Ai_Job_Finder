package com.jobfinder.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class JobSearchResponse {

    private int totalJobs;
    private String location;
    private LocalDateTime lastUpdated;
    private Map<String, String> appliedFilters;
    private List<JobDto> jobs;

    public JobSearchResponse(int totalJobs, String location, LocalDateTime lastUpdated,
                             Map<String, String> appliedFilters, List<JobDto> jobs) {
        this.totalJobs = totalJobs;
        this.location = location;
        this.lastUpdated = lastUpdated;
        this.appliedFilters = appliedFilters;
        this.jobs = jobs;
    }

    public int getTotalJobs() { return totalJobs; }
    public void setTotalJobs(int totalJobs) { this.totalJobs = totalJobs; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public Map<String, String> getAppliedFilters() { return appliedFilters; }
    public void setAppliedFilters(Map<String, String> appliedFilters) { this.appliedFilters = appliedFilters; }

    public List<JobDto> getJobs() { return jobs; }
    public void setJobs(List<JobDto> jobs) { this.jobs = jobs; }
}
