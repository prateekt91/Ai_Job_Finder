package com.jobfinder.service;

import com.jobfinder.model.Job;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Component
public class JobDataProvider {

    private static final Logger log = LoggerFactory.getLogger(JobDataProvider.class);

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    @Value("${job.scrape.timeout-ms:20000}")
    private int timeoutMs;

    private final List<String> companyNames = new ArrayList<>();

    public void setCompanyNames(List<String> names) {
        this.companyNames.clear();
        this.companyNames.addAll(names);
    }

    public List<Job> fetchLatestJobs() {
        List<Job> jobs = scrapeLinkedInJobs();
        log.info("Total jobs fetched from LinkedIn: {}", jobs.size());
        return jobs;
    }

    private List<Job> scrapeLinkedInJobs() {
        List<Job> jobs = new ArrayList<>();

        List<String> searchTerms = List.of(
                "Software Engineer", "Java Developer", "Data Scientist",
                "DevOps", "Full Stack Developer", "Cloud Engineer",
                "Product Manager", "QA Engineer", "Frontend Developer",
                "Backend Developer", "Machine Learning", "Python Developer"
        );

        for (String term : searchTerms) {
            try {
                String encoded = URLEncoder.encode(term + " Manyata Tech Park Bangalore",
                        StandardCharsets.UTF_8);
                String url = "https://www.linkedin.com/jobs/search?keywords=" + encoded
                        + "&location=Bengaluru%2C+Karnataka%2C+India&position=1&pageNum=0";

                log.debug("Scraping LinkedIn for: {}", term);

                Document doc = Jsoup.connect(url)
                        .userAgent(USER_AGENT)
                        .header("Accept-Language", "en-US,en;q=0.9")
                        .timeout(timeoutMs)
                        .get();

                Elements cards = doc.select("div.base-card");
                if (cards.isEmpty()) {
                    cards = doc.select("li div.base-search-card");
                }
                if (cards.isEmpty()) {
                    cards = doc.select("ul.jobs-search__results-list li");
                }

                for (Element card : cards) {
                    try {
                        String title = extractText(card, "h3.base-search-card__title", "h3");
                        String company = extractText(card, "h4.base-search-card__subtitle a",
                                "h4.base-search-card__subtitle", "h4");
                        String location = extractText(card, "span.job-search-card__location", "span");
                        String jobUrl = extractJobUrl(card);

                        if (title == null || title.isBlank() || company == null || company.isBlank()) {
                            continue;
                        }

                        if (!isRelevantToManyata(company, location)) {
                            continue;
                        }

                        String dateText = extractText(card, "time");
                        LocalDate postedDate = parseDateText(dateText);

                        Job job = new Job(
                                title.trim(),
                                company.trim(),
                                location != null ? location.trim() : "Bengaluru, Karnataka, India",
                                inferExperience(title),
                                inferSkills(title),
                                null,
                                jobUrl,
                                null,
                                "Full-time",
                                "LinkedIn",
                                postedDate
                        );
                        jobs.add(job);
                    } catch (Exception e) {
                        log.debug("Skipping card due to parse error: {}", e.getMessage());
                    }
                }

                Thread.sleep(1500 + new Random().nextInt(1000));
            } catch (IOException e) {
                log.warn("Failed to scrape LinkedIn for '{}': {}", term, e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        log.info("Scraped {} jobs from LinkedIn", jobs.size());
        return jobs;
    }

    private boolean isRelevantToManyata(String company, String location) {
        String companyLower = company.toLowerCase();
        for (String name : companyNames) {
            String nameParts = name.toLowerCase().split("\\s+")[0];
            if (companyLower.contains(nameParts) || nameParts.contains(companyLower)) {
                return true;
            }
        }
        if (location != null) {
            String locLower = location.toLowerCase();
            if (locLower.contains("manyata") || locLower.contains("nagavara") || locLower.contains("hebbal")) {
                return true;
            }
        }
        return false;
    }

    private String extractText(Element parent, String... selectors) {
        for (String sel : selectors) {
            Element el = parent.selectFirst(sel);
            if (el != null && !el.text().isBlank()) {
                return el.text().trim();
            }
        }
        return null;
    }

    private String extractJobUrl(Element card) {
        String[] selectors = {
                "a.base-card__full-link",
                "a.base-search-card__full-link",
                "a[data-tracking-control-name='public_jobs_jserp-result_search-card']"
        };

        for (String sel : selectors) {
            Element el = card.selectFirst(sel);
            if (el != null && el.hasAttr("href")) {
                return cleanLinkedInUrl(el.attr("href"));
            }
        }

        for (Element a : card.select("a[href]")) {
            String href = a.attr("href");
            if (href.contains("/jobs/view/")) {
                return cleanLinkedInUrl(href);
            }
        }

        return null;
    }

    private String cleanLinkedInUrl(String href) {
        if (href == null || href.isBlank()) return null;
        href = href.trim();

        if (href.contains("/jobs/view/")) {
            int viewIdx = href.indexOf("/jobs/view/");
            String afterView = href.substring(viewIdx + "/jobs/view/".length());
            String jobId = afterView.split("[/?]")[0];
            if (!jobId.isEmpty()) {
                return "https://www.linkedin.com/jobs/view/" + jobId;
            }
        }

        if (href.contains("?")) {
            href = href.substring(0, href.indexOf("?"));
        }
        return href;
    }

    private LocalDate parseDateText(String dateText) {
        if (dateText == null) return LocalDate.now();
        String lower = dateText.toLowerCase();
        if (lower.contains("today") || lower.contains("just now") || lower.contains("hour")) {
            return LocalDate.now();
        } else if (lower.contains("yesterday") || lower.contains("1 day")) {
            return LocalDate.now().minusDays(1);
        } else if (lower.contains("day")) {
            try {
                int days = Integer.parseInt(lower.replaceAll("[^0-9]", ""));
                return LocalDate.now().minusDays(days);
            } catch (NumberFormatException e) {
                return LocalDate.now().minusDays(3);
            }
        } else if (lower.contains("week")) {
            try {
                int weeks = Integer.parseInt(lower.replaceAll("[^0-9]", ""));
                return LocalDate.now().minusWeeks(weeks);
            } catch (NumberFormatException e) {
                return LocalDate.now().minusWeeks(1);
            }
        } else if (lower.contains("month")) {
            return LocalDate.now().minusMonths(1);
        }
        return LocalDate.now();
    }

    private String inferExperience(String title) {
        String lower = title.toLowerCase();
        if (lower.contains("intern")) return "Intern";
        if (lower.contains("junior") || lower.contains("jr") || lower.contains("entry")
                || lower.contains("associate") || lower.contains("fresher")) return "Entry Level (0-2 years)";
        if (lower.contains("senior") || lower.contains("sr.") || lower.contains("lead")
                || lower.contains("staff")) return "Senior (5-10 years)";
        if (lower.contains("principal") || lower.contains("architect") || lower.contains("director")
                || lower.contains("head") || lower.contains("vp")) return "Expert (10+ years)";
        if (lower.contains("manager")) return "Senior (5-10 years)";
        return "Mid Level (2-5 years)";
    }

    private String inferSkills(String title) {
        String lower = title.toLowerCase();
        List<String> skills = new ArrayList<>();

        Map<String, String> skillMap = new LinkedHashMap<>();
        skillMap.put("java", "Java");
        skillMap.put("spring", "Spring Boot");
        skillMap.put("python", "Python");
        skillMap.put("react", "React");
        skillMap.put("angular", "Angular");
        skillMap.put("node", "Node.js");
        skillMap.put("javascript", "JavaScript");
        skillMap.put("typescript", "TypeScript");
        skillMap.put(".net", ".NET");
        skillMap.put("c#", "C#");
        skillMap.put("golang", "Go");
        skillMap.put("rust", "Rust");
        skillMap.put("kotlin", "Kotlin");
        skillMap.put("swift", "Swift");
        skillMap.put("ios", "iOS");
        skillMap.put("android", "Android");
        skillMap.put("flutter", "Flutter");
        skillMap.put("aws", "AWS");
        skillMap.put("azure", "Azure");
        skillMap.put("gcp", "GCP");
        skillMap.put("cloud", "Cloud");
        skillMap.put("devops", "DevOps");
        skillMap.put("kubernetes", "Kubernetes");
        skillMap.put("docker", "Docker");
        skillMap.put("data scien", "Data Science");
        skillMap.put("machine learning", "Machine Learning");
        skillMap.put("ml", "Machine Learning");
        skillMap.put("ai", "AI");
        skillMap.put("sql", "SQL");
        skillMap.put("database", "Database");
        skillMap.put("frontend", "Frontend");
        skillMap.put("front-end", "Frontend");
        skillMap.put("backend", "Backend");
        skillMap.put("back-end", "Backend");
        skillMap.put("full stack", "Full Stack");
        skillMap.put("fullstack", "Full Stack");
        skillMap.put("qa", "QA Testing");
        skillMap.put("test", "Testing");
        skillMap.put("sdet", "SDET");
        skillMap.put("selenium", "Selenium");
        skillMap.put("salesforce", "Salesforce");
        skillMap.put("sap", "SAP");
        skillMap.put("tableau", "Tableau");
        skillMap.put("power bi", "Power BI");
        skillMap.put("product", "Product Management");
        skillMap.put("scrum", "Agile/Scrum");
        skillMap.put("agile", "Agile/Scrum");
        skillMap.put("cybersec", "Cybersecurity");
        skillMap.put("security", "Security");
        skillMap.put("network", "Networking");
        skillMap.put("embedded", "Embedded Systems");

        for (Map.Entry<String, String> entry : skillMap.entrySet()) {
            if (lower.contains(entry.getKey())) {
                skills.add(entry.getValue());
            }
        }

        if (skills.isEmpty()) {
            if (lower.contains("engineer") || lower.contains("developer")) {
                skills.add("Software Development");
            } else {
                skills.add("General");
            }
        }

        return String.join(", ", skills);
    }

}
