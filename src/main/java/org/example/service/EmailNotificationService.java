package org.example.service;

import org.example.dto.ApplicationEmailRequest;
import org.example.dto.EmailNotificationDto;
import org.example.dto.EmailNotificationResponse;
import org.example.entity.Application;
import org.example.entity.IOrigNotification;
import org.example.exception.ApplicationException;
import org.example.repository.ApplicationRepository;
import org.example.repository.IOrigNotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmailNotificationService {

    private final IOrigNotificationRepository repository;
    private final ApplicationRepository applicationRepository;

    public EmailNotificationService(IOrigNotificationRepository repository, 
                                    ApplicationRepository applicationRepository) {
        this.repository = repository;
        this.applicationRepository = applicationRepository;
    }

    public EmailNotificationResponse saveEmailNotification(ApplicationEmailRequest request) {
        if (request.getAppId() == null || request.getAppId().isEmpty()) {
            throw new ApplicationException("Application ID is required", 400);
        }

        String appId = request.getAppId().trim();

        // Validate application exists by appId (UUID)
        Application application = applicationRepository.findByAppId(appId)
                .orElseThrow(() -> new ApplicationException("Application not found with ID: " + appId, 404));

        IOrigNotification notification = IOrigNotification.builder()
                .application(application)
                .emailId(request.getEmailId())
                .ssn(request.getSsn())
                .stage(request.getAppStatus())
                .subject(request.getSubject())
                .content(request.getContent())
                .languageCode(request.getLanguageCode())
                .createdBy(request.getCreatedBy())
                .build();

        IOrigNotification savedNotification = repository.save(notification);

        return EmailNotificationResponse.builder()
                .interfaceSeqId(savedNotification.getInterfaceSeqId())
                .message("Email notification saved successfully")
                .status("SUCCESS")
                .build();
    }

    /**
     * Get all email notifications
     */
    @Transactional(readOnly = true)
    public List<EmailNotificationDto> getAllNotifications() {
        return repository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get notification by interface sequence ID
     */
    @Transactional(readOnly = true)
    public EmailNotificationDto getNotificationById(Long interfaceSeqId) {
        if (interfaceSeqId == null || interfaceSeqId <= 0) {
            throw new ApplicationException("Invalid interface sequence ID", 400);
        }

        return repository.findByInterfaceSeqId(interfaceSeqId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ApplicationException(
                        "Notification not found with ID: " + interfaceSeqId, 404));
    }

    /**
     * Get all notifications by application ID
     */
    @Transactional(readOnly = true)
    public List<EmailNotificationDto> getNotificationsByAppSeqId(Long appSeqId) {
        if (appSeqId == null || appSeqId <= 0) {
            throw new ApplicationException("Invalid application sequence ID", 400);
        }

        // Validate application exists
        Application app = applicationRepository.findById(appSeqId)
                .orElseThrow(() -> new ApplicationException("Application not found with ID: " + appSeqId, 404));

        List<IOrigNotification> notifications = repository.findByApplication_AppSeqId(appSeqId);
        if (notifications.isEmpty()) {
            throw new ApplicationException(
                    "No notifications found for app ID: " + appSeqId, 404);
        }

        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all notifications by email ID
     */
    @Transactional(readOnly = true)
    public List<EmailNotificationDto> getNotificationsByEmailId(String emailId) {
        if (emailId == null || emailId.isEmpty()) {
            throw new ApplicationException("Email ID is required", 400);
        }

        List<IOrigNotification> notifications = repository.findByEmailId(emailId);
        if (notifications.isEmpty()) {
            throw new ApplicationException(
                    "No notifications found for email: " + emailId, 404);
        }

        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all notifications by SSN
     */
    @Transactional(readOnly = true)
    public List<EmailNotificationDto> getNotificationsBySsn(String ssn) {
        if (ssn == null || ssn.isEmpty()) {
            throw new ApplicationException("SSN is required", 400);
        }

        List<IOrigNotification> notifications = repository.findBySsn(ssn);
        if (notifications.isEmpty()) {
            throw new ApplicationException(
                    "No notifications found for SSN: " + ssn, 404);
        }

        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all notifications by stage
     */
    @Transactional(readOnly = true)
    public List<EmailNotificationDto> getNotificationsByStage(String stage) {
        if (stage == null || stage.isEmpty()) {
            throw new ApplicationException("Stage is required", 400);
        }

        List<IOrigNotification> notifications = repository.findByStage(stage);
        if (notifications.isEmpty()) {
            throw new ApplicationException(
                    "No notifications found for stage: " + stage, 404);
        }

        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get notifications by application ID and stage
     */
    @Transactional(readOnly = true)
    public List<EmailNotificationDto> getNotificationsByAppSeqIdAndStage(Long appSeqId, String stage) {
        if (appSeqId == null || appSeqId <= 0) {
            throw new ApplicationException("Invalid application sequence ID", 400);
        }
        if (stage == null || stage.isEmpty()) {
            throw new ApplicationException("Stage is required", 400);
        }

        // Validate application exists
        Application app = applicationRepository.findById(appSeqId)
                .orElseThrow(() -> new ApplicationException("Application not found with ID: " + appSeqId, 404));

        List<IOrigNotification> notifications = repository.findByApplication_AppSeqIdAndStage(appSeqId, stage);
        if (notifications.isEmpty()) {
            throw new ApplicationException(
                    "No notifications found for app ID: " + appSeqId + " and stage: " + stage, 404);
        }

        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert entity to DTO
     */
    private EmailNotificationDto convertToDto(IOrigNotification entity) {
        Application application = entity.getApplication();
        Long appSeqId = null;
        String appId = null;
        String appStatus = null;
        String processState = null;

        if (application != null) {
            appSeqId = application.getAppSeqId();
            appId = application.getAppId();
            appStatus = application.getApplicationStatus();
            processState = application.getProcessState();
        }

        return EmailNotificationDto.builder()
                .interfaceSeqId(entity.getInterfaceSeqId())
                .appSeqId(appSeqId)
                .appId(appId)
                .applicationStatus(appStatus)
                .processState(processState)
                .emailId(entity.getEmailId())
                .ssn(entity.getSsn())
                .stage(entity.getStage())
                .subject(entity.getSubject())
                .content(entity.getContent())
                .languageCode(entity.getLanguageCode())
                .createdBy(entity.getCreatedBy())
                .createdOn(entity.getCreatedOn())
                .build();
    }
}



