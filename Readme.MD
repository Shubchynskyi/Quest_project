# Quest Web Application

A web application for creating and completing text quests.

Developed as part of an educational project at [Javarush University](https://javarush.com/university).

## Features Implemented

- **User Management**: Create, edit, and manage users with different roles.
- **User Roles**: Role-based access and functionality tailored to different types of users.
- **Quest Creation**: Create quests from text templates.
- **Quest Editing**: Edit quests even after creation, allowing for flexibility and updates.
- **Testing**: Comprehensive unit and integration tests using Testcontainers to ensure reliability and stability.
- **Logging**: Persistent logging to assist with monitoring and debugging. Logs are stored and preserved across application updates.
- **Automated CI/CD Pipeline**: The project is built, tested, and deployed using Jenkins on a server within Docker, ensuring continuous integration and delivery.

## Technologies Used

- **Backend**: Java with Spring Boot, Spring Data JPA, Liquibase for database migrations.
- **Frontend**: Thymeleaf, JSP, and JSTL for dynamic rendering of pages, combined with HTML and Bootstrap for responsive design.
- **Testing**: JUnit for unit tests and Testcontainers for integration tests, simulating real environments with Docker containers.
- **Deployment**: The application is packaged and deployed using Docker and Docker Compose, managed by a Jenkins CI/CD pipeline.

## How to Start from IntelliJ IDEA

1. **Update [`.env` File](./.env)**:
    - Change the `COMPOSE_FILE` setting in the `.env` file:
      ```plaintext
      # Change from:
      COMPOSE_FILE=docker-compose-server.yaml
      # To:
      COMPOSE_FILE=docker-compose-local.yaml
      ```
2. **Run the Build Script**:
    - Run the `local-build-and-run.sh` script from the root of the project using Git Bash or another compatible terminal:
      ```bash
      ./local-build-and-run.sh
      ```
    - [Link to the local-build-and-run.sh script](./local-build-and-run.sh)

3. **Ensure Docker is Running**:
    - Make sure Docker is running on your machine before executing the script. You can check by running `docker ps` in your terminal.


## Live Application

Explore the live version of the project here: [Quest Web Application](https://quests-app.shubchynskyi.pp.ua)