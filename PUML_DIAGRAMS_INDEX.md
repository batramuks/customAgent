# Email Notification Service - PlantUML Diagrams Index

## 📊 Complete Diagram Suite

This project includes **5 comprehensive PlantUML diagrams** visualizing every aspect of the Email Notification Service API.

---

## 1. 📈 Sequence Diagram
**File:** `EmailNotificationAPI_Sequence.puml`

### Purpose
Shows the complete request/response flow through all layers of the application.

### Covers
- ✅ **Success Path:** Full flow from client to database and back (HTTP 200)
- ✅ **Error Path 1:** Validation error - missing appId (HTTP 400)
- ✅ **Error Path 2:** Database error during insert (HTTP 500)

### Components Shown
```
Client
  ↓ POST /email/save
EmailNotificationController
  ↓ saveEmailNotification()
EmailNotificationService
  ├─ Validate appId
  ├─ Convert to Entity
  └─ Call Repository
    ↓
IOrigNotificationRepository
  ↓ save()
Database
  ├─ Generate INTF_SEQ_ID (sequence)
  ├─ Set INTF_CREATED_ON (timestamp)
  ├─ Write INTF_CONTENT (CLOB)
  └─ Commit Transaction
    ↓
GlobalExceptionHandler (if error)
  ↓
Response (200/400/500)
```

### Key Features
- Transaction flow
- CLOB content handling
- Exception handling path
- Auto-generated IDs and timestamps

---

## 2. 🏗️ Class Diagram
**File:** `EmailNotificationAPI_ClassDiagram.puml`

### Purpose
Displays the structural design with all classes and their relationships.

### Packages
1. **Controller** - REST endpoint handler
2. **Service** - Business logic
3. **Repository** - Data access
4. **Entity** - JPA entity with CLOB support
5. **DTO** - Request/Response models
6. **Exception** - Error handling classes

### Classes
| Class | Purpose |
|-------|---------|
| EmailNotificationController | Handles POST /email/save |
| EmailNotificationService | Validates and processes request |
| IOrigNotificationRepository | Spring Data JPA interface |
| IOrigNotification | JPA entity (CLOB content field) |
| ApplicationEmailRequest | Input DTO (8 fields) |
| EmailNotificationResponse | Output DTO (3 fields) |
| ApplicationException | Custom exception with status code |
| ErrorResponse | Error response model |
| GlobalExceptionHandler | Centralized error handling |

### Relationships
```
Controller → Service → Repository
Service → ApplicationEmailRequest
Service → EmailNotificationResponse
Service → IOrigNotification (Entity)
Service → ApplicationException
GlobalExceptionHandler → ErrorResponse
```

---

## 3. 🔧 Component Diagram
**File:** `EmailNotificationAPI_ComponentDiagram.puml`

### Purpose
Shows the architecture layers and technology integration.

### Layers
1. **Client Application**
   - HTTP Client

2. **Spring Boot Application**
   - REST API Layer (Controller)
   - Service Layer (Service + Exception Handler)
   - Data Access Layer (Repository + JPA)
   - Configuration Layer (App Config + Hibernate)

3. **Oracle Database**
   - IORIG_NOTIFICATION Table
   - Oracle JDBC Driver

### Technology Stack
```
HTTP/REST
  ↓
Spring Web MVC
  ↓
Spring Service (@Transactional)
  ↓
Spring Data JPA
  ↓
Hibernate ORM
  ↓
Oracle JDBC Driver
  ↓
Oracle Database (CLOB LOB)
```

### Configuration
- **Hibernate Dialect:** OracleDialect
- **CLOB Handling:** non_contextual_creation: true
- **Connection Pool:** HikariCP (default Spring Boot)
- **Transaction Manager:** Spring JPA

---

## 4. 🔄 State Diagram
**File:** `EmailNotificationAPI_StateDiagram.puml`

### Purpose
Illustrates the states and transitions during request processing.

### Request States
1. **ReceiveRequest** (Entry)
2. **ParseRequest** - JSON parsing
3. **ValidateAppId** - Field validation
4. **AppIdNull** - Decision: null or empty?
5. **ConvertEntity** - DTO to entity mapping
6. **MapFields** - Field-to-column mapping
7. **CallRepository** - JPA persist
8. **DatabaseSave** - SQL execution
9. **GenerateSequence** - Auto-ID generation
10. **GenerateTimestamp** - Auto-timestamp
11. **WriteCLOB** - CLOB data handling
12. **DatabaseSuccess** - Transaction commit
13. **BuildResponse** - Response creation
14. **ReturnSuccess** - HTTP 200 OK
15. **ReturnError400** - HTTP 400 (validation)
16. **ReturnError500** - HTTP 500 (system error)

### State Transitions

**Success Path:**
```
ReceiveRequest → ParseRequest → ValidateAppId 
→ (NO appId issue) → ConvertEntity → MapFields 
→ CallRepository → DatabaseSave → GenerateSequence 
→ GenerateTimestamp → WriteCLOB → DatabaseSuccess 
→ BuildResponse → ReturnSuccess
```

**Validation Error Path:**
```
ReceiveRequest → ParseRequest → ValidateAppId 
→ (YES appId issue) → ReturnError400 → [END]
```

**Database Error Path:**
```
... → DatabaseSave → DatabaseError 
→ ReturnError500 → [END]
```

