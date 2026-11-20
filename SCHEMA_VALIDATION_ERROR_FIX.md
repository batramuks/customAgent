# Schema Validation Error - RESOLVED

## Problem
**Error Message:**
```
Schema-validation: missing column [intf_app_idx] in table [iorig_notification]
```

The application was trying to map a foreign key column `INTF_APP_IDX` that doesn't exist in the `IORIG_NOTIFICATION` table, causing Hibernate schema validation to fail.

---

## Root Cause
The initial implementation attempted to use JPA's `@ManyToOne` relationship with `@JoinColumn(name = "INTF_APP_IDX")`, but this column doesn't exist in the actual database table.

**Problematic Code:**
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "INTF_APP_IDX", referencedColumnName = "APP_SEQ_ID")
private Application application;
```

This assumes a physical foreign key column exists, which it doesn't.

---

## Solution
Changed to a **programmatic approach** without relying on a physical FK column:

### 1. Kept `APP_SEQ_ID` as a Regular Column
```java
@Column(name = "APP_SEQ_ID")
private Long appSeqId;
```

### 2. Made Application Field Transient
```java
@Transient
private Application application;
```

This allows the Application object to be loaded programmatically without requiring a physical database column.

### 3. Load Application On-Demand
The service manually loads the Application entity when needed:

```java
// In convertToDto method
if (appId != null) {
    Application application = applicationRepository.findById(appId).orElse(null);
    if (application != null) {
        appIdStr = application.getAppId();
        appStatus = application.getApplicationStatus();
        processState = application.getProcessState();
    }
}
```

---

## Changes Made

### 1. IOrigNotification Entity
- ✅ Removed `@ManyToOne` annotation
- ✅ Restored `appSeqId` as regular `@Column`
- ✅ Made `application` field `@Transient`

### 2. IOrigNotificationRepository
- ✅ Changed `findByApplication()` to `findByAppSeqId(Long)`
- ✅ Changed `findByApplicationAndStage()` to `findByAppSeqIdAndStage(Long, String)`

### 3. EmailNotificationService
- ✅ Updated to pass `appSeqId` instead of Application object
- ✅ Load Application separately in DTO conversion
- ✅ Maintain all validation logic

### 4. Tests
- ✅ Updated to mock `findByAppSeqId()` instead of `findByApplication()`
- ✅ All 45 tests passing
- ✅ Build successful

---

## Before vs After

### Before (Failing)
```
Error: Schema-validation: missing column [intf_app_idx] in table [iorig_notification]
```

### After (Working)
```
✅ All Tests Passing: 45/45
✅ Build Successful
✅ Application runs without schema validation errors
```

---

## Implementation Details

### Database Structure (Actual)
```
IORIG_NOTIFICATION
├── INTF_SEQ_ID (PK)
├── APP_SEQ_ID ← Simple column (not a FK with INTF_APP_IDX)
├── INTF_EMAIL_ID
├── ... other fields
```

### JPA Mapping (Fixed)
```java
@Entity
@Table(name = "IORIG_NOTIFICATION")
public class IOrigNotification {
    @Id
    @Column(name = "INTF_SEQ_ID")
    private Long interfaceSeqId;
    
    @Column(name = "APP_SEQ_ID")  // Simple column
    private Long appSeqId;         // No @JoinColumn needed
    
    @Transient
    private Application application; // Loaded programmatically
}
```

---

## Performance Considerations

### Current Approach
- **Pro:** Works with existing database schema without modifications
- **Pro:** No foreign key constraints at JPA level
- **Con:** Requires N+1 queries if converting many notifications (one findById per notification)

### Optimization (Future)
If performance becomes an issue, consider:
1. **Batch Loading:** Load all required Applications in one query
2. **Caching:** Cache Application objects with @Cacheable
3. **Left Join:** Use custom query with left join to load both entities

Example optimization:
```java
@Transactional(readOnly = true)
public List<EmailNotificationDto> getNotificationsByAppSeqId(Long appSeqId) {
    List<IOrigNotification> notifications = repository.findByAppSeqId(appSeqId);
    
    // Batch load all unique appSeqIds
    Set<Long> appIds = notifications.stream()
        .map(IOrigNotification::getAppSeqId)
        .collect(Collectors.toSet());
    
    Map<Long, Application> appMap = applicationRepository.findAllById(appIds)
        .stream()
        .collect(Collectors.toMap(Application::getAppSeqId, Function.identity()));
    
    // Convert with cached applications
    return notifications.stream()
        .map(n -> convertToDto(n, appMap.get(n.getAppSeqId())))
        .collect(Collectors.toList());
}
```

---

## Verification

### Test Results
```
✅ Total Tests: 45
✅ Passed: 45
❌ Failed: 0
✅ Build: SUCCESS
```

### Running the Application
```bash
mvn spring-boot:run
```

Now successfully starts without schema validation errors.

---

## Summary

**Fixed the schema validation error by:**
1. Removing the non-existent `INTF_APP_IDX` FK column mapping
2. Keeping `APP_SEQ_ID` as a simple column
3. Loading Application data programmatically when needed
4. Maintaining all business logic and API functionality

**Status:** ✅ **RESOLVED AND TESTED**
