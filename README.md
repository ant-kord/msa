# Microservices Project

This project implements three Spring Boot microservices in a single Maven multi-module repository:
- order-service (port 8081)
- payment-service (port 8082)
- delivery-service (port 8083)

Each service uses Spring Boot, Spring Data JPA, H2 in-memory database, and Lombok to reduce boilerplate. They expose CRUD REST APIs for managing domain objects and include integration tests and a Postman collection.

Features
- Entities, Value Objects (Embeddables), and Aggregates
- Repositories, Services (business logic & validation), Controllers (REST CRUD)
- H2 console enabled for each service
- DTOs for request/response mapping
- Postman collection with sample requests

Prerequisites
- Java 17+
- Maven 3.6+

Installation and Build
1. Clone or extract project files to a directory.
2. Build the entire project:
    mvn clean package

Running services
You can run individual services via Maven:

- Order service:
    mvn -pl :order-service spring-boot:run
  Service runs on port 8081. H2 console at http://localhost:8081/h2-console

- Payment service:
    mvn -pl :payment-service spring-boot:run
  Service runs on port 8082. H2 console at http://localhost:8082/h2-console

- Delivery service:
    mvn -pl :delivery-service spring-boot:run
  Service runs on port 8083. H2 console at http://localhost:8083/h2-console

You can also run all by starting multiple terminals with the commands above.

Configuration
Each service includes an `application.properties` file under `src/main/resources` that configures H2 and server ports.
You can override ports with environment variables, see `.env.example`.

API Usage Examples (curl)
- Create Order:
    curl -X POST http://localhost:8081/orders -H "Content-Type: application/json" -d '{"customerId":"11111111-1111-1111-1111-111111111111","items":[{"productId":"22222222-2222-2222-2222-222222222222","quantity":2,"price":10.5}]}'

- Create Payment:
    curl -X POST http://localhost:8082/payments -H "Content-Type: application/json" -d '{"orderId":"11111111-1111-1111-1111-111111111111","amount":21.0,"method":"CREDIT_CARD","paymentDetails":{"cardLast4":"4242"}}'

- Create Delivery:
    curl -X POST http://localhost:8083/deliveries -H "Content-Type: application/json" -d '{"orderId":"11111111-1111-1111-1111-111111111111","address":{"street":"123 Main","city":"City","postalCode":"12345","country":"Country"}}'

Postman
Import `postman_collection.json` into Postman to test CRUD endpoints for each service.

Running Tests
To run tests for a module:
- Order:
    mvn -pl :order-service test
- Payment:
    mvn -pl :payment-service test
- Delivery:
    mvn -pl :delivery-service test

Project Structure
- pom.xml (root)
- order-service/
  - src/main/java/com/example/order/... (entities, repositories, services, controllers)
  - src/test/java/... (integration tests)
  - src/main/resources/application.properties
- payment-service/
  - similar structure
- delivery-service/
  - similar structure
- postman_collection.json
- .env.example

Troubleshooting
- If a port is already in use, change the `server.port` in the module's `application.properties` or set the corresponding environment variable.
- If Lombok annotations don't work in your IDE, install Lombok plugin and enable annotation processing.
- For H2 console, JDBC URL is `jdbc:h2:mem:testdb`, username `sa`, no password.

If you need the full source for any specific file or a zip archive of the project, request it and it will be provided.
