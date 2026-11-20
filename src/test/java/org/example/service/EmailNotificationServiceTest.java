package org.example.service;

import org.example.dto.ApplicationEmailRequest;
import org.example.dto.EmailNotificationDto;
import org.example.dto.EmailNotificationResponse;
import org.example.entity.Application;
import org.example.entity.IOrigNotification;
import org.example.exception.ApplicationException;
import org.example.repository.ApplicationRepository;
import org.example.repository.IOrigNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceTest {

    @Mock
    private IOrigNotificationRepository repository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private EmailNotificationService service;

    private ApplicationEmailRequest request;
    private IOrigNotification notification;
    private Application application;

    @BeforeEach
    void setUp() {
        application = Application.builder()
                .appSeqId(123L)
                .appId("873dd088-4ab7-41b3-a9df-11bf96db188e")
                .applicationStatus("ACTIVE")
                .processState("INITIATED")
                .countryCode("US")
                .currencyCode("USD")
                .createdBy("ADMIN")
                .modifiedBy("ADMIN")
                .appLock(0L)
                .build();

        request = new ApplicationEmailRequest();
        request.setAppId("873dd088-4ab7-41b3-a9df-11bf96db188e");
        request.setSsn("123456789");
        request.setAppStatus("PENDING");
        request.setEmailId("test@example.com");
        request.setSubject("Test Subject");
        request.setContent("Test Content");
        request.setCreatedBy("ADMIN");
        request.setLanguageCode("EN");

        notification = IOrigNotification.builder()
                .interfaceSeqId(1L)
                .application(application)
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

    // POST/Save Tests
    @Test
    void testSaveEmailNotificationSuccess() {
        // Arrange
        when(applicationRepository.findByAppId("873dd088-4ab7-41b3-a9df-11bf96db188e"))
                .thenReturn(Optional.of(application));
        when(repository.save(any(IOrigNotification.class))).thenReturn(notification);

        // Act
        EmailNotificationResponse response = service.saveEmailNotification(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getInterfaceSeqId());
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("Email notification saved successfully", response.getMessage());
        verify(applicationRepository, times(1)).findByAppId("873dd088-4ab7-41b3-a9df-11bf96db188e");
        verify(repository, times(1)).save(any(IOrigNotification.class));
    }

    @Test
    void testSaveEmailNotificationMissingAppId() {
        // Arrange
        request.setAppId(null);

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            service.saveEmailNotification(request);
        });

        assertEquals("Application ID is required", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void testSaveEmailNotificationEmptyAppId() {
        // Arrange
        request.setAppId("");

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            service.saveEmailNotification(request);
        });

        assertEquals("Application ID is required", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void testSaveEmailNotificationApplicationNotFound() {
        // Arrange
        when(applicationRepository.findByAppId("873dd088-4ab7-41b3-a9df-11bf96db188e"))
                .thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            service.saveEmailNotification(request);
        });

        assertEquals("Application not found with ID: 873dd088-4ab7-41b3-a9df-11bf96db188e", 
                exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void testSaveEmailNotificationWithAllFields() {
        // Arrange
        when(applicationRepository.findByAppId("873dd088-4ab7-41b3-a9df-11bf96db188e"))
                .thenReturn(Optional.of(application));
        when(repository.save(any(IOrigNotification.class))).thenReturn(notification);

        // Act
        EmailNotificationResponse response = service.saveEmailNotification(request);

        // Assert
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        verify(repository, times(1)).save(any(IOrigNotification.class));
    }

    @Test
    void testSaveEmailNotificationDatabaseError() {
        // Arrange
        when(applicationRepository.findByAppId("873dd088-4ab7-41b3-a9df-11bf96db188e"))
                .thenReturn(Optional.of(application));
        when(repository.save(any(IOrigNotification.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            service.saveEmailNotification(request);
        });

        verify(repository, times(1)).save(any(IOrigNotification.class));
    }

    // GET/Retrieve Tests
    @Test
    void testGetAllNotificationsSuccess() {
        // Arrange
        List<IOrigNotification> notifications = Arrays.asList(notification);
        when(repository.findAll()).thenReturn(notifications);

        // Act
        List<EmailNotificationDto> result = service.getAllNotifications();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getInterfaceSeqId());
        assertEquals("test@example.com", result.get(0).getEmailId());
        assertEquals("873dd088-4ab7-41b3-a9df-11bf96db188e", result.get(0).getAppId());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetAllNotificationsEmpty() {
        // Arrange
        when(repository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<EmailNotificationDto> result = service.getAllNotifications();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetNotificationByIdSuccess() {
        // Arrange
        when(repository.findByInterfaceSeqId(1L)).thenReturn(Optional.of(notification));

        // Act
        EmailNotificationDto result = service.getNotificationById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getInterfaceSeqId());
        assertEquals("test@example.com", result.getEmailId());
        assertEquals("873dd088-4ab7-41b3-a9df-11bf96db188e", result.getAppId());
        verify(repository, times(1)).findByInterfaceSeqId(1L);
    }

    @Test
    void testGetNotificationByIdNotFound() {
        // Arrange
        when(repository.findByInterfaceSeqId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            service.getNotificationById(999L);
        });

        assertEquals("Notification not found with ID: 999", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testGetNotificationByIdInvalidId() {
        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            service.getNotificationById(null);
        });

        assertEquals("Invalid interface sequence ID", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        verify(repository, never()).findByInterfaceSeqId(any());
    }

    @Test
    void testGetNotificationsByAppSeqIdSuccess() {
        // Arrange
        List<IOrigNotification> notifications = Arrays.asList(notification);
        when(applicationRepository.findById(123L)).thenReturn(Optional.of(application));
        when(repository.findByApplication_AppSeqId(123L)).thenReturn(notifications);

        // Act
        List<EmailNotificationDto> result = service.getNotificationsByAppSeqId(123L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(123L, result.get(0).getAppSeqId());
        verify(applicationRepository, times(1)).findById(123L);
        verify(repository, times(1)).findByApplication_AppSeqId(123L);
    }

    @Test
    void testGetNotificationsByAppSeqIdApplicationNotFound() {
        // Arrange
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            service.getNotificationsByAppSeqId(999L);
        });

        assertEquals("Application not found with ID: 999", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testGetNotificationsByAppSeqIdNotFound() {
        // Arrange
        when(applicationRepository.findById(123L)).thenReturn(Optional.of(application));
        when(repository.findByApplication_AppSeqId(123L)).thenReturn(Arrays.asList());

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            service.getNotificationsByAppSeqId(123L);
        });

        assertEquals("No notifications found for app ID: 123", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testGetNotificationsByEmailIdSuccess() {
        // Arrange
        List<IOrigNotification> notifications = Arrays.asList(notification);
        when(repository.findByEmailId("test@example.com")).thenReturn(notifications);

        // Act
        List<EmailNotificationDto> result = service.getNotificationsByEmailId("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmailId());
        verify(repository, times(1)).findByEmailId("test@example.com");
    }

    @Test
    void testGetNotificationsByEmailIdEmpty() {
        // Arrange
        when(repository.findByEmailId("invalid@example.com")).thenReturn(Arrays.asList());

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            service.getNotificationsByEmailId("invalid@example.com");
        });

        assertEquals("No notifications found for email: invalid@example.com", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testGetNotificationsBySsnSuccess() {
        // Arrange
        List<IOrigNotification> notifications = Arrays.asList(notification);
        when(repository.findBySsn("123456789")).thenReturn(notifications);

        // Act
        List<EmailNotificationDto> result = service.getNotificationsBySsn("123456789");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("123456789", result.get(0).getSsn());
        verify(repository, times(1)).findBySsn("123456789");
    }

    @Test
    void testGetNotificationsByStageSuccess() {
        // Arrange
        List<IOrigNotification> notifications = Arrays.asList(notification);
        when(repository.findByStage("PENDING")).thenReturn(notifications);

        // Act
        List<EmailNotificationDto> result = service.getNotificationsByStage("PENDING");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStage());
        verify(repository, times(1)).findByStage("PENDING");
    }

    @Test
    void testGetNotificationsByAppSeqIdAndStageSuccess() {
        // Arrange
        List<IOrigNotification> notifications = Arrays.asList(notification);
        when(applicationRepository.findById(123L)).thenReturn(Optional.of(application));
        when(repository.findByApplication_AppSeqIdAndStage(123L, "PENDING")).thenReturn(notifications);

        // Act
        List<EmailNotificationDto> result = service.getNotificationsByAppSeqIdAndStage(123L, "PENDING");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(123L, result.get(0).getAppSeqId());
        assertEquals("PENDING", result.get(0).getStage());
        verify(applicationRepository, times(1)).findById(123L);
        verify(repository, times(1)).findByApplication_AppSeqIdAndStage(123L, "PENDING");
    }

    @Test
    void testGetNotificationsByAppSeqIdAndStageNotFound() {
        // Arrange
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            service.getNotificationsByAppSeqIdAndStage(999L, "COMPLETED");
        });

        assertEquals("Application not found with ID: 999", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }
}
