package com.jobfinder.service;

import com.jobfinder.dto.CompanyDto;
import com.jobfinder.dto.CompanyResponse;
import com.jobfinder.model.Company;
import com.jobfinder.repository.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CompanyService {

    private static final Logger log = LoggerFactory.getLogger(CompanyService.class);
    private static final String LOCATION = "Manyata Embassy Business Park, Nagavara, Bengaluru-560045";

    private final CompanyRepository companyRepository;
    private final CompanyDataProvider companyDataProvider;

    private LocalDateTime lastRefreshed;

    public CompanyService(CompanyRepository companyRepository,
                          CompanyDataProvider companyDataProvider) {
        this.companyRepository = companyRepository;
        this.companyDataProvider = companyDataProvider;
    }

    public CompanyResponse getAllCompanies() {
        List<Company> companies = companyRepository.findAllByOrderByNameAsc();
        List<CompanyDto> dtos = companies.stream()
                .map(CompanyDto::fromEntity)
                .toList();

        return new CompanyResponse(dtos.size(), LOCATION, lastRefreshed, dtos);
    }

    public CompanyResponse getCompaniesBySector(String sector) {
        List<Company> companies = companyRepository.findBySectorIgnoreCase(sector);
        List<CompanyDto> dtos = companies.stream()
                .map(CompanyDto::fromEntity)
                .toList();

        return new CompanyResponse(dtos.size(), LOCATION, lastRefreshed, dtos);
    }

    public CompanyResponse searchCompanies(String query) {
        List<Company> companies = companyRepository.findByNameContainingIgnoreCase(query);
        List<CompanyDto> dtos = companies.stream()
                .map(CompanyDto::fromEntity)
                .toList();

        return new CompanyResponse(dtos.size(), LOCATION, lastRefreshed, dtos);
    }

    public List<String> getAllSectors() {
        return companyRepository.findAll().stream()
                .map(Company::getSector)
                .distinct()
                .sorted()
                .toList();
    }

    @Transactional
    public CompanyResponse refreshCompanies() {
        log.info("Refreshing company data (live scrape with hardcoded fallback)...");

        List<Company> latestCompanies = companyDataProvider.fetchLatestCompanies();

        if (latestCompanies.isEmpty()) {
            log.warn("No companies returned from provider. Skipping refresh to preserve existing data.");
            return getAllCompanies();
        }

        int added = 0;
        int updated = 0;

        for (Company incoming : latestCompanies) {
            var existing = companyRepository.findByNameIgnoreCase(incoming.getName());
            if (existing.isPresent()) {
                Company c = existing.get();
                if (incoming.getAddress() != null) c.setAddress(incoming.getAddress());
                if (incoming.getEmail() != null) c.setEmail(incoming.getEmail());
                if (incoming.getPhone() != null) c.setPhone(incoming.getPhone());
                if (incoming.getSector() != null) c.setSector(incoming.getSector());
                companyRepository.save(c);
                updated++;
            } else {
                companyRepository.save(incoming);
                added++;
            }
        }

        lastRefreshed = LocalDateTime.now();
        log.info("Refresh complete. Added: {}, Updated: {}, Total: {}",
                added, updated, companyRepository.count());

        return getAllCompanies();
    }

    @Transactional
    public void initializeIfEmpty() {
        if (companyRepository.count() == 0) {
            log.info("Database is empty. Seeding initial company data...");
            refreshCompanies();
        } else {
            lastRefreshed = LocalDateTime.now();
            log.info("Database already has {} companies.", companyRepository.count());
        }
    }
}
