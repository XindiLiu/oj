# Online Code Testing Platform

[//]: # (![GitHub Repo stars]&#40;https://img.shields.io/github/stars/yourusername/your-repo-name?style=social&#41;)

[//]: # (![GitHub forks]&#40;https://img.shields.io/github/forks/yourusername/your-repo-name?style=social&#41;)

[//]: # (![GitHub license]&#40;https://img.shields.io/github/license/yourusername/your-repo-name&#41;)

[//]: # (## Table of Contents)

[//]: # (- [Overview]&#40;#overview&#41;)

[//]: # (- [Features]&#40;#features&#41;)

[//]: # (- [Technologies Used]&#40;#technologies-used&#41;)

[//]: # (- [Architecture]&#40;#architecture&#41;)

[//]: # (- [Installation]&#40;#installation&#41;)

[//]: # (- [Usage]&#40;#usage&#41;)

[//]: # (- [API Endpoints]&#40;#api-endpoints&#41;)

[//]: # (- [Contributing]&#40;#contributing&#41;)

[//]: # (- [License]&#40;#license&#41;)

[//]: # (- [Contact]&#40;#contact&#41;)

## Overview

The **Online Code Testing Platform** is a robust web application designed to facilitate programming problem submissions
and automated code evaluations. It allows users to submit solutions to various coding challenges, which are then
automatically tested against predefined test cases to validate correctness and performance.

## Features

- **User Management:** Registration, authentication, and profile management.
- **Problem Management:** Create, update, and manage programming problems with detailed descriptions and test cases.
- **Code Submission:** Users can submit code in multiple programming languages.
- **Automated Testing:** Submissions are automatically compiled and executed within Docker containers to ensure isolated
  and secure evaluation.
- **Real-time Feedback:** Immediate results on submission status, including pass/fail notifications and performance
  metrics.
- **Scalability:** Designed with a modular architecture to handle a large number of concurrent users and submissions.
- **Caching** Caching frequently used data in the backend to reduce the number of queries to the database. (TODO)
- **Leaderboard:** Track and display user rankings based on performance and scores. (TODO)

## Technologies Used

- **Backend:** Java, Spring Boot, Spring Data JPA, Hibernate
- **Frontend:** React
- **Database:** MySQL
- **Containerization:** Docker
- **Authentication:** Spring Security
- **Build Tools:** Maven
- **Other:** Lombok, RESTful APIs

## Architecture

[//]: # (![Architecture Diagram]&#40;docs/architecture-diagram.png&#41;)

The platform follows a **Microservices Architecture**, ensuring scalability and maintainability. Key components include:

- **Submission Service:** Handles code submissions, manages submission statuses, and interfaces with the code tester.
- **Problem Service:** Manages programming problems, including creation and retrieval of problem details and test cases.
- **User Service:** Manages user accounts, authentication, and profiles.
- **Code Tester:** Executes and evaluates code submissions within Docker containers to ensure isolated and secure
  testing environments.

[//]: # (## Installation)

[//]: # ()

[//]: # (### Prerequisites)

[//]: # ()

[//]: # (- Java 11 or higher)

[//]: # (- Maven)

[//]: # (- Docker)

[//]: # (- MySQL)

[//]: # (### Steps)

[//]: # ()

[//]: # (1. **Clone the Repository**)

[//]: # (    ```bash)

[//]: # (    git clone https://github.com/XindiLiu/oj.git)

[//]: # (    cd oj)

[//]: # (    ```)

[//]: # ()

[//]: # (2. **Configure the Database**)

[//]: # (    - Create a MySQL database.)

[//]: # (    - Update the `application.properties` file with your database credentials.)

[//]: # (    ```properties)

[//]: # (    spring.datasource.url=jdbc:mysql://localhost:3306/your_database)

[//]: # (    spring.datasource.username=your_username)

[//]: # (    spring.datasource.password=your_password)

[//]: # (    ```)

[//]: # ()

[//]: # (3. **Build the Project**)

[//]: # (    ```bash)

[//]: # (    mvn clean install)

[//]: # (    ```)

[//]: # ()

[//]: # (4. **Run Docker**)

[//]: # (    - Ensure Docker is running on your machine.)

[//]: # (    - Pull the necessary Docker images or build your own if required.)

[//]: # ()

[//]: # (5. **Start the Application**)

[//]: # (    ```bash)

[//]: # (    mvn spring-boot:run)

[//]: # (    ```)

[//]: # ()

[//]: # (6. **Access the Application**)

[//]: # (    - Open your browser and navigate to `http://localhost:8080`.)

[//]: # (## Usage)

[//]: # ()

[//]: # (1. **Register an Account**)

[//]: # (    - Navigate to the registration page and create a new account.)

[//]: # ()

[//]: # (2. **Create or Select a Problem**)

[//]: # (    - If you have administrative privileges, you can create new programming problems.)

[//]: # (    - Otherwise, browse through the existing problem set.)

[//]: # ()

[//]: # (3. **Submit Your Code**)

[//]: # (    - Select a problem and submit your solution in your preferred programming language.)

[//]: # ()

[//]: # (4. **View Results**)

[//]: # (    - Receive immediate feedback on your submission status, including which test cases passed or failed and performance)

[//]: # (      metrics.)
