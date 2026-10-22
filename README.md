# RepoSearch

RepoSearch is a Spring Boot REST API that searches GitHub repositories, stores the results in PostgreSQL, and provides a paginated API to retrieve stored repositories.

## Quick Start

### Requirements

- JDK 21
- PostgreSQL
- IntelliJ IDEA or a terminal

### Database setup

Create one database for local development and one separate database for integration tests:

```sql
CREATE USER repo_search_app WITH PASSWORD 'your_secure_password';

CREATE DATABASE repo_search OWNER repo_search_app;
CREATE DATABASE repo_search_test OWNER repo_search_app;
```

Flyway creates the application tables automatically when the application or integration tests start.

### Environment variables

Configure these variables in your IntelliJ run configuration or operating-system environment:

```text
DATABASE_URL=jdbc:postgresql://localhost:5432/repo_search
DATABASE_USERNAME=repo_search_app
DATABASE_PASSWORD=your_database_password
TEST_DATABASE_PASSWORD=your_database_password
GITHUB_TOKEN=optional_github_personal_access_token
```

`GITHUB_TOKEN` is optional, but increases the available GitHub API rate limit.

### Run locally

In IntelliJ, configure JDK 21, add the environment variables, and run `RepoSearchApplication`.

Or run from a Windows terminal:

```powershell
.\mvnw.cmd spring-boot:run
```

Open Swagger UI after startup:

```text
http://localhost:8080/swagger-ui/index.html
```

## Features

- Search GitHub repositories by query and optional language.
- Persist repositories in PostgreSQL.
- Use a PostgreSQL native upsert to update existing repositories without duplicates.
- Retrieve stored repositories with optional language and minimum-star filters.
- Paginate results with a default size of `20` and maximum size of `50`.
- Sort by supported fields in ascending or descending order.
- Validate requests and return consistent API error responses.
- Support an optional GitHub authentication token.
- Provide Swagger/OpenAPI documentation.
- Include MVC, service, and PostgreSQL integration tests.

## Technology Stack

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA and Hibernate ORM
- PostgreSQL
- Flyway
- Spring `RestClient`
- Maven
- JUnit 5 and Mockito
- Swagger UI with springdoc-openapi

## API Documentation

Swagger UI is available locally at:

```text
http://localhost:8080/swagger-ui/index.html
```

### Search and store repositories

```http
POST /api/github/search
```

Example request:

```json
{
  "query": "spring boot",
  "language": "Java",
  "sort": "stars",
  "direction": "desc"
}
```

- `query` is required.
- `language` is optional.
- `sort` defaults to `stars`.
- `direction` defaults to `desc`.

The API searches GitHub and stores the mapped results. Existing GitHub repository IDs are updated rather than duplicated.

### Retrieve stored repositories

```http
GET /api/github/repositories
```

Supported query parameters:

| Parameter | Required | Default | Description |
|---|---:|---|---|
| `language` | No | — | Exact programming-language filter |
| `minStars` | No | — | Inclusive minimum GitHub star count |
| `sort` | No | `stars` | `stars`, `forks`, or `updated` |
| `direction` | No | `desc` | `asc` or `desc` |
| `page` | No | `0` | Zero-based page number |
| `size` | No | `20` | Results per page; allowed range: `1`–`50` |

Example:

```text
GET /api/github/repositories?language=Java&minStars=100&sort=stars&direction=desc&page=0&size=20
```

## Architecture

```text
Controller
   ↓
Service
   ↓
GitHub API Client / Mapper / Repository
   ↓
PostgreSQL
```

```text
com.example.reposearch
├── client          # GitHub API client and external API DTOs
├── config          # Application configuration
├── controller      # REST endpoints
├── dto             # API request and response models
├── entity          # JPA entities
├── exception       # Exceptions and global error handling
├── mapper          # Data mapping
├── model           # Search and sorting models
├── repository      # JPA specifications and PostgreSQL upsert
└── service         # Business logic
```

## Database Design

Flyway migrations are stored in:

```text
src/main/resources/db/migration
```

The `github_repositories` table has a unique GitHub repository ID. Repository imports use PostgreSQL `INSERT ... ON CONFLICT ... DO UPDATE` for an efficient, atomic upsert.

JPA/Hibernate is used for entity mapping, filtering, pagination, and sorting. Native SQL is intentionally used only for the PostgreSQL-specific upsert operation.

## Tests

Run all tests:

```powershell
.\mvnw.cmd test
```

The test suite includes:

- MVC/controller tests for validation and successful HTTP responses.
- Service unit tests using Mockito.
- PostgreSQL integration tests for Flyway, native upsert, filters, sorting, and pagination.

Integration tests use the separate `repo_search_test` database. Ensure `TEST_DATABASE_PASSWORD` is available to IntelliJ's JUnit configuration.

## Deployment

The intended production topology is:

```text
Client
  ↓
Render: Spring Boot API
  ↓
Neon: PostgreSQL database
```

Deployment configuration and the public API URL will be added after Render and Neon are configured.
