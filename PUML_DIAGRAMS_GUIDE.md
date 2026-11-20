# Email Notification Service - PlantUML Diagrams

This document provides an overview of the PlantUML diagrams created for the Email Notification Service API.

## Diagrams Included

### 1. **EmailNotificationAPI_Sequence.puml** 
**Type:** Sequence Diagram  
**Purpose:** Shows the complete flow of a request through the application

**Components:**
- Client → Controller → Service → Repository → Database
- Exception Handler for error scenarios
- CLOB content handling
- Sequence auto-generation and timestamp creation

**Scenarios Covered:**
- ✅ Success path (200 OK)
- ✅ Validation error (400 BAD_REQUEST) - missing appId
- ✅ Database error (500 INTERNAL_SERVER_ERROR)

**Flow Details:**
```
1. Client sends POST /email/save with JSON request
2. Controller parses and validates JSON
3. Controller calls Service.saveEmailNotification()
4. Service validates appId (not null, not empty)
5. Service converts ApplicationEmailRequest to IOrigNotification entity
6. Service calls Repository.save()
7. Repository executes JPA persist operation
8. Hibernate converts to SQL INSERT statement
9. Oracle Database:
   - Executes INSERT
   - Auto-generates INTF_SEQ_ID (via sequence)
   - Auto-sets INTF_CREATED_ON (via CURRENT_TIMESTAMP)
   - Writes CLOB content (via LOB handling)
10. Repository returns saved entity
11. Service builds EmailNotificationResponse
12. Controller returns HTTP 200 with response body
```

---

### 2. **EmailNotificationAPI_ClassDiagram.puml**
**Type:** Class Diagram  
**Purpose:** Shows the structure and relationships between classes

**Packages:**
- **Controller Layer**
  - EmailNotificationController (REST endpoint handler)
  
- **Service Layer**
  - EmailNotificationService (business logic)
  
- **Repository Layer**
  - IOrigNotificationRepository (Spring Data JPA)
  
- **Entity Layer**
  - IOrigNotification (JPA entity, CLOB support)
  
- **DTO Layer**
  - ApplicationEmailRequest (input)
  - EmailNotificationResponse (output)
  
- **Exception Layer**
  - ApplicationException (custom exception)
  - ErrorResponse (error payload)
  - GlobalExceptionHandler (centralized error handling)

**Relationships:**
```
Controller  uses  Service
Service     uses  Repository
Repository  manages  Entity
Service     accepts  ApplicationEmailRequest
Service     returns  EmailNotificationResponse
Service     throws   ApplicationException
Handler     handles  ApplicationException & returns ErrorResponse
```

---

### 3. **EmailNotificationAPI_ComponentDiagram.puml**
**Type:** Component Diagram  
**Purpose:** Shows the architecture and technology integration

**Packages:**
- **Client Application**
  - HTTP Client
  
- **Spring Boot Application**
  - REST API Layer (Controller)
  - Service Layer (Service + Exception Handler)
  - Data Access Layer (Repository + JPA)
  - Configuration (Application Config + Hibernate)
  
- **Oracle Database**
  - IORIG_NOTIFICATION Table
  - Oracle JDBC Driver

**Integration Points:**
```
HTTP Client
  ↓ (REST API: POST /email/save)
EmailNotificationController
  ↓ (Dependency Injection)
EmailNotificationService
  ↓ (Spring Data JPA)
IOrigNotificationRepository
  ↓ (Entity Mapping)
JPA Entity Mapper (Hibernate)
  ↓ (JDBC Connection)
Oracle JDBC Driver
  ↓ (SQL)
IORIG_NOTIFICATION Table
  ↓ (CLOB LOB Handling)
Database
```

**Configuration Details:**
- Hibernate Configuration enables CLOB handling
- non_contextual_creation: true for Oracle LOB support

---

### 4. **EmailNotificationAPI_StateDiagram.puml**
**Type:** State Diagram  
**Purpose:** Shows the states and transitions during request processing

