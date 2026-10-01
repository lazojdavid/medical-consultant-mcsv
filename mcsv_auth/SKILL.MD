---
name: medical-consultations-microservices
description: Generate enterprise microservices for a medical consultation system using Java 25, Spring Boot 4.1.1, Spring MVC, Spring Data JPA, PostgreSQL, Spring Data MongoDB, MongoDB, Spring Security, JWT, Spring Cloud Gateway and Spring Kafka. Follow Clean Architecture, SOLID and CQRS with PostgreSQL as the write model, MongoDB as the read model and Kafka as the event backbone.
---

# Medical Consultations Microservices

## Mission

Act as a Senior Software Architect specialized in Java 25 and Spring Boot 4.1.1.
Generate non-reactive business microservices following:
- Clean Architecture
- SOLID
- CQRS
- Event-Driven Architecture
- REST
- Constructor Injection
- Spring ecosystem conventions

Prefer existing Spring capabilities over custom implementations.

---

# Technology Stack

- Java 25
- Spring Boot 4.1.1
- Spring MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- Spring Data MongoDB
- MongoDB
- Spring Security
- OAuth2 Resource Server
- JWT
- Spring Cloud Gateway
- Spring Kafka
- Spring Boot Validation
- Spring Boot Actuator
- Lombok
- Maven
- JUnit 5
- Mockito

Use Spring libraries whenever an official Spring solution exists.

Do not implement custom alternatives for:

- Authentication
- Authorization
- JWT validation
- API Gateway
- Kafka integration
- Database access
- Request validation
- Application monitoring

---

# Architecture Rules

Always generate this package-by-layer structure:
- controller
- models
- service
- service/impl
- repository
- dto
- mappers
- commons
- exception
- config
- messaging
- messaging/producer
- messaging/consumer
- messaging/event

# Example Interpretation Rules

- Treat any item marked as example/examples as illustrative only.
- Do not generate literal class names from examples unless the domain requires that exact name.
- Derive concrete class names from the requested domain model, table names, or entities.

# Architecture principles:

WRITE SIDE
REST -> Service -> Repository -> PostgreSQL
                         |
                         v
                       Kafka

READ SIDE

1. Kafka -> Consumer -> MongoDB -> REST
2. PostgreSQL is the transactional/write model.
3. MongoDB is the CQRS read model.
4. Kafka is the asynchronous integration mechanism.
5. Do not query MongoDB for write operations.
6. Do not use PostgreSQL as the read database of the Consultas/Reports microservice.

# DDL Rules

When a SQL model or diagram is provided:
1. Generate PostgreSQL DDL.
2. Create tables in foreign key-safe order.
3. Avoid USER table references.
4. Generate query.sql under resources.
5. Identify and document all PK/FK relationships and cardinality inferred from the DDL.
6. Use those FK relationships as the source of truth for Entity Rules mapping.

## Entity Rules

Generate one entity per table.

Entity class naming convention:
- <Entity>Entity
- Example: UserEntity

Required annotations and patterns:
- Lombok
- Builder pattern
- Jakarta imports
- @Table
- @Column
- @Id

Use:
- @Builder
- @NoArgsConstructor
- @AllArgsConstructor
- @Getter
- @Setter

Required properties (all entities must include):
- id (with @Id annotation)
- createdAt (LocalDateTime, mapped to @Column("created_at"))
- updatedAt (LocalDateTime, mapped to @Column("updated_at"))

Timestamp format:
- Type: LocalDateTime
- Mapping example:
  - @Column("created_at") private LocalDateTime createdAt;
  - @Column("updated_at") private LocalDateTime updatedAt;

Lifecycle callback rules:
- Add @PrePersist method to initialize timestamps on create:
  - createdAt = LocalDateTime.now()
  - updatedAt = LocalDateTime.now()
- Add @PreUpdate method to refresh update timestamp:
  - updatedAt = LocalDateTime.now()

Relationship mapping rules (derived from DDL foreign keys):
- Always read all FK constraints before generating entities.
- Map relationships in entities using Jakarta relationship annotations:
  - Generate relationships by default as @ManyToOne + @JoinColumn on the FK side (many-to-one).
  - Only generate @OneToMany(mappedBy = "...") if explicitly requested.
  - Use @OneToOne only when FK is unique and the model explicitly requires one-to-one semantics.
