package com.jobfinder.service;

import com.jobfinder.model.Company;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CompanyDataProvider {

    private static final Logger log = LoggerFactory.getLogger(CompanyDataProvider.class);

    @Value("${company.scrape.url}")
    private String sourceUrl;

    @Value("${company.scrape.timeout-ms:15000}")
    private int timeoutMs;

    private static final Map<String, String> SECTOR_KEYWORDS = Map.ofEntries(
            Map.entry("health", "Healthcare & Pharma"),
            Map.entry("cerner", "Healthcare & Pharma"),
            Map.entry("pharma", "Healthcare & Pharma"),
            Map.entry("legato", "Healthcare & Pharma"),
            Map.entry("indegene", "Healthcare & Pharma"),
            Map.entry("philips", "Healthcare & Pharma"),
            Map.entry("optum", "Healthcare & Pharma"),
            Map.entry("illumina", "Healthcare & Pharma"),
            Map.entry("bank", "Finance & Banking"),
            Map.entry("anz", "Finance & Banking"),
            Map.entry("fidelity", "Finance & Banking"),
            Map.entry("morgan", "Finance & Banking"),
            Map.entry("northern trust", "Finance & Banking"),
            Map.entry("swiss re", "Finance & Banking"),
            Map.entry("axa", "Finance & Banking"),
            Map.entry("svb", "Finance & Banking"),
            Map.entry("rolls-royce", "Engineering & Manufacturing"),
            Map.entry("boeing", "Engineering & Manufacturing"),
            Map.entry("globalfoundries", "Engineering & Manufacturing"),
            Map.entry("andritz", "Engineering & Manufacturing"),
            Map.entry("nxp", "Semiconductors"),
            Map.entry("amazon", "E-Commerce & Retail"),
            Map.entry("flipkart", "E-Commerce & Retail"),
            Map.entry("myntra", "E-Commerce & Retail"),
            Map.entry("target", "E-Commerce & Retail"),
            Map.entry("lowes", "E-Commerce & Retail"),
            Map.entry("lowe's", "E-Commerce & Retail"),
            Map.entry("penney", "E-Commerce & Retail"),
            Map.entry("hudson", "E-Commerce & Retail"),
            Map.entry("ikea", "E-Commerce & Retail"),
            Map.entry("mast global", "E-Commerce & Retail"),
            Map.entry("concentrix", "BPO & Services"),
            Map.entry("sutherland", "BPO & Services"),
            Map.entry("qualitest", "BPO & Services"),
            Map.entry("dynacorp", "BPO & Services"),
            Map.entry("ascena", "BPO & Services"),
            Map.entry("kas services", "BPO & Services"),
            Map.entry("aditi staffing", "Staffing & HR"),
            Map.entry("jurimatrix", "Legal Tech"),
            Map.entry("tp vision", "Consumer Electronics"),
            Map.entry("justdial", "Internet Services"),
            Map.entry("l&t technology", "Engineering & Technology"),
            Map.entry("l & t technology", "Engineering & Technology")
    );

    public List<Company> fetchLatestCompanies() {
        List<Company> companies = new ArrayList<>();

        try {
            log.info("Scraping companies from: {}", sourceUrl);

            Document doc = Jsoup.connect(sourceUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(timeoutMs)
                    .get();

            Elements tables = doc.select("table");
            if (tables.isEmpty()) {
                log.warn("No tables found on the page.");
                return companies;
            }

            Element table = tables.first();
            Elements rows = table.select("tbody tr");

            if (rows.isEmpty()) {
                rows = table.select("tr");
            }

            for (Element row : rows) {
                Elements cols = row.select("td");
                if (cols.size() < 3) continue;

                String name = cleanText(cols.get(1).text());
                String address = cols.size() > 2 ? cleanText(cols.get(2).text()) : null;
                String email = cols.size() > 3 ? extractEmail(cols.get(3).text()) : null;
                String phone = cols.size() > 4 ? cleanText(cols.get(4).text()) : null;

                if (name.isEmpty() || name.equalsIgnoreCase("COMPANY")) continue;

                String sector = classifySector(name);
                companies.add(new Company(name, address, email, phone, sector));
            }

        } catch (IOException e) {
            log.error("Failed to scrape companies from {}. Error: {}", sourceUrl, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during scraping: {}", e.getMessage(), e);
        }

        log.info("Scraped {} companies from {}", companies.size(), sourceUrl);
        return companies;
    }

    private String classifySector(String companyName) {
        String lower = companyName.toLowerCase();
        for (Map.Entry<String, String> entry : SECTOR_KEYWORDS.entrySet()) {
            if (lower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "IT & Technology";
    }

    private String cleanText(String text) {
        if (text == null) return null;
        return text.replaceAll("\\s+", " ").trim();
    }

    private String extractEmail(String text) {
        if (text == null || text.isBlank()) return null;
        String cleaned = text.trim();
        if (cleaned.contains("@")) {
            String[] parts = cleaned.split("\\s+");
            for (String part : parts) {
                if (part.contains("@")) {
                    return part.replaceAll("[.,;]+$", "");
                }
            }
        }
        return null;
    }

}
