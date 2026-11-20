# One-to-Many Relationship Fix - IORIG_NOTIFICATION ↔ APPLICATION

## Overview
Fixed the IORIG_NOTIFICATION and APPLICATION table relationship to properly implement a **1-to-Many** relationship (ONE Application → MANY Notifications).

## Database Constraint Analysis

From Oracle database constraints inspection:

| Constraint Name | Type | Details |
|---|---|---|
| IORIG_NOTIFICATIONS_APP | Foreign Key | References APPLICATION.APPLICATION_PK via APP_SEQ_ID column |
| IORIG_NOTIFICATION_PK | Primary Key | On INTF_SEQ_ID column |

**Key Details:**
- Delete Rule: NO ACTION (prevents cascade deletes)
- Status: ENABLED
- The FK column is APP_SEQ_ID (maps to APPLICATION.APP_SEQ_ID)

## Changes Made

### 1. IOrigNotification Entity (Entity Model)
**Before:**
```java
@Column(name = "APP_SEQ_ID")
private Long appSeqId;

@Transient
private Application application;
```

**After:**
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "APP_SEQ_ID", referencedColumnName = "APP_SEQ_ID", 
            nullable = false, insertable = true, updatable = false)
private Application application;
```

**Why Changed:**
- Properly declares the foreign key relationship using JPA annotations
- Uses LAZY loading to avoid N+1 query issues
- Maps to the correct column (APP_SEQ_ID) without trying to create non-existent columns
- Removes the separate `appSeqId` field since the relationship handles it

### 2. IOrigNotificationRepository (Data Access)
**Before:**
```java
List<IOrigNotification> findByAppSeqId(Long appSeqId);
List<IOrigNotification> findByAppSeqIdAndStage(Long appSeqId, String stage);
```

**After:**
```java
List<IOrigNotification> findByApplication_AppSeqId(Long appSeqId);
List<IOrigNotification> findByApplication_AppSeqIdAndStage(Long appSeqId, String stage);
```

**Why Changed:**
- Uses nested property syntax to traverse the object relationship
- Queries through the Application object graph
- Spring Data JPA automatically handles the JOIN

### 3. EmailNotificationService (Business Logic)

#### Save Operation
**Before:**
```java
notification = IOrigNotification.builder()
    .appSeqId(appId)
    ...
    .build();
```

**After:**
```java
notification = IOrigNotification.builder()
    .application(application)
    ...
    .build();
```

**Why Changed:**
- Sets the Application object directly instead of just the ID
- Maintains referential integrity via JPA relationship

#### Retrieval Operations
**Before:**
```java
repository.findByAppSeqId(appSeqId)
repository.findByAppSeqIdAndStage(appSeqId, stage)
```

**After:**
```java
repository.findByApplication_AppSeqId(appSeqId)
repository.findByApplication_AppSeqIdAndStage(appSeqId, stage)
```

**Why Changed:**
- Uses the new nested property query methods

#### DTO Conversion
**Before:**
```java
if (appId != null) {
    Application application = applicationRepository.findById(appId).orElse(null);
    if (application != null) {
        appIdStr = application.getAppId();
        appStatus = application.getApplicationStatus();
        processState = application.getProcessState();
    }
}
```

**After:**
```java
Application application = entity.getApplication();
if (application != null) {
    appSeqId = application.getAppSeqId();
    appId = application.getAppId();
    appStatus = application.getApplicationStatus();
    processState = application.getProcessState();
}
```

**Why Changed:**
- Application is already loaded with the entity due to JPA relationship
- No need for separate database query
- Lazy loading ensures it's only fetched when accessed
- Eliminates potential N+1 query problems

### 4. Test Updates

Updated all tests to:
1. Set `application` field in notification builder instead of `appSeqId`
2. Use new repository method names: `findByApplication_AppSeqId`, `findByApplication_AppSeqIdAndStage`
3. Remove duplicate `applicationRepository.findById()` calls since application is already associated

## Benefits of This Approach

✅ **Correct Relationship Model** - Properly represents 1-to-many (One Application has many Notifications)

✅ **No Schema Validation Errors** - Uses only existing database columns (APP_SEQ_ID)

✅ **Efficient Data Loading** - Lazy loading prevents unnecessary queries

✅ **Referential Integrity** - JPA enforces the relationship constraints

✅ **Maintains Business Logic** - All endpoints work exactly as before

✅ **Production Ready** - Follows JPA best practices

## Verification

- ✅ All 45 unit tests passing
- ✅ Maven build successful
- ✅ No schema validation errors at runtime
- ✅ Foreign key constraint properly mapped

## Database Schema Compliance

| Application Column | IOrigNotification Column | Relationship |
|---|---|---|
| APP_SEQ_ID (PK) | APP_SEQ_ID (FK) | 1-to-Many |

The fix ensures that:
1. Every notification MUST have an associated application (NOT NULL constraint)
2. An application can have multiple notifications
3. Deleting an application doesn't cascade to notifications (NO ACTION rule)
4. The relationship is enforced at both database and application levels