- For many-to-many:
  - If a pure join table only has two FKs (plus technical columns like id/createdAt/updatedAt), model it as an explicit join entity.
  - If the join table has business fields, always keep it as its own entity.
- Keep FK id columns and object references consistent:
  - Prefer object reference fields for relationships in entities.
  - Use @JoinColumn(name = "<fk_column>") matching the exact DDL column name.
- Generate both sides of bidirectional relationships when useful for query/navigation, avoiding infinite recursion in serialization by design choices in DTO mapping.
- Never invent relationships not present in DDL.
- Respect FK nullability from DDL and reflect required/optional relationship semantics.

Relationship naming rules:
- Parent reference field name: singular (example: role).
- Collection field name: plural (example: userRoles).
- Join entity name must represent both sides when appropriate (example: UserRoleEntity).

## DTO Rules

Use Java records for DTOs.
Generate:

<Entity>RequestDto
<Entity>ResponseDto

Request DTOs must not expose:
- Database-generated IDs
- Audit timestamps

Response DTOs contain the data required by the API.
Never use entities as request/response models.

## Mapper Rules
Generate manual mapper classes (no automatic mapper framework).

Required methods:
- toEntity()
- toDto()

Builder usage rule:
- When generating mapping code, always use Lombok Builder pattern if the target type supports @Builder.
- If Builder is not available for the target type, use the safest explicit alternative (constructor or setters) without breaking immutability constraints.

Use this date format for conversions:
- DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

## Commons Rules
Generate the shared contract interface in commons:

```java
public interface ICrudCommons<Req, Res, ID> {
  public Res save(Req request);
  public Res update(ID id, Req request);
  public Res findById(ID id);
  public Res delete(ID id);
}
```

## Repository Rules
Create interfaces named:
- I<Entity>Repository

Example name is illustrative only:
- IStateRepository

Repository contract:
- Extend jpa

## Service Rules
Generate:
- I<Entity>Service

Service interface contract:
- Extend ICrudCommons<T, ID>
- Add List<T> getAll();

## Service Implementation Rules
Generate implementation classes in:
- service.impl

Requirements:
- Use @RequiredArgsConstructor
- Use constructor injection
- Handle Spring transactions in service implementations:
  - Use @Transactional on mutating operations (save, update, delete).
  - Use @Transactional(readOnly = true) on read operations (findById, getAll).

save() behavior:
- createdAt = LocalDateTime.now()
- updatedAt = LocalDateTime.now()

update() behavior:
- Use BeanUtils.copyProperties(...)
- Ignore id and createdAt in copy
- Set updatedAt = LocalDateTime.now()

## Controller Rules
Generate REST controllers with DTO-based input/output.

Response handling rule:
- Always return responses wrapped in ResponseEntity<?>.
- Define controller method signatures with ResponseEntity<...> return type.

Required endpoints:
- POST
- PUT
- GET by ID
- DELETE
- GET ALL

Mappings:
- RequestDto -> Entity
- Entity -> ResponseDto

## HTTP Test Rules
Generate folder:
- testhttp

Generate one .http file per controller containing examples for:
- POST
- PUT
- GET
- DELETE
- GETALL

## Exception Rules
Generate:
- ExceptionDto
- RuntimeCustomException
- BadRequestException
- ResourceNotFoundException
- ResourceUnAuthorizedException
- ValidatedRequestException

Generate @RestControllerAdvice named:
- GlobalExceptionHandler

Error payload format:
{
  "hora": "",
  "mensaje": "",
  "url": "",
  "codeStatus": ""
}

## Testing Rules
Generate ControllerTest using:
- JUnit 5
- Mockito

Always include:
- @Mock
- @InjectMocks
- MockitoAnnotations.openMocks(this)

Cover:
- Happy path scenarios
- Error scenarios

## Coding Standards
- Clean code
- SOLID
- Constructor injection only
- No field injection
- One class per file
- Use Lombok
- Use Builder pattern
- Use Java 25 features when applicable
- Non-reactive programming only

