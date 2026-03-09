package com.jobfinder.controller;

import com.jobfinder.dto.CompanyResponse;
import com.jobfinder.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "*")
@Tag(name = "Companies", description = "Endpoints to browse and search tech companies in Manyata Tech Park")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    @Operation(summary = "Get all companies", description = "Returns the full list of companies sorted alphabetically")
    public ResponseEntity<CompanyResponse> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/sector/{sector}")
    @Operation(summary = "Filter companies by sector", description = "Returns companies belonging to a given sector (e.g. IT & Technology, Finance & Banking)")
    public ResponseEntity<CompanyResponse> getCompaniesBySector(
            @Parameter(description = "Sector name", example = "IT & Technology") @PathVariable String sector) {
        return ResponseEntity.ok(companyService.getCompaniesBySector(sector));
    }

    @GetMapping("/search")
    @Operation(summary = "Search companies by name", description = "Case-insensitive partial match on company name")
    public ResponseEntity<CompanyResponse> searchCompanies(
            @Parameter(description = "Search query", example = "IBM") @RequestParam String q) {
        return ResponseEntity.ok(companyService.searchCompanies(q));
    }

    @GetMapping("/sectors")
    @Operation(summary = "List all sectors", description = "Returns distinct sector names available in the database")
    public ResponseEntity<Map<String, List<String>>> getAllSectors() {
        return ResponseEntity.ok(Map.of("sectors", companyService.getAllSectors()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh company data", description = "Triggers a live scrape to update the company list from the web source")
    public ResponseEntity<CompanyResponse> refreshCompanies() {
        return ResponseEntity.ok(companyService.refreshCompanies());
    }
}
