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
This online code testing platform is a robust web application designed to facilitate programming problem submissions and automated code evaluations. It allows users to submit solutions to various coding challenges, which are then automatically tested against predefined test cases to validate correctness and performance.
Please check section [Explanation of Terminologies](#explanation-of-terminologies) if you are not familiar with online judgement systems.

## Features
- **User Management:** Registration, authentication, and profile management.
- **Problem Management:** Create, update, and manage programming problems with detailed descriptions and test cases.
- **Problem Statistics:** Shows the fastest submissions for each problem and each language.
- **Code Submission:** Users can submit code in multiple programming languages.
- **Automated Testing:** Submissions are automatically compiled and executed within Docker containers to ensure isolated
  and secure evaluation.
- **Real-time Feedback:** Immediate results on submission status, including pass/fail notifications and performance
  metrics.
- **Scalability:** Designed with a modular architecture to handle a large number of concurrent users and submissions.
- **Caching** Caching frequently used data in the backend to reduce the number of queries to the database. (TODO)
- **Ranklist:** Ranklist of users ordered by scores.

## Technologies Used

- **Backend:** Java 17, Spring Boot 3, Spring Data JPA, Hibernate
- **Frontend:** React
- **Database:** PostgresSQL
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


This **Online Code Testing Platform** is a robust web application designed to facilitate programming problem submissions
and automated code evaluations. It allows users to submit solutions to various coding challenges, which are then
automatically tested against predefined test cases to validate correctness and performance.

## Explanation of Terminologies
This section is for understanding the terminologies used in online judgment systems. You can skip this section if you are already familiar with similar platforms such as LeetCode, Kattis, or Codeforces.

- **Online Judgement (OJ)**: An Online Judgement (OJ) system is a platform that allows users to solve programming challenges by submitting their code. The system automatically evaluates submissions against predefined test cases to determine their correctness and efficiency.

- **Problem**: A Problem is a programming challenge presented to users. It specifies the required tasks, input format, and output format. Solutions must meet certain performance constraints, including limits on CPU time and memory usage.

- **Solution**: A Solution is the code written to solve a given problem, or the compiled program resulting from that code.

- **Test Case**: A Test Case is a set of input data paired with its expected output, used to evaluate the correctness of a solution. Each problem typically includes multiple test cases, often arranged by increasing difficulty, to thoroughly assess various scenarios.

- **Code Testing**: Code Testing is the process of evaluating a solution by compiling the code and then running it against predefined test cases to determine correctness and efficiency.

- **Judgement**: Judgement is the outcome of the code testing process. It classifies solutions based on their performance with respect to the test cases and resource constraints.

  Common Judgement Types:
  - **Accepted (AC)**: The solution passed all test cases within the given resource limits.
  - **Wrong Answer (WA)**: The solution produced incorrect output for one or more test cases.
  - **Compilation Error (CE)**: The code failed to compile due to syntax or other compilation issues.
  - **Time Limit Exceeded (TLE)**: The solution did not complete execution within the specified time limit.
  - **Memory Limit Exceeded (MLE)**: The solution used more memory than allowed.
  - **Runtime Error (RE)**: The solution crashed or encountered an unexpected error during execution.
  - **Judgement Error (JE)**: An internal error occurred during the evaluation process.

- **Submission**: A Submission is an attempt at solving a problem by sending a solution to the OJ system. Each submission contains the code, its judgement result, execution time, memory usage, and the number of test cases passed.


## Installation

### Prerequisites:

- Docker
- Maven
- Java 17

### Steps:
#### 1. Configure the Datasource
Create a `datasource.properties` file to set up the application's datasource.
**File Path:** `src/main/resources/datasource.properties`
```
spring.datasource.url=your_database_url
spring.datasource.username=your_username
spring.datasource.password=your_password
```
#### 2. Install Docker and Create the oj-judge Image
**a. Install Docker** 
If Docker is not installed on your machine, download and install it.
**b. Create the code tester Image**
Navigate to the oj-judge folder and run the following command to build the image:
```
docker build -t code2 .
```
#### 3. Build and Run the Application Using Maven Wrapper
```
./mvnw clean install
./mvnw spring-boot:run
```
### Accessing the Application
The application will be running on port 8080.
