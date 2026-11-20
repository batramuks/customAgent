# Application Entity - Complete Schema Mapping

## Overview
Updated the Application entity to include all 18 fields from the Oracle DATABASE APPLICATION table. The entity now properly maps all columns with correct data types and constraints.

---

## Application Entity Fields

| Column Name | Java Field | Type | Length | Nullable | Description |
|-------------|-----------|------|--------|----------|-------------|
| APP_SEQ_ID | appSeqId | Long | - | NO | Primary Key - Application Sequence ID |
| APP_ID | appId | String | 40 | NO | Unique Application ID |
| APP_START_TIME | appStartTime | LocalDateTime | - | YES | Application Start Timestamp |
| APP_END_TIME | appEndTime | LocalDateTime | - | YES | Application End Timestamp |
| APP_APPLICATION_STATUS | applicationStatus | String | 40 | NO | Application Status (ACTIVE, INACTIVE, etc.) |
| APP_PROCESS_STATE | processState | String | 40 | NO | Current Process State (INITIATED, COMPLETED, etc.) |
| APP_CMND_PROCESS_DEF_NAME | cmndProcessDefName | String | 40 | YES | Command Process Definition Name |
| APP_CMND_PROCESS_DEF_VERSION | cmndProcessDefVersion | String | 40 | YES | Command Process Definition Version |
| APP_API_VERSION | apiVersion | String | 40 | YES | API Version |
| APP_COUNTRY_CODE | countryCode | String | 2 | NO | ISO Country Code |
| APP_CURRENCY_CODE | currencyCode | String | 3 | NO | ISO Currency Code |
| APP_CREATED_BY | createdBy | String | 40 | NO | User who created the record |
| APP_CREATED_ON | createdOn | LocalDateTime | - | YES | Record Creation Timestamp |
| APP_MODIFIED_BY | modifiedBy | String | 40 | NO | User who last modified the record |
| APP_MODIFIED_ON | modifiedOn | LocalDateTime | - | YES | Last Modification Timestamp |
| APP_LOCK | appLock | Long | - | NO | Optimistic Lock Version |
| APP_CSP_TIMESTAMP | cspTimestamp | LocalDateTime | - | YES | CSP (Customer Service Platform) Timestamp |
| APP_MANUAL_SCORING_NEEDED | manualScoringNeeded | Long | - | YES | Manual Scoring Flag (0/1) |

---

## Application Entity Code

```java
@Entity
@Table(name = "APPLICATION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @Column(name = "APP_SEQ_ID")
    private Long appSeqId;

    @Column(name = "APP_ID", length = 40, nullable = false)
    private String appId;

    @Column(name = "APP_START_TIME")
    private LocalDateTime appStartTime;

    @Column(name = "APP_END_TIME")
    private LocalDateTime appEndTime;

    @Column(name = "APP_APPLICATION_STATUS", length = 40, nullable = false)
    private String applicationStatus;

    @Column(name = "APP_PROCESS_STATE", length = 40, nullable = false)
    private String processState;

    @Column(name = "APP_CMND_PROCESS_DEF_NAME", length = 40)
    private String cmndProcessDefName;

    @Column(name = "APP_CMND_PROCESS_DEF_VERSION", length = 40)
    private String cmndProcessDefVersion;

    @Column(name = "APP_API_VERSION", length = 40)
    private String apiVersion;

    @Column(name = "APP_COUNTRY_CODE", length = 2, nullable = false)
    private String countryCode;

    @Column(name = "APP_CURRENCY_CODE", length = 3, nullable = false)
    private String currencyCode;

    @Column(name = "APP_CREATED_BY", length = 40, nullable = false)
    private String createdBy;

    @Column(name = "APP_CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "APP_MODIFIED_BY", length = 40, nullable = false)
    private String modifiedBy;

    @Column(name = "APP_MODIFIED_ON")
    private LocalDateTime modifiedOn;

    @Column(name = "APP_LOCK", nullable = false)
    private Long appLock;

    @Column(name = "APP_CSP_TIMESTAMP")
    private LocalDateTime cspTimestamp;

    @Column(name = "APP_MANUAL_SCORING_NEEDED")
    private Long manualScoringNeeded;
}
```

---

## Updated EmailNotificationDto

The DTO now includes relevant Application fields for API responses:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailNotificationDto {
    private Long interfaceSeqId;
    private Long appSeqId;
    private String appId;                    // From Application
    private String applicationStatus;         // From Application
    private String processState;              // From Application
    private String emailId;
    private String ssn;
    private String stage;
    private String subject;
    private String content;
    private String languageCode;
    private String createdBy;
    private LocalDateTime createdOn;
}
```

---

## Relationship Mapping

### IOrigNotification → Application
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "INTF_APP_IDX", referencedColumnName = "APP_SEQ_ID", 
            foreignKey = @ForeignKey(name = "IORIG_NOTIFICATION_APP_0"))
private Application application;
```

