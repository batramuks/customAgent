# Email Notification Service - Quick Reference

## Endpoint Details

### POST /email/save
Saves an email notification to the IORIG_NOTIFICATION database table.

## Success Response (200 OK)
```json
{
  "interfaceSeqId": 1,
  "message": "Email notification saved successfully",
  "status": "SUCCESS"
}
```

## Error Response (400 / 500)
```json
{
  "status": 400,
  "message": "Application ID is required",
  "timestamp": "2024-10-13T12:43:44.123456"
}
```

## Test Results
```
Total Tests: 11
Passed: 11 ✅
Failed: 0
Coverage: Service & Controller layers
```

## Build Status
```
Status: SUCCESS ✅
JAR Package: target/TESTAgent-1.0-SNAPSHOT.jar
```

## Running the Service

### Start the application:
```bash
cd c:\git-code\DEMAND-CCD2\TESTAgent
mvn spring-boot:run
```

### Test with cURL:
```bash
# Success case
curl -X POST http://localhost:8080/email/save \
  -H "Content-Type: application/json" \
  -d '{
    "appId": "123",
    "ssn": "123456789",
    "appStatus": "PENDING",
    "emailId": "user@example.com",
    "subject": "Welcome Email",
    "content": "Welcome to our platform",
    "createdBy": "SYSTEM",
    "languageCode": "EN"
  }'

# Error case - missing appId
curl -X POST http://localhost:8080/email/save \
  -H "Content-Type: application/json" \
  -d '{
    "appId": "",
    "ssn": "123456789"
  }'
```

## Database Connection
- **URL**: jdbc:oracle:thin:@tvdclorigdb01.baf.ikano:1521:IORIGD01
- **User**: IORIGTEST_OWNER
- **Password**: IORIGTEST_OWNER
- **Table**: IORIG_NOTIFICATION

## Validation Rules
- ✅ appId is mandatory (must not be null or empty)
- ✅ Returns 400 BAD REQUEST if appId validation fails
- ✅ Returns 500 INTERNAL_SERVER_ERROR on database errors
- ✅ Returns 200 OK on successful save

## Field Mapping
| Request Field | Database Column | Type |
|---|---|---|
| appId | APP_SEQ_ID | NUMBER |
| ssn | INTF_SSN | VARCHAR2(40) |
| appStatus | INTF_STAGE | VARCHAR2(100) |
| emailId | INTF_EMAIL_ID | VARCHAR2(100) |
| subject | INTF_SUBJECT | VARCHAR2(255) |
| content | INTF_CONTENT | VARCHAR2(4000) |
| createdBy | INTF_CREATED_BY | VARCHAR2(40) |
| languageCode | INTF_LANG_CODE | VARCHAR2(255) |
| - | INTF_CREATED_ON | TIMESTAMP (auto) |
| - | INTF_SEQ_ID | NUMBER (auto) |

## Technologies Used
- Spring Boot 3.1.5
- Spring Data JPA
- Oracle JDBC Driver 11
- JUnit 5
- Mockito
- Lombok
- Maven 3.x
- Java 17

## File Structure
```
src/main/
├── java/org/example/
│   ├── Application.java
│   ├── controller/EmailNotificationController.java
│   ├── service/EmailNotificationService.java
│   ├── repository/IOrigNotificationRepository.java
│   ├── entity/IOrigNotification.java
│   ├── dto/ApplicationEmailRequest.java
│   ├── dto/EmailNotificationResponse.java
│   └── exception/(ApplicationException, ErrorResponse, GlobalExceptionHandler)
└── resources/application.yml

src/test/
├── service/EmailNotificationServiceTest.java (6 tests)
└── controller/EmailNotificationControllerTest.java (5 tests)
```

## Next Steps for Production
1. Configure actual Oracle database credentials in application.yml
2. Add additional validation as needed (email format, SSN format, etc.)
3. Add request/response logging
4. Add metrics/monitoring
5. Add authentication/authorization
6. Deploy to production environment