---

## 5. 🗄️ Entity Relationship Diagram
**File:** `EmailNotificationAPI_ERDiagram.puml`

### Purpose
Details the database table structure and column mappings.

### Table: IORIG_NOTIFICATION

| Column | Type | Size | Key | Notes |
|--------|------|------|-----|-------|
| INTF_SEQ_ID | NUMBER(38,0) | - | PK | Auto-generated via sequence |
| APP_SEQ_ID | NUMBER(38,0) | - | - | Mapped from appId |
| INTF_EMAIL_ID | VARCHAR2 | 100 | - | Email address |
| INTF_SSN | VARCHAR2 | 40 | - | Social Security Number |
| INTF_STAGE | VARCHAR2 | 100 | - | Mapped from appStatus |
| INTF_SUBJECT | VARCHAR2 | 255 | - | Email subject |
| INTF_CONTENT | CLOB | Unlimited | - | **Unlimited email body** |
| INTF_LANG_CODE | VARCHAR2 | 255 | - | Language code |
| INTF_CREATED_BY | VARCHAR2 | 40 | - | Creator identifier |
| INTF_CREATED_ON | TIMESTAMP(6) | - | - | Auto-timestamp |

### Key Mappings
```
Request Field          →  Database Column
appId                  →  APP_SEQ_ID
ssn                    →  INTF_SSN
appStatus              →  INTF_STAGE
emailId                →  INTF_EMAIL_ID
subject                →  INTF_SUBJECT
content                →  INTF_CONTENT (CLOB)
languageCode           →  INTF_LANG_CODE
createdBy              →  INTF_CREATED_BY
[auto]                 →  INTF_SEQ_ID (via sequence)
[auto]                 →  INTF_CREATED_ON (CURRENT_TIMESTAMP)
```

### CLOB Column Details
- **Type:** Character Large Object
- **Size:** Unlimited (> 4GB capability)
- **Stored As:** CLOB in Oracle
- **JPA Annotation:** @Lob
- **Hibernate Config:** non_contextual_creation: true
- **Use Case:** Email body with unlimited size

---

## 📐 How to Generate Images

### Option 1: Online Editor
1. Go to [PlantUML Online](http://www.plantuml.com/plantuml/uml/)
2. Copy content from .puml file
3. Paste into editor
4. Export as PNG, SVG, or PDF

### Option 2: VS Code
1. Install "PlantUML" extension
2. Open .puml file
3. Press Alt+D to preview
4. Right-click → Export as PNG/SVG

### Option 3: Command Line
```bash
# Install PlantUML
java -jar plantuml.jar -tpng *.puml

# Generate all formats
java -jar plantuml.jar -tsvg *.puml
java -jar plantuml.jar -tpdf *.puml
```

### Option 4: Docker
```bash
docker run --rm -v $(pwd):/workspace plantuml/plantuml:latest \
  -tpng /workspace/*.puml
```

---

## 📋 Quick Reference

### HTTP Status Codes
| Code | Scenario | Response |
|------|----------|----------|
| 200 | Success | EmailNotificationResponse |
| 400 | Missing appId | ErrorResponse (status: 400) |
| 500 | Database error | ErrorResponse (status: 500) |

### Request Validation
- ✅ appId is mandatory (not null, not empty)
- ✅ Returns 400 if validation fails
- ✅ No save attempt for invalid requests

### Data Persistence
- ✅ All fields saved to IORIG_NOTIFICATION
- ✅ INTF_SEQ_ID auto-generated via sequence
- ✅ INTF_CREATED_ON auto-set to CURRENT_TIMESTAMP
- ✅ INTF_CONTENT stored as CLOB (unlimited size)

### Exception Handling
- ✅ ApplicationException (400/500) → GlobalExceptionHandler
- ✅ General Exception (500) → GlobalExceptionHandler
- ✅ All errors return ErrorResponse with timestamp

---

## 🎯 Use Cases for Diagrams

### 1. Onboarding
- Help new developers understand the architecture
- Show data flow and component interactions

### 2. Documentation
- Include in API documentation
- Attach to technical specifications
- Embed in README files

### 3. Design Reviews
- Discuss architecture with stakeholders
- Identify potential bottlenecks
- Validate design decisions

### 4. Testing
- Plan test cases based on state diagram
- Validate error handling paths
- Map test scenarios to flows

### 5. Troubleshooting
- Use sequence diagram to trace issues
- Identify where errors occur
- Understand data transformations

---

## 📝 Summary

| Diagram | Type | Focus |
|---------|------|-------|
| Sequence | UML | Request flow & interactions |
| Class | UML | Object structure & relationships |
| Component | UML | Architecture & technology |
| State | UML | Request processing states |
| ER | Relational | Database schema & mapping |

**Total Coverage:** 5 diagrams covering all aspects of the Email Notification Service API from client request to database persistence.

---

## 🔗 Related Files
- `IMPLEMENTATION_SUMMARY.md` - Complete feature overview
- `QUICK_REFERENCE.md` - API usage guide
- `CLOB_IMPLEMENTATION.md` - CLOB field details
- `PUML_DIAGRAMS_GUIDE.md` - Detailed diagram guide

---

**Last Updated:** 2026-10-13  
**Version:** 1.0  
**Status:** Complete & Ready for Use