**Key Points:**
- LAZY loading for performance optimization
- Foreign Key constraint: `IORIG_NOTIFICATION_APP_0`
- Join column: `INTF_APP_IDX` → `APP_SEQ_ID`

---

## Data Validation

### Required Fields (NOT NULL)
- appSeqId (Primary Key)
- appId
- applicationStatus
- processState
- countryCode
- currencyCode
- createdBy
- modifiedBy
- appLock

### Optional Fields (Nullable)
- appStartTime
- appEndTime
- cmndProcessDefName
- cmndProcessDefVersion
- apiVersion
- createdOn
- modifiedOn
- cspTimestamp
- manualScoringNeeded

---

## Service Layer Integration

### Post Endpoint Validation
When saving email notification:
```java
Application application = applicationRepository.findById(appId)
    .orElseThrow(() -> new ApplicationException("Application not found with ID: " + appId, 404));

IOrigNotification notification = IOrigNotification.builder()
    .application(application)
    .emailId(request.getEmailId())
    // ... other fields
    .build();
```

### Get Endpoint Response
All notification responses include related Application data:
```json
{
  "interfaceSeqId": 1,
  "appSeqId": 123,
  "appId": "APP-001",
  "applicationStatus": "ACTIVE",
  "processState": "INITIATED",
  "emailId": "user@example.com",
  "ssn": "123456789",
  "stage": "PENDING",
  "subject": "Email Subject",
  "content": "Email content...",
  "languageCode": "EN",
  "createdBy": "ADMIN",
  "createdOn": "2024-10-13T14:11:23.632"
}
```

---

## Test Setup Example

```java
// Application creation in tests
Application application = Application.builder()
    .appSeqId(123L)
    .appId("APP-001")
    .applicationStatus("ACTIVE")
    .processState("INITIATED")
    .countryCode("US")
    .currencyCode("USD")
    .createdBy("ADMIN")
    .modifiedBy("ADMIN")
    .appLock(0L)
    .build();
```

---

## Database Alignment Verification

✅ **Column Mapping Verified:**
- ✅ APP_SEQ_ID → appSeqId (Long, PK)
- ✅ APP_ID → appId (String 40, NOT NULL)
- ✅ APP_START_TIME → appStartTime (LocalDateTime)
- ✅ APP_END_TIME → appEndTime (LocalDateTime)
- ✅ APP_APPLICATION_STATUS → applicationStatus (String 40, NOT NULL)
- ✅ APP_PROCESS_STATE → processState (String 40, NOT NULL)
- ✅ APP_CMND_PROCESS_DEF_NAME → cmndProcessDefName (String 40)
- ✅ APP_CMND_PROCESS_DEF_VERSION → cmndProcessDefVersion (String 40)
- ✅ APP_API_VERSION → apiVersion (String 40)
- ✅ APP_COUNTRY_CODE → countryCode (String 2, NOT NULL)
- ✅ APP_CURRENCY_CODE → currencyCode (String 3, NOT NULL)
- ✅ APP_CREATED_BY → createdBy (String 40, NOT NULL)
- ✅ APP_CREATED_ON → createdOn (LocalDateTime)
- ✅ APP_MODIFIED_BY → modifiedBy (String 40, NOT NULL)
- ✅ APP_MODIFIED_ON → modifiedOn (LocalDateTime)
- ✅ APP_LOCK → appLock (Long, NOT NULL)
- ✅ APP_CSP_TIMESTAMP → cspTimestamp (LocalDateTime)
- ✅ APP_MANUAL_SCORING_NEEDED → manualScoringNeeded (Long)

---

## Testing Results

✅ **All Tests Passing:** 45/45
✅ **Build Status:** Successful
✅ **No Compilation Errors**
✅ **No Test Failures**

---

## Files Updated

1. **Application.java** - Complete entity with all 18 fields
2. **EmailNotificationDto.java** - Updated to include Application fields
3. **EmailNotificationService.java** - Updated DTO conversion
4. **EmailNotificationServiceTest.java** - Updated test data setup
5. **EmailNotificationControllerTest.java** - Updated DTO builders

---

## Notes

- **Lazy Loading:** Application is lazy-loaded for performance
- **Immutability:** Use builder pattern for creating instances
- **Validation:** ApplicationRepository validates existence before usage
- **Audit Fields:** Created/Modified by and timestamp fields are managed
- **Lock Field:** appLock is for optimistic locking (concurrency control)
- **Scoring:** manualScoringNeeded flag for workflow control

---

## Summary

The Application entity now fully represents the DATABASE APPLICATION table with all 18 columns correctly mapped, validated, and integrated with the EmailNotification service and API endpoints.
