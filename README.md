# Public Transport Performance Analysis for Auckland (Backend)

## Overview
This project analyses historical public transport performance data in Auckland 
and provides interpretable summary insights through a Spring Boot backend and visual dashboards.

## Tech Stack
- Backend
    - Java 17, Spring Boot (Spring Web)
    - RESTful APIs (JSON)
- Data & Storage
    - Data source: Official Auckland Transport open datasets (CSV)
    - ETL: Download → validate and clean → import into PostgreSQL
    - Database: PostgreSQL
- Frontend
    - Web-based frontend for data presentation
    - HTML / CSS / JavaScript (consuming backend APIs)
    - (Optional) React for interactive views
- Analytics & Visualisation
    - Python (Pandas) for data analysis
    - Power BI for dashboard development
- Tooling
    - Maven, Git/GitHub

## Data Flow
- AT open CSV datasets → PostgreSQL → Spring Boot REST APIs → Power BI / Python analysis

## MVP Features
- Traffic summary inquiry
- Station or route delay overview
- Performance analysis dashboard

## Documentation
- Project Proposal: docs/project-proposal.md