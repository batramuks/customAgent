# Email Notification API - GET Endpoints Documentation

## Overview
This document outlines all GET endpoints for retrieving email notifications from the Email Notification Service.

---

## GET Endpoints

### 1. Get All Notifications
**Endpoint:** `GET /email/all`

**Description:** Retrieve all email notifications from the system.

**Request:**
```bash
curl -X GET "http://localhost:8080/email/all"
```

**Response (200 OK):**
```json
[
  {
    "interfaceSeqId": 1,
    "appSeqId": 123,
    "emailId": "user@example.com",
    "ssn": "123456789",
    "stage": "PENDING",
    "subject": "Email Subject",
    "content": "Email content here...",
    "languageCode": "EN",
    "createdBy": "ADMIN",
    "createdOn": "2024-10-13T13:42:07.050"
  }
]
```

**Status Codes:**
- `200 OK` - Success

---

### 2. Get Notification by ID
**Endpoint:** `GET /email/{interfaceSeqId}`

**Description:** Retrieve a specific notification by its interface sequence ID.

**Path Parameters:**
- `interfaceSeqId` (Long, required): The unique identifier of the notification

**Request:**
```bash
curl -X GET "http://localhost:8080/email/1"
```

**Response (200 OK):**
```json
{
  "interfaceSeqId": 1,
  "appSeqId": 123,
  "emailId": "user@example.com",
  "ssn": "123456789",
  "stage": "PENDING",
  "subject": "Email Subject",
  "content": "Email content here...",
  "languageCode": "EN",
  "createdBy": "ADMIN",
  "createdOn": "2024-10-13T13:42:07.050"
}
```

**Status Codes:**
- `200 OK` - Success
- `400 Bad Request` - Invalid ID format
- `404 Not Found` - Notification not found

**Error Response (404):**
```json
{
  "status": 404,
  "message": "Notification not found with ID: 999",
  "timestamp": "2024-10-13T13:42:07.050"
}
```

---

### 3. Get Notifications by Application Sequence ID
**Endpoint:** `GET /email/app/{appSeqId}`

**Description:** Retrieve all notifications for a specific application.

**Path Parameters:**
- `appSeqId` (Long, required): The application sequence ID

**Request:**
```bash
curl -X GET "http://localhost:8080/email/app/123"
```

**Response (200 OK):**
```json
[
  {
    "interfaceSeqId": 1,
    "appSeqId": 123,
    "emailId": "user@example.com",
    "ssn": "123456789",
    "stage": "PENDING",
    "subject": "Email Subject",
    "content": "Email content here...",
    "languageCode": "EN",
    "createdBy": "ADMIN",
    "createdOn": "2024-10-13T13:42:07.050"
  }
]
```

**Status Codes:**
- `200 OK` - Success
- `400 Bad Request` - Invalid app ID
- `404 Not Found` - No notifications found for app ID

---

### 4. Get Notifications by Email ID
**Endpoint:** `GET /email/search/email`

**Description:** Retrieve all notifications for a specific email address.

**Query Parameters:**
- `emailId` (String, required): The email address to search for

**Request:**
```bash
curl -X GET "http://localhost:8080/email/search/email?emailId=user@example.com"
```

**Response (200 OK):**
```json
[
  {
    "interfaceSeqId": 1,
    "appSeqId": 123,
    "emailId": "user@example.com",
    "ssn": "123456789",
    "stage": "PENDING",
    "subject": "Email Subject",
    "content": "Email content here...",
    "languageCode": "EN",
    "createdBy": "ADMIN",
    "createdOn": "2024-10-13T13:42:07.050"
  }
]
```

**Status Codes:**
- `200 OK` - Success
- `400 Bad Request` - Missing emailId parameter
- `404 Not Found` - No notifications found for email ID

---

### 5. Get Notifications by SSN
**Endpoint:** `GET /email/search/ssn`

**Description:** Retrieve all notifications for a specific SSN.

**Query Parameters:**
- `ssn` (String, required): The social security number to search for

**Request:**
```bash
curl -X GET "http://localhost:8080/email/search/ssn?ssn=123456789"
```

**Response (200 OK):**
```json
[
  {
    "interfaceSeqId": 1,
    "appSeqId": 123,
    "emailId": "user@example.com",
    "ssn": "123456789",
    "stage": "PENDING",
    "subject": "Email Subject",
    "content": "Email content here...",
    "languageCode": "EN",
    "createdBy": "ADMIN",
    "createdOn": "2024-10-13T13:42:07.050"
  }
]
```