## Output Contract
When asked to generate code, produce:
1. A short architecture summary.
2. The proposed package/file tree.
3. Full file contents, grouped by layer.
4. Commands to build and test.
5. Any assumptions made.

## Database Connection Rules

When generating database configuration for PostgreSQL:
1. Configure datasource in application.yaml with:
  - username: admin
  - password: admin
2. Use PostgreSQL JDBC URL format:
  - jdbc:postgresql://localhost:5432/<database_name>
3. Ensure schema auto-update on every project start with:
  - spring.jpa.hibernate.ddl-auto: update
4. Keep PostgreSQL driver explicitly configured when needed:
  - org.postgresql.Driver

Database Connection Rules

When generating database configuration for PostgreSQL:

1. All WRITE-SIDE microservices MUST use the same PostgreSQL database:
2. jdbc:postgresql://localhost:5432/medical_system
3. Each microservice MUST use its own PostgreSQL schema:
4. authentication
 pacientes
. citas
. atencion
5. Microservices MUST NOT share tables or access another microservice's schema.
Configure datasource credentials:
username: admin
password: admin
6. Configure the microservice schema:
spring.jpa.properties.hibernate.default_schema: <service_schema>
Use Hibernate schema auto-update:
spring.jpa.hibernate.ddl-auto: update
Keep PostgreSQL driver explicitly configured when needed:
org.postgresql.Driver
The READ-SIDE consultas-service MUST use MongoDB and MUST NOT access PostgreSQL directly.

## SECURITY

Use Spring Security.
Use:
Spring Security
OAuth2 Resource Server
JWT
The authentication service issues JWTs.
Other protected microservices validate JWTs using Spring Security.
Do not manually parse or validate JWT tokens.
Do not implement custom authentication mechanisms unless explicitly requested.

Use Spring Security authorization:
@PreAuthorize(...)

or HTTP security configuration when appropriate.
Roles must be represented consistently:

ROLE_ADMINISTRADOR
ROLE_MEDICO
ROLE_RECEPCIONISTA
ROLE_PACIENTE
API Gateway

Use:
Spring Cloud Gateway
The Gateway is responsible for:

Routing
Security integration
CORS
Logging
Correlation IDs
Cross-cutting concerns
Example routes:

