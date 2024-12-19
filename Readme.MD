# Quest Web Application

[![Live Application](https://img.shields.io/badge/Live-Application-brightgreen)](https://quests-app.shubchynskyi.pp.ua)

A web application for creating and completing text quests, developed to demonstrate professional web application development skills using Java.

## Overview

This application allows users to create and manage text-based quests with flexible editing capabilities and robust user management. Comprehensive testing ensures quality, and a fully automated CI/CD pipeline facilitates seamless deployments.

## Features Implemented

- **User Management**: Manage users with roles, providing role-based access and functionality tailored to different types of users.
- **Quest Creation**: Create quests from text templates.
- **Quest Editing**: Edit quests even after creation, allowing for flexibility and updates.
- **Testing**: Comprehensive unit, integration, and end-to-end tests using JUnit, Testcontainers, and Selenium to ensure reliability and validate user scenarios in a simulated real-world environment.
- **Logging**: Persistent logging to assist with monitoring and debugging. Logs are stored and preserved across application updates.
- **Automated CI/CD Pipeline**: The project is built, tested, and deployed using Jenkins on a server within Docker, ensuring continuous integration and delivery. The application is tested in isolated Docker containers before deployment to verify stability.

## Technologies Used

- **Backend**: Java with Spring Boot, Spring Data JPA, Liquibase for database migrations.
- **Frontend**: Thymeleaf for dynamic rendering of pages, combined with HTML, JavaScript, and Bootstrap for responsive design.
- **Testing**: JUnit for unit tests, Testcontainers for integration tests, and Selenium for end-to-end web testing, all running within Docker environments.
- **Deployment**: The application is packaged and deployed using Docker and Docker Compose, managed by a Jenkins CI/CD pipeline with secure credential management.

## How to run from IntelliJ IDEA

1. **Ensure Docker is Running**
   - Make sure Docker is running on your machine before executing the script. You can check by running `docker ps` in your terminal.
   ```bash
   bash -c docker ps
   ```
2. **Test and build jar file**:
    - Run the [build-and-test.sh](build-and-test.sh) script from the root of the project using Git Bash or another compatible terminal.
   ```bash
   ./build-and-test.sh
   ```
3. **Deploy App with Docker**:
   - Run the [deploy.sh](deploy.sh) script from the root of the project using Git Bash or another compatible terminal with database credential and local compose file.
   ```bash
   DB_USERNAME=postgres DB_PASSWORD=postgres bash -c "./deploy.sh docker-compose-local.yaml"
   ```
4. **Access the Application**  
   - Once the deployment completes, open the application in your browser: [http://localhost:8080](http://localhost:8080) 
   ```
   http://localhost:8080
   ```
5. **Remove all local data**
   - To remove all local data from docker run script:  
   ```bash
   ./local-docker-cleanup.sh
   ```

## Contact

For any questions or further information, please contact [d.shubchynskyi@gmail.com](mailto:d.shubchynskyi@gmail.com)
   ```
   d.shubchynskyi@gmail.com
   ```