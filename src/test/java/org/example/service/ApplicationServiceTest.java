package org.example.service;

import org.example.dto.ApplicationDto;
import org.example.entity.Application;
import org.example.exception.ApplicationException;
import org.example.repository.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    private ApplicationService applicationService;

    @Mock
    private ApplicationRepository applicationRepository;

    @BeforeEach
    public void setUp() {
        applicationService = new ApplicationService(applicationRepository);
    }

    @Test
    public void testGetApplicationByAppId_Success() {
        // Arrange
        String appId = "APP-001";
        LocalDateTime now = LocalDateTime.now();
        Application mockApplication = Application.builder()
                .appSeqId(1L)
                .appId(appId)
                .appStartTime(now)
                .applicationStatus("ACTIVE")
                .processState("APPROVED")
                .countryCode("US")
                .currencyCode("USD")
                .createdBy("ADMIN")
                .createdOn(now)
                .modifiedBy("ADMIN")
                .modifiedOn(now)
                .appLock(0L)
                .manualScoringNeeded(0L)
                .build();

        when(applicationRepository.findByAppId(appId))
                .thenReturn(Optional.of(mockApplication));

        // Act
        ApplicationDto result = applicationService.getApplicationByAppId(appId);

        // Assert
        assertNotNull(result);
        assertEquals(appId, result.getAppId());
        assertEquals("ACTIVE", result.getApplicationStatus());
        assertEquals("APPROVED", result.getProcessState());
        assertEquals("US", result.getCountryCode());
        assertEquals("USD", result.getCurrencyCode());
        verify(applicationRepository, times(1)).findByAppId(appId);
    }

    @Test
    public void testGetApplicationByAppId_NotFound() {
        // Arrange
        String appId = "INVALID-APP";
        when(applicationRepository.findByAppId(appId))
                .thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                applicationService.getApplicationByAppId(appId));
        
        assertTrue(exception.getMessage().contains("Application not found"));
        assertTrue(exception.getMessage().contains(appId));
        assertEquals(404, exception.getStatusCode());
        verify(applicationRepository, times(1)).findByAppId(appId);
    }

    @Test
    public void testGetApplicationByAppId_AllFields() {
        // Arrange
        String appId = "APP-COMPLETE";
        LocalDateTime now = LocalDateTime.now();
        Application mockApplication = Application.builder()
                .appSeqId(100L)
                .appId(appId)
                .appStartTime(now)
                .appEndTime(now.plusDays(1))
                .applicationStatus("COMPLETED")
                .processState("CLOSED")
                .cmndProcessDefName("LOAN_PROCESS")
                .cmndProcessDefVersion("1.0")
                .apiVersion("2.0")
                .countryCode("CA")
                .currencyCode("CAD")
                .createdBy("SYSTEM")
                .createdOn(now)
                .modifiedBy("SYSTEM")
                .modifiedOn(now)
                .appLock(1L)
                .cspTimestamp(now)
                .manualScoringNeeded(1L)
                .build();

        when(applicationRepository.findByAppId(appId))
                .thenReturn(Optional.of(mockApplication));

        // Act
        ApplicationDto result = applicationService.getApplicationByAppId(appId);

        // Assert
        assertNotNull(result);
        assertEquals(appId, result.getAppId());
        assertEquals("COMPLETED", result.getApplicationStatus());
        assertEquals("LOAN_PROCESS", result.getCmndProcessDefName());
        assertEquals("1.0", result.getCmndProcessDefVersion());
        assertEquals("2.0", result.getApiVersion());
        assertEquals("CA", result.getCountryCode());
        assertEquals("CAD", result.getCurrencyCode());
        assertEquals("SYSTEM", result.getCreatedBy());
        assertEquals(1L, result.getManualScoringNeeded());
        assertEquals(now, result.getCspTimestamp());
    }

    @Test
    public void testGetApplicationByAppId_MinimalFields() {
        // Arrange
        String appId = "APP-MIN";
        Application mockApplication = Application.builder()
                .appSeqId(2L)
                .appId(appId)
                .applicationStatus("PENDING")
                .processState("SUBMITTED")
                .countryCode("US")
                .currencyCode("USD")
                .createdBy("USER")
                .modifiedBy("USER")
                .appLock(0L)
                .build();

        when(applicationRepository.findByAppId(appId))
                .thenReturn(Optional.of(mockApplication));

        // Act
        ApplicationDto result = applicationService.getApplicationByAppId(appId);

        // Assert
        assertNotNull(result);
        assertEquals(appId, result.getAppId());
        assertEquals("PENDING", result.getApplicationStatus());
        assertNull(result.getAppStartTime());
        assertNull(result.getAppEndTime());
        assertNull(result.getCmndProcessDefName());
    }

    @Test
    public void testGetApplicationByAppId_DifferentCountries() {
        // Test with multiple country codes
        String[] countryCodes = {"US", "GB", "CA", "AU", "IN"};
        
        for (String countryCode : countryCodes) {
            // Arrange
            String appId = "APP-" + countryCode;
            Application mockApplication = Application.builder()
                    .appSeqId(1L)
                    .appId(appId)
                    .applicationStatus("ACTIVE")
                    .processState("APPROVED")
                    .countryCode(countryCode)
                    .currencyCode("XXX")
                    .createdBy("ADMIN")
                    .modifiedBy("ADMIN")
                    .appLock(0L)
                    .build();

            when(applicationRepository.findByAppId(appId))
                    .thenReturn(Optional.of(mockApplication));

            // Act
            ApplicationDto result = applicationService.getApplicationByAppId(appId);

            // Assert
            assertEquals(countryCode, result.getCountryCode());
        }
    }

    @Test
    public void testGetApplicationByAppId_ExceptionMessage() {
        // Arrange
        String appId = "NONEXISTENT";
        when(applicationRepository.findByAppId(appId))
                .thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                applicationService.getApplicationByAppId(appId));
        
        String expectedMessage = "Application not found with appId: " + appId;
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }
}