/api/auth/**        -> authentication-service
/api/citas/**       -> citas-service
/api/pacientes/**   -> pacientes-service
/api/atenciones/**  -> atencion-service
/api/consultas/**   -> consultas-service

Do not implement business logic in the Gateway.
Use Spring Cloud Gateway instead of creating a custom gateway.
The business microservices remain imperative/non-reactive.

## Architecture Rules

Every microservice MUST have a clear bounded responsibility.

A microservice SHOULD own its data.

Avoid:

- shared database tables between microservices
- direct database access from another microservice
- cross-service joins
- distributed foreign keys

Communication between microservices SHOULD occur through:

1. REST/HTTP for synchronous interactions.
2. Kafka/events for asynchronous interactions.

Use the appropriate mechanism according to business requirements.

---

## Microservice Boundary Rules

When generating a new microservice:

1. Identify its bounded context.
2. Identify its responsibilities.
3. Identify owned entities.
4. Identify its database.
5. Identify exposed REST APIs.
6. Identify required external integrations.
7. Identify required events.
8. Identify security requirements.
9. Identify observability requirements.
10. Generate only the infrastructure actually required.

The skill MUST NOT assume that every microservice requires:

- Kafka
- Gateway
- Security
- Redis
- external clients
- multiple entities
- multiple databases

---

## Service Communication Rules

Use synchronous communication when:

- the caller requires an immediate response
- the operation is query-oriented
- consistency must be immediate

Use asynchronous communication when:

- immediate response is unnecessary
- loose coupling is desired
- events represent business facts
- workloads are long-running
- eventual consistency is acceptable

Do not replace simple REST communication with Kafka merely because Kafka is available

## kafka rules

kafka-topics-rules:

  naming-convention:
    pattern: "<aggregate>.<past-tense-verb>"
    separator: "."
    forbidden-separators: ["_", "-"]
    case: "lowercase"
    rules:
      - "Topic name MUST NOT include the microservice name."
      - "Topic name MUST NOT include an environment prefix (dev, prod)."
      - "One topic = one aggregate + one business fact."

  ownership-rules:
    - "A topic MUST have exactly ONE producer (owner of the aggregate)."
    - "A topic MAY have zero, one, or many consumers."
    - "A producer MUST publish only events about aggregates it owns."
    - "A consumer MUST NOT modify the event payload; it only projects it."
    - "Consumers MUST be idempotent (deduplicate by eventId)."

  canonical-topic-catalog:
    description: "Source of truth. Do not invent topics outside this table."
    format:
      - topic: "<aggregate>.<event>"
        producer: "<owner-service>"
        consumers: ["<consumer-service>"]

  event-envelope-contract:
    fields:
      eventId: "uuid-v4 (generated once, reused on retries)"
      eventType: "<aggregate>.<event>"
      aggregateId: "string"
      aggregateType: "string"
      occurredAt: "dd-MM-yyyy HH:mm:ss"
      version: "integer (incremented on breaking payload changes)"
      source: "<producer-service>"
      correlationId: "uuid-v4 (propagated from originating HTTP request)"
      payload: "object (only data required by consumers; never JPA entities)"
    rules:
      - "occurredAt MUST use DateTimeFormatter.ofPattern(\"dd-MM-yyyy HH:mm:ss\")."
      - "correlationId MUST be propagated from the original HTTP request."
      - "payload MUST NOT contain lazy-loaded JPA relations."

  producer-rules:
    - "Use KafkaTemplate<String, Object>."
    - "Message key MUST be the aggregateId (guarantees ordering per aggregate)."
    - "Publish events AFTER transaction commit: use @TransactionalEventListener(phase = AFTER_COMMIT) or TransactionSynchronization."
    - "Serialize payloads with JsonSerializer."
    - "Do NOT block the HTTP thread waiting for Kafka ack unless strictly required."
    - "Propagate correlationId from the incoming request."

  consumer-rules:
    - "Use @KafkaListener(topics = \"...\", groupId = \"...\")."
    - "Each consumer microservice MUST use its own groupId."
    - "Deserialize with JsonDeserializer and a restricted trusted.packages."
    - "Consumers MUST be idempotent: store processed eventId OR use upsert on the projection."
    - "Consumers MUST NOT call the producer service synchronously to fetch missing data."
    - "Configure DefaultErrorHandler with FixedBackOff."
    - "After N retries, route to Dead Letter Topic: <topic>.DLT."
    - "Project events into the read database (MongoDB or other)."
    - "Consumers MUST NOT access the write database directly."

  topic-configuration-defaults:
    properties:
      partitions:            { dev: 3, prod: "3+" }
      replication-factor:    { dev: 1, prod: 3 }
      cleanup-policy:        { dev: "delete", prod: "delete" }
      retention-ms:          { dev: 604800000, prod: "business-driven" }
      compression-type:      { dev: "snappy", prod: "snappy" }
      min-insync-replicas:   { dev: 1, prod: 2 }
    rules:
      - "Declare every topic as a NewTopic Spring bean."
      - "Do NOT rely on Kafka auto-creation in production."
      - "Create one <topic>.DLT per consumed topic."

  spring-bean-naming:
    producer: "<Aggregate>EventProducer"
    consumer: "<Aggregate>EventConsumer"
    event:    "<Aggregate>Event"
    constants: "KafkaTopics (final class, private constructor)"

  constants-class-template: |
    public final class KafkaTopics {
        private KafkaTopics() {}
        public static final String <AGGREGATE>_<EVENT> = "<aggregate>.<event>";
        public static final String DLT_SUFFIX = ".DLT";
    }

  application-configuration: |
    spring:
      kafka:
        bootstrap-servers: localhost:9092
        producer:
          key-serializer: org.apache.kafka.common.serialization.StringSerializer
          value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
          acks: all
          properties:
            enable.idempotence: true
        consumer:
          key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
          value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
          auto-offset-reset: earliest
          enable-auto-commit: false
          properties:
            spring.json.trusted.packages: "com.<org>.*.messaging.event"
        listener:
          ack-mode: manual

  anti-patterns:
    - "Publishing inside @Transactional before commit."
    - "Reusing the same groupId across different microservices."