**Status Codes:**
- `200 OK` - Success
- `400 Bad Request` - Missing ssn parameter
- `404 Not Found` - No notifications found for SSN

---

### 6. Get Notifications by Stage/Status
**Endpoint:** `GET /email/search/stage`

**Description:** Retrieve all notifications with a specific stage/status.

**Query Parameters:**
- `stage` (String, required): The stage/status to search for (e.g., PENDING, COMPLETED, FAILED)

**Request:**
```bash
curl -X GET "http://localhost:8080/email/search/stage?stage=PENDING"
```

**Response (200 OK):**
```json
[
  {
    "interfaceSeqId": 1,
    "appSeqId": 123,
    "emailId": "user@example.com",
    "ssn": "123456789",
    "stage": "PENDING",
    "subject": "Email Subject",
    "content": "Email content here...",
    "languageCode": "EN",
    "createdBy": "ADMIN",
    "createdOn": "2024-10-13T13:42:07.050"
  }
]
```

**Status Codes:**
- `200 OK` - Success
- `400 Bad Request` - Missing stage parameter
- `404 Not Found` - No notifications found for stage

---

### 7. Get Notifications by Application ID and Stage
**Endpoint:** `GET /email/search/app-stage`

**Description:** Retrieve notifications matching both application ID and stage criteria.

**Query Parameters:**
- `appSeqId` (Long, required): The application sequence ID
- `stage` (String, required): The stage/status to search for

**Request:**
```bash
curl -X GET "http://localhost:8080/email/search/app-stage?appSeqId=123&stage=PENDING"
```

**Response (200 OK):**
```json
[
  {
    "interfaceSeqId": 1,
    "appSeqId": 123,
    "emailId": "user@example.com",
    "ssn": "123456789",
    "stage": "PENDING",
    "subject": "Email Subject",
    "content": "Email content here...",
    "languageCode": "EN",
    "createdBy": "ADMIN",
    "createdOn": "2024-10-13T13:42:07.050"
  }
]
```

**Status Codes:**
- `200 OK` - Success
- `400 Bad Request` - Missing required parameters
- `404 Not Found` - No notifications found matching criteria

---

## Complete API Summary

| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|-----------|
| GET | `/email/all` | Get all notifications | None |
| GET | `/email/{interfaceSeqId}` | Get by notification ID | Path: interfaceSeqId |
| GET | `/email/app/{appSeqId}` | Get by app ID | Path: appSeqId |
| GET | `/email/search/email` | Get by email address | Query: emailId |
| GET | `/email/search/ssn` | Get by SSN | Query: ssn |
| GET | `/email/search/stage` | Get by stage/status | Query: stage |
| GET | `/email/search/app-stage` | Get by app ID + stage | Query: appSeqId, stage |
| POST | `/email/save` | Save new notification | Body: ApplicationEmailRequest |

---

## Common Error Responses

### 400 Bad Request
```json
{
  "status": 400,
  "message": "Invalid interface sequence ID",
  "timestamp": "2024-10-13T13:42:07.050"
}
```

### 404 Not Found
```json
{
  "status": 404,
  "message": "No notifications found for app ID: 999",
  "timestamp": "2024-10-13T13:42:07.050"
}
```

### 500 Internal Server Error
```json
{
  "status": 500,
  "message": "Internal server error: Database connection failed",
  "timestamp": "2024-10-13T13:42:07.050"
}
```

---

## Usage Examples

### Example 1: Get all notifications
```bash
curl -X GET "http://localhost:8080/email/all" \
  -H "Accept: application/json"
```

### Example 2: Get specific notification
```bash
curl -X GET "http://localhost:8080/email/1" \
  -H "Accept: application/json"
```

### Example 3: Get notifications for an application
```bash
curl -X GET "http://localhost:8080/email/app/123" \
  -H "Accept: application/json"
```

### Example 4: Search notifications by email
```bash
curl -X GET "http://localhost:8080/email/search/email?emailId=user@example.com" \
  -H "Accept: application/json"
```

### Example 5: Search notifications by stage
```bash
curl -X GET "http://localhost:8080/email/search/stage?stage=PENDING" \
  -H "Accept: application/json"
```

### Example 6: Search by app ID and stage
```bash
curl -X GET "http://localhost:8080/email/search/app-stage?appSeqId=123&stage=COMPLETED" \
  -H "Accept: application/json"
```

---

## Notes

- All endpoints use `application/json` content type
- All timestamps are in ISO 8601 format
- Content field may contain large CLOB data (up to 4GB in Oracle)
- All search endpoints are read-only (no data modification)
- Proper error handling with appropriate HTTP status codes
- All endpoints support CORS if configured
