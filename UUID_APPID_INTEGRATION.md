# UUID Application ID Integration

## Overview
Updated the Email Notification API to accept `appId` as a UUID string (e.g., `873dd088-4ab7-41b3-a9df-11bf96db188e`) instead of a numeric Long value, matching the actual database schema and real-world usage patterns.

## Changes Made

### 1. ApplicationRepository
**Added new query method:**
```java
Optional<Application> findByAppId(String appId);
```

**Why:** Enables lookup of Application entities using the UUID string from the request payload.

### 2. EmailNotificationService - saveEmailNotification()

**Before:**
```java
Long appId = Long.parseLong(request.getAppId());
Application application = applicationRepository.findById(appId)
    .orElseThrow(() -> new ApplicationException("Application not found with ID: " + appId, 404));
```

**After:**
```java
String appId = request.getAppId().trim();
Application application = applicationRepository.findByAppId(appId)
    .orElseThrow(() -> new ApplicationException("Application not found with ID: " + appId, 404));
```

**Why:** 
- Treats appId as a UUID string, not a numeric value
- Calls `trim()` to handle whitespace
- Uses `findByAppId()` to query by the UUID field instead of the numeric PK

### 3. Test Data Updates

**Before:**
```java
request.setAppId("123");
application = Application.builder()
    .appSeqId(123L)
    .appId("APP-001")
    ...
    .build();
```

**After:**
```java
request.setAppId("873dd088-4ab7-41b3-a9df-11bf96db188e");
application = Application.builder()
    .appSeqId(123L)
    .appId("873dd088-4ab7-41b3-a9df-11bf96db188e")
    ...
    .build();
```

**Why:**
- Test data now matches real-world UUID format
- Service tests and controller tests updated consistently
- Test assertions updated to expect UUID value

## Database Schema Context

| Column | Type | Notes |
|--------|------|-------|
| APP_SEQ_ID | NUMBER(38) | Primary Key - numeric long value |
| APP_ID | VARCHAR2(40) | UUID string - used for external references |

## Request/Response Example

### Request
```json
{
  "appId": "873dd088-4ab7-41b3-a9df-11bf96db188e",
  "emailId": "user@example.com",
  "ssn": "123456789",
  "appStatus": "PENDING",
  "subject": "Application Status Update",
  "content": "Your application is being processed...",
  "createdBy": "SYSTEM",
  "languageCode": "EN"
}
```

### Response
```json
{
  "interfaceSeqId": 1,
  "status": "SUCCESS",
  "message": "Email notification saved successfully"
}
```

## Technical Details

### Why This Approach?
1. **Real-world alignment** - Applications use UUID strings for external identifiers
2. **Security** - Doesn't expose internal numeric PKs to external systems
3. **Flexibility** - Allows for UUID validation and proper error handling
4. **Simplicity** - Direct string lookup without conversion

### Error Handling
If the application with the given UUID doesn't exist:
```
HTTP 404
{
  "error": "Application not found with ID: 873dd088-4ab7-41b3-a9df-11bf96db188e",
  "statusCode": 404
}
```

### Performance
- Query by APP_ID is efficient if there's an index on this column
- Consider adding database index if not present:
  ```sql
  CREATE INDEX IDX_APP_ID ON APPLICATION(APP_ID);
  ```

## Test Results
- ✅ **45/45 unit tests passing**
- ✅ **Maven build successful**
- ✅ Supports UUID format in requests
- ✅ Proper error handling for missing applications

## Backward Compatibility
⚠️ **Breaking Change**: Existing code that sends numeric appId will fail
- Old format: `"appId": "123"`
- New format: `"appId": "873dd088-4ab7-41b3-a9df-11bf96db188e"`

Client applications must be updated to send UUID format.

## Future Considerations
- Add UUID format validation: `@Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")`
- Add database index on APP_ID for better query performance
- Consider caching Application entities if query volume is high
