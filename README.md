# Auckland Traffic Flow Analysis (Backend)

## Overview
This project focuses on historical analyses of traffic flow on Auckland roads. The data is from Auckland Transport. It provides the analysis of traffic volumes and traffic peak periods via visual dashboards.

## Tech Stack
- Backend
    - Java 17, Spring Boot (Spring Web)
    - RESTful APIs (JSON)
- Data & Storage
    - Data source: Official Auckland Transport traffic count datasets (XLSX)
    - Data range: July 2012 to September 2025
    - ETL: Download → validate and clean → import into PostgreSQL
    - Database: PostgreSQL
- Frontend
    - Web-based frontend for data presentation
    - HTML / CSS / JavaScript (consuming backend APIs)
    - (Optional) React for interactive views
- Analytics & Visualization
    - Python (Pandas) for data analysis
    - Power BI for dashboard development
- Tooling
    - Maven, Git/GitHub

## Data Flow
- AT traffic count datasets → PostgreSQL → Spring Boot REST APIs → Power BI / Python analysis

## MVP Features
- User registration, activation, and login
- Traffic flow summary inquiry
- Traffic volume overview
- Peak period traffic analysis dashboard

## Project Structure
```text
src/main/java/io/github/pansai/traffic
├─ config/                 # Configuration Class
├─ handler/                # Global Handler
├─ controller/             # Control Layer
├─ dto/                    # Data Transfer Object
│  ├─ request/
│  └─ response/
├─ service/                # Business Logic Layer
│  └─ impl/
├─ dao/                    # Data Access Layer
├─ entity/                 # Entity Class (JPA Entity)
├─ enums/                  # Define Enumeration
└─ NZTrafficAnalysisBackendApplication  # Spring Boot Startup
```

## Documentation
- Project Proposal: docs/project-proposal.md

## Frontend Integration
The backend communicates with the frontend via REST APIs.
- Frontend: nz-traffic-analysis-frontend
- Frontend GitHub: https://github.com/pansai-77/nz-traffic-analysis-frontend.git