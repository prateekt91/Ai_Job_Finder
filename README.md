# Manyata Tech Park - Company & Job Finder

A Spring Boot REST API that provides a comprehensive list of tech companies and **live job postings** from **Manyata Embassy Business Park** (Manyata Tech Park), Nagavara, Bengaluru - 560045.

## Features

### Companies
- REST API to fetch all companies in Manyata Tech Park
- **Live scraping** of company data from public sources (with hardcoded fallback)
- Filter companies by **sector** (IT, Finance, Healthcare, etc.)
- Search companies by name

### Jobs
- **Job listings** from 29+ companies in Manyata Tech Park
- **Live scraping** from LinkedIn + curated seed data
- **Advanced search** by role, technology/skill, experience level, company, and job type
- **Global keyword search** across titles, skills, companies, and descriptions
- **Filter options** endpoint for building dynamic UIs

### Auto-Refresh
- **Automatic weekly refresh** of both company data and job listings via `@Scheduled` tasks
- **Manual refresh** endpoints for on-demand updates

## Tech Stack

- Java 22, Spring Boot 4.0.3
- Spring Data JPA, H2 Database (file-based)
- Jsoup (HTML scraping)
- Maven

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+

### Run the application

```bash
mvn spring-boot:run
```

The server starts at `http://localhost:8080`.

---

## Company API Endpoints

| Method | Endpoint                        | Description                              |
|--------|---------------------------------|------------------------------------------|
| GET    | `/api/companies`                | Get all companies (sorted alphabetically)|
| GET    | `/api/companies/sector/{name}`  | Filter by sector                         |
| GET    | `/api/companies/search?q=query` | Search companies by name                 |
| GET    | `/api/companies/sectors`        | List all available sectors               |
| POST   | `/api/companies/refresh`        | Manually trigger a company data refresh  |

## Job API Endpoints

| Method | Endpoint                              | Description                                      |
|--------|---------------------------------------|--------------------------------------------------|
| GET    | `/api/jobs`                           | Get all job listings                             |
| GET    | `/api/jobs/search?role=&skill=&experience=&company=&jobType=` | Advanced multi-filter search |
| GET    | `/api/jobs/search/global?q=keyword`   | Global keyword search (title, skills, company, description) |
| GET    | `/api/jobs/company/{name}`            | Jobs by company name                             |
| GET    | `/api/jobs/role/{role}`               | Jobs by role/title                               |
| GET    | `/api/jobs/skill/{skill}`             | Jobs by technology/skill                         |
| GET    | `/api/jobs/experience/{level}`        | Jobs by experience level                         |
| GET    | `/api/jobs/filters`                   | Get all filter options (companies, skills, levels)|
| POST   | `/api/jobs/refresh`                   | Manually trigger job listings refresh            |

### Example Requests

```bash
# --- Companies ---
curl http://localhost:8080/api/companies
curl http://localhost:8080/api/companies/search?q=IBM
curl http://localhost:8080/api/companies/sectors

# --- Jobs ---
# Get all jobs
curl http://localhost:8080/api/jobs

# Search by skill + experience
curl "http://localhost:8080/api/jobs/search?skill=Java&experience=Senior"

# Search by role + company
curl "http://localhost:8080/api/jobs/search?role=Developer&company=Flipkart"

# Global keyword search
curl "http://localhost:8080/api/jobs/search/global?q=Python"

# Jobs at a specific company
curl http://localhost:8080/api/jobs/company/NVIDIA

# Jobs requiring Kubernetes
curl http://localhost:8080/api/jobs/skill/Kubernetes

# Entry level jobs
curl "http://localhost:8080/api/jobs/experience/Entry%20Level%20(0-2%20years)"

# Get all filter options (for building dropdowns)
curl http://localhost:8080/api/jobs/filters

# Force refresh jobs from sources
curl -X POST http://localhost:8080/api/jobs/refresh
```

## Scheduled Refresh

Both companies and jobs are automatically refreshed **every 7 days**. Configure intervals in `application.properties`:

```properties
company.refresh.interval-ms=604800000
job.refresh.interval-ms=604800000
```

## Swagger / API Documentation

Interactive API docs are available at:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON spec:

```
http://localhost:8080/api-docs
```

## H2 Console

For development, the H2 database console is available at:

```
http://localhost:8080/h2-console
```

- JDBC URL: `jdbc:h2:file:./data/manyata_companies`
- Username: `sa`
- Password: *(empty)*
