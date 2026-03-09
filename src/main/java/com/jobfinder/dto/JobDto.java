package com.jobfinder.dto;

import com.jobfinder.model.Job;

import java.time.LocalDate;
import java.util.List;

public class JobDto {

    private Long id;
    private String title;
    private String companyName;
    private String location;
    private String experienceLevel;
    private List<String> skills;
    private String description;
    private String jobUrl;
    private String salary;
    private String jobType;
    private String source;
    private LocalDate postedDate;

    public static JobDto fromEntity(Job job) {
        JobDto dto = new JobDto();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setCompanyName(job.getCompanyName());
        dto.setLocation(job.getLocation());
        dto.setExperienceLevel(job.getExperienceLevel());
        dto.setDescription(job.getDescription());
        dto.setJobUrl(job.getJobUrl());
        dto.setSalary(job.getSalary());
        dto.setJobType(job.getJobType());
        dto.setSource(job.getSource());
        dto.setPostedDate(job.getPostedDate());

        if (job.getSkills() != null && !job.getSkills().isBlank()) {
            dto.setSkills(List.of(job.getSkills().split("\\s*,\\s*")));
        } else {
            dto.setSkills(List.of());
        }

        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getJobUrl() { return jobUrl; }
    public void setJobUrl(String jobUrl) { this.jobUrl = jobUrl; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDate getPostedDate() { return postedDate; }
    public void setPostedDate(LocalDate postedDate) { this.postedDate = postedDate; }
}
