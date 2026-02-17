 ProfitMax Scheduler Solutions – Project Scheduling System
📖 Overview

ProfitMax Scheduler Solutions Pvt. Ltd. is a project management company that handles multiple client software projects such as UI design, development, testing, and deployment.

This system automates project scheduling to maximize profit while following business constraints:

Company operates 5 days per week (Monday–Friday)

Maximum 5 projects per week

Only 1 project per day

Projects must be completed before their deadline

If a deadline is missed, the project revenue is lost

The objective is to automatically select and schedule projects in a way that maximizes total revenue while respecting all scheduling constraints.

🎯 Problem Statement

Each project contains:

Project ID (auto-generated)

Title

Deadline (in working days)

Expected Revenue

Example:
If deadline = 3
→ The project must be completed within the first 3 working days.

The system must:

Select projects strategically

Ensure deadlines are not violated

Maximize total weekly revenue

Schedule no more than 5 projects per week

🧠 Solution Approach

This system uses a Greedy Algorithm (Job Sequencing with Deadlines) to achieve optimal scheduling.

Steps:

Sort all projects in descending order of expected revenue

For each project, attempt to assign it to the latest available day before its deadline

If a slot is available, schedule the project

If no slot is available, skip the project

This approach ensures maximum possible revenue within the given constraints.

🏗 Tech Stack
Backend

Java

JDBC

Database

PostgreSQL 18

pgAdmin