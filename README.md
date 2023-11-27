# Banking Services

This repository contains four microservices written in Springboot for banking services. Follow the instructions below to pull the repository and run each microservice.

## Pulling the Repository
1. Open your terminal or Git Bash.
2. Navigate to the desired directory where you want to clone the repository.
3. Run the following command:

git clone https://github.com/DereckChamboko/BankingServices.git

## Running the Microservices

### DiscoveryServer
1. Navigate into the `DiscoveryServer` in the root of your repository clone folder.
2. Open it with your favorite IDE.
3. Build and run the project.
- Note: If port 8761 is already in use, modify the `application.properties` file in the `discovery server` project files and change the server port accordingly.

### Account-Managment-Service
1. Navigate into the `Account-Managment-Service` in the root of your repository clone folder.
2. Open it with your favorite IDE.
3. This application runs on port 8762. If port 8762 is already in use, modify the `application.properties` file of this project to change the port.
4. Ensure you have a RabbitMQ instance and a Redis instance installed on your local machine.
5. Make sure the Discovery server is running.
6. Build and run the application.
7. The documentation for this service should be available at `http://localhost:8762/swagger-ui/index.html`, where 8762 is the port specified in your `application.properties` file.

### TransactionProcessingService
1. Navigate into the `TransactionProcessingService` in the root of your repository clone folder.
2. Open it with your favorite IDE.
3. This application runs on port 8763. If port 8763 is already in use, modify the `application.properties` file of this project to change the port.
4. Ensure you have a RabbitMQ instance and a Redis instance installed on your local machine.
5. Make sure the Discovery server is running.
6. Build and run the application.
7. The documentation for this service should be available at `http://localhost:8763/swagger-ui/index.html`, where 8763 is the port specified in your `application.properties` file.

### Customer-Support-Service
1. Navigate into the `Customer-Support-Service` in the root of your repository clone folder.
2. Open it with your favorite IDE.
3. This application runs on port 8764. If port 8764 is already in use, modify the `application.properties` file of this project to change the port.
4. Ensure you have a RabbitMQ instance and a Redis instance installed on your local machine.
5. Ensure you have mongodb installed and running on your machine
6. Make sure the Discovery server is running.
7. Build and run the application.
8. The documentation for this service should be available at `http://localhost:8764/swagger-ui/index.html`, where 8764 is the port specified in your `application.properties` file.
