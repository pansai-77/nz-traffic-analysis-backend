# Road Traffic Flow Analysis for Auckland (Backend)

## Overview
This project analyses historical road traffic flow data in Auckland and provides interpretable summary insights on traffic volumes and peak periods through a Spring Boot backend and visual dashboards.
The project focuses on historical analysis rather than real-time traffic management or control.

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
- Analytics & Visualisation
    - Python (Pandas) for data analysis
    - Power BI for dashboard development
- Tooling
    - Maven, Git/GitHub

## Data Flow
- AT traffic count datasets → PostgreSQL → Spring Boot REST APIs → Power BI / Python analysis

## MVP Features
- Traffic flow summary inquiry
- Traffic volume overview
- Peak period traffic analysis dashboard

## Documentation
- Project Proposal: docs/project-proposal.md