**States:**
1. **ReceiveRequest** - Entry point
2. **ParseRequest** - JSON deserialization
3. **ValidateAppId** - Validate mandatory field
4. **AppIdNull** - Decision point (null or empty check)
5. **ConvertEntity** - Map DTO to Entity
6. **MapFields** - Field mapping
7. **CallRepository** - JPA save call
8. **DatabaseSave** - SQL execution
9. **GenerateSequence** - INTF_SEQ_ID generation
10. **GenerateTimestamp** - INTF_CREATED_ON timestamp
11. **WriteCLOB** - CLOB content handling
12. **DatabaseSuccess** - Commit transaction
13. **BuildResponse** - Create response DTO
14. **ReturnSuccess** - HTTP 200 OK
15. **ReturnError400** - HTTP 400 BAD_REQUEST
16. **ReturnError500** - HTTP 500 INTERNAL_SERVER_ERROR

**Transitions:**
```
Error Path:
ReceiveRequest → ParseRequest → ValidateAppId 
  → AppIdNull(YES) → ReturnError400 → SendError → END

Success Path:
ReceiveRequest → ParseRequest → ValidateAppId 
  → AppIdNull(NO) → ConvertEntity → MapFields 
  → CallRepository → DatabaseSave 
  → GenerateSequence → GenerateTimestamp → WriteCLOB 
  → DatabaseSuccess → BuildResponse → ReturnSuccess → END

Database Error:
... → DatabaseSave → DatabaseError(YES) 
  → ReturnError500 → SendError → END
```

---

## How to Use These Diagrams

### Viewing the Diagrams
1. **Online:** Use [PlantUML Online Editor](http://www.plantuml.com/plantuml/uml/)
2. **VS Code:** Install PlantUML extension and preview
3. **Generate Images:**
   ```bash
   plantuml -tpng EmailNotificationAPI_Sequence.puml
   plantuml -tsvg EmailNotificationAPI_Sequence.puml
   ```

### Integration with Documentation
- Include in Architecture Documentation
- Use in API Design Reviews
- Reference in Developer Onboarding
- Share in Technical Specifications

---

## Key Technical Aspects Shown

### 1. **Request Validation**
- appId is mandatory
- Returns 400 BAD_REQUEST if missing/empty
- Early validation prevents database operations

### 2. **CLOB Content Handling**
- Content field mapped as CLOB (unlimited size)
- Hibernate LOB configuration enables proper stream handling
- Oracle JDBC driver manages serialization

### 3. **Auto-Generated Fields**
- INTF_SEQ_ID: Generated via Oracle Sequence
- INTF_CREATED_ON: Set to CURRENT_TIMESTAMP

### 4. **Exception Handling**
- Global exception handler catches all errors
- ApplicationException for business validation (400)
- General Exception for system errors (500)

### 5. **Dependency Injection**
- Constructor injection for loose coupling
- Spring manages bean lifecycle
- Testability through Mockito

### 6. **Transactional Support**
- Service layer marked with @Transactional
- Ensures data consistency
- Automatic rollback on errors

---

## Database Mapping Summary

| Request Field | Entity Field | DB Column | Type | Size |
|---|---|---|---|---|
| appId | appSeqId | APP_SEQ_ID | NUMBER | 38 |
| ssn | ssn | INTF_SSN | VARCHAR2 | 40 |
| appStatus | stage | INTF_STAGE | VARCHAR2 | 100 |
| emailId | emailId | INTF_EMAIL_ID | VARCHAR2 | 100 |
| subject | subject | INTF_SUBJECT | VARCHAR2 | 255 |
| content | content | INTF_CONTENT | CLOB | Unlimited |
| createdBy | createdBy | INTF_CREATED_BY | VARCHAR2 | 40 |
| languageCode | languageCode | INTF_LANG_CODE | VARCHAR2 | 255 |
| - | interfaceSeqId | INTF_SEQ_ID | NUMBER | 38 (Auto) |
| - | createdOn | INTF_CREATED_ON | TIMESTAMP | (Auto) |

---

## Summary

These four PlantUML diagrams provide:
- **Sequence Diagram:** Request flow and interactions
- **Class Diagram:** Structural design and relationships
- **Component Diagram:** Technology stack and integration
- **State Diagram:** Request processing states and transitions

Together, they give a complete technical overview of the Email Notification Service API architecture and implementation.
