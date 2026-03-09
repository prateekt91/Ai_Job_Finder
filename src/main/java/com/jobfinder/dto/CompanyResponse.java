package com.jobfinder.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CompanyResponse {

    private int totalCompanies;
    private String location;
    private LocalDateTime lastUpdated;
    private List<CompanyDto> companies;

    public CompanyResponse(int totalCompanies, String location,
                           LocalDateTime lastUpdated, List<CompanyDto> companies) {
        this.totalCompanies = totalCompanies;
        this.location = location;
        this.lastUpdated = lastUpdated;
        this.companies = companies;
    }

    public int getTotalCompanies() { return totalCompanies; }
    public void setTotalCompanies(int totalCompanies) { this.totalCompanies = totalCompanies; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public List<CompanyDto> getCompanies() { return companies; }
    public void setCompanies(List<CompanyDto> companies) { this.companies = companies; }
}
