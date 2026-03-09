package com.jobfinder.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs", indexes = {
        @Index(name = "idx_job_company", columnList = "companyName"),
        @Index(name = "idx_job_title", columnList = "title"),
        @Index(name = "idx_job_experience", columnList = "experienceLevel")
})
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String companyName;

    @Column(length = 500)
    private String location;

    private String experienceLevel;

    @Column(length = 1000)
    private String skills;

    @Column(length = 5000)
    private String description;

    @Column(length = 1000)
    private String jobUrl;

    private String salary;

    private String jobType;

    private String source;

    @Column(nullable = false, unique = true, length = 500)
    private String fingerprint;

    private LocalDate postedDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Job() {}

    public Job(String title, String companyName, String location, String experienceLevel,
               String skills, String description, String jobUrl, String salary,
               String jobType, String source, LocalDate postedDate) {
        this.title = title;
        this.companyName = companyName;
        this.location = location;
        this.experienceLevel = experienceLevel;
        this.skills = skills;
        this.description = description;
        this.jobUrl = jobUrl;
        this.salary = salary;
        this.jobType = jobType;
        this.source = source;
        this.postedDate = postedDate;
        this.fingerprint = generateFingerprint();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.fingerprint == null) {
            this.fingerprint = generateFingerprint();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private String generateFingerprint() {
        String raw = (title + "|" + companyName + "|" + (jobUrl != null ? jobUrl : location))
                .toLowerCase().replaceAll("\\s+", "");
        return String.valueOf(raw.hashCode());
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

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

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

    public String getFingerprint() { return fingerprint; }
    public void setFingerprint(String fingerprint) { this.fingerprint = fingerprint; }

    public LocalDate getPostedDate() { return postedDate; }
    public void setPostedDate(LocalDate postedDate) { this.postedDate = postedDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
