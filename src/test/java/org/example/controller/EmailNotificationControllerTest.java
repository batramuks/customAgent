package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.ApplicationEmailRequest;
import org.example.dto.EmailNotificationDto;
import org.example.dto.EmailNotificationResponse;
import org.example.exception.ApplicationException;
import org.example.service.EmailNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailNotificationControllerTest {

    @Mock
    private EmailNotificationService service;

    @InjectMocks
    private EmailNotificationController controller;

    private ApplicationEmailRequest request;
    private EmailNotificationDto notificationDto;

    @BeforeEach
    void setUp() {
        request = new ApplicationEmailRequest();
        request.setAppId("873dd088-4ab7-41b3-a9df-11bf96db188e");
        request.setSsn("123456789");
        request.setAppStatus("PENDING");
        request.setEmailId("test@example.com");
        request.setSubject("Test Subject");
        request.setContent("Test Content");
        request.setCreatedBy("ADMIN");
        request.setLanguageCode("EN");

        notificationDto = EmailNotificationDto.builder()
                .interfaceSeqId(1L)
                .appSeqId(123L)
                .appId("873dd088-4ab7-41b3-a9df-11bf96db188e")
                .applicationStatus("ACTIVE")
                .processState("INITIATED")
                .emailId("test@example.com")
                .ssn("123456789")
                .stage("PENDING")
                .subject("Test Subject")
                .content("Test Content")
                .languageCode("EN")
                .createdBy("ADMIN")
                .createdOn(LocalDateTime.now())
                .build();
    }

    // POST Tests
    @Test
    void testSaveEmailNotificationSuccess() {
        // Arrange
        EmailNotificationResponse response = EmailNotificationResponse.builder()
                .interfaceSeqId(1L)
                .message("Email notification saved successfully")
                .status("SUCCESS")
                .build();

        when(service.saveEmailNotification(any(ApplicationEmailRequest.class))).thenReturn(response);

        // Act
        ResponseEntity<EmailNotificationResponse> result = controller.saveEmailNotification(request);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().getInterfaceSeqId());
        assertEquals("SUCCESS", result.getBody().getStatus());
        assertEquals("Email notification saved successfully", result.getBody().getMessage());
    }

    @Test
    void testSaveEmailNotificationMissingAppId() {
        // Arrange
        request.setAppId(null);
        when(service.saveEmailNotification(any(ApplicationEmailRequest.class)))
                .thenThrow(new ApplicationException("Application ID is required", 400));

        // Act & Assert
        assertThrows(ApplicationException.class, () -> {
            controller.saveEmailNotification(request);
        });
    }

    @Test
    void testSaveEmailNotificationInternalError() {
        // Arrange
        when(service.saveEmailNotification(any(ApplicationEmailRequest.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            controller.saveEmailNotification(request);
        });
    }

    @Test
    void testSaveEmailNotificationEmptyAppId() {
        // Arrange
        request.setAppId("");
        when(service.saveEmailNotification(any(ApplicationEmailRequest.class)))
                .thenThrow(new ApplicationException("Application ID is required", 400));

        // Act & Assert
        assertThrows(ApplicationException.class, () -> {
            controller.saveEmailNotification(request);
        });
    }

    @Test
    void testSaveEmailNotificationWithAllFields() {
        // Arrange
        EmailNotificationResponse response = EmailNotificationResponse.builder()
                .interfaceSeqId(1L)
                .message("Email notification saved successfully")
                .status("SUCCESS")
                .build();

        when(service.saveEmailNotification(any(ApplicationEmailRequest.class))).thenReturn(response);

        // Act
        ResponseEntity<EmailNotificationResponse> result = controller.saveEmailNotification(request);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().getInterfaceSeqId());
        assertEquals("SUCCESS", result.getBody().getStatus());
    }

    // GET Tests
    @Test
    void testGetAllNotificationsSuccess() {
        // Arrange
        List<EmailNotificationDto> notifications = Arrays.asList(notificationDto);
        when(service.getAllNotifications()).thenReturn(notifications);

        // Act
        ResponseEntity<List<EmailNotificationDto>> result = controller.getAllNotifications();

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals(1L, result.getBody().get(0).getInterfaceSeqId());
        assertEquals("test@example.com", result.getBody().get(0).getEmailId());
    }

    @Test
    void testGetNotificationByIdSuccess() {
        // Arrange
        when(service.getNotificationById(anyLong())).thenReturn(notificationDto);

        // Act
        ResponseEntity<EmailNotificationDto> result = controller.getNotificationById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().getInterfaceSeqId());
        assertEquals("test@example.com", result.getBody().getEmailId());
    }

    @Test
    void testGetNotificationByIdNotFound() {
        // Arrange
        when(service.getNotificationById(anyLong()))
                .thenThrow(new ApplicationException("Notification not found with ID: 999", 404));

        // Act & Assert
        assertThrows(ApplicationException.class, () -> {
            controller.getNotificationById(999L);
        });
    }

    @Test
    void testGetNotificationByIdInvalidId() {
        // Arrange
        when(service.getNotificationById(anyLong()))
                .thenThrow(new ApplicationException("Invalid interface sequence ID", 400));

        // Act & Assert
        assertThrows(ApplicationException.class, () -> {
            controller.getNotificationById(-1L);
        });
    }

    @Test
    void testGetNotificationsByAppSeqIdSuccess() {
        // Arrange
        List<EmailNotificationDto> notifications = Arrays.asList(notificationDto);
        when(service.getNotificationsByAppSeqId(anyLong())).thenReturn(notifications);

        // Act
        ResponseEntity<List<EmailNotificationDto>> result = controller.getNotificationsByAppSeqId(123L);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals(123L, result.getBody().get(0).getAppSeqId());
    }

    @Test
    void testGetNotificationsByAppSeqIdNotFound() {
        // Arrange
        when(service.getNotificationsByAppSeqId(anyLong()))
                .thenThrow(new ApplicationException("No notifications found for app ID: 999", 404));

        // Act & Assert
        assertThrows(ApplicationException.class, () -> {
            controller.getNotificationsByAppSeqId(999L);
        });
    }

    @Test
    void testGetNotificationsByEmailIdSuccess() {
        // Arrange
        List<EmailNotificationDto> notifications = Arrays.asList(notificationDto);
        when(service.getNotificationsByEmailId(anyString())).thenReturn(notifications);

        // Act
        ResponseEntity<List<EmailNotificationDto>> result = controller.getNotificationsByEmailId("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals("test@example.com", result.getBody().get(0).getEmailId());
    }

    @Test
    void testGetNotificationsByEmailIdNotFound() {
        // Arrange
        when(service.getNotificationsByEmailId(anyString()))
                .thenThrow(new ApplicationException("No notifications found for email: invalid@example.com", 404));

        // Act & Assert
        assertThrows(ApplicationException.class, () -> {
            controller.getNotificationsByEmailId("invalid@example.com");
        });
    }

    @Test
    void testGetNotificationsBySsnSuccess() {
        // Arrange
        List<EmailNotificationDto> notifications = Arrays.asList(notificationDto);
        when(service.getNotificationsBySsn(anyString())).thenReturn(notifications);

        // Act
        ResponseEntity<List<EmailNotificationDto>> result = controller.getNotificationsBySsn("123456789");

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals("123456789", result.getBody().get(0).getSsn());
    }

    @Test
    void testGetNotificationsByStageSuccess() {
        // Arrange
        List<EmailNotificationDto> notifications = Arrays.asList(notificationDto);
        when(service.getNotificationsByStage(anyString())).thenReturn(notifications);

        // Act
        ResponseEntity<List<EmailNotificationDto>> result = controller.getNotificationsByStage("PENDING");

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals("PENDING", result.getBody().get(0).getStage());
    }

    @Test
    void testGetNotificationsByAppSeqIdAndStageSuccess() {
        // Arrange
        List<EmailNotificationDto> notifications = Arrays.asList(notificationDto);
        when(service.getNotificationsByAppSeqIdAndStage(anyLong(), anyString())).thenReturn(notifications);

        // Act
        ResponseEntity<List<EmailNotificationDto>> result = controller.getNotificationsByAppSeqIdAndStage(123L, "PENDING");

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals(123L, result.getBody().get(0).getAppSeqId());
        assertEquals("PENDING", result.getBody().get(0).getStage());
    }

    @Test
    void testGetNotificationsByAppSeqIdAndStageNotFound() {
        // Arrange
        when(service.getNotificationsByAppSeqIdAndStage(anyLong(), anyString()))
                .thenThrow(new ApplicationException("No notifications found for app ID: 999 and stage: COMPLETED", 404));

        // Act & Assert
        assertThrows(ApplicationException.class, () -> {
            controller.getNotificationsByAppSeqIdAndStage(999L, "COMPLETED");
        });
    }
}


