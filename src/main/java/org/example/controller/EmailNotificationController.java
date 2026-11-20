package org.example.controller;

import org.example.dto.ApplicationEmailRequest;
import org.example.dto.EmailNotificationDto;
import org.example.dto.EmailNotificationResponse;
import org.example.service.EmailNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/email")
public class EmailNotificationController {

    private final EmailNotificationService service;

    public EmailNotificationController(EmailNotificationService service) {
        this.service = service;
    }

    /**
     * POST /email/save - Save a new email notification
     */
    @PostMapping("/save")
    public ResponseEntity<EmailNotificationResponse> saveEmailNotification(
            @RequestBody ApplicationEmailRequest request) {
        EmailNotificationResponse response = service.saveEmailNotification(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * GET /email/all - Get all email notifications
     */
    @GetMapping("/all")
    public ResponseEntity<List<EmailNotificationDto>> getAllNotifications() {
        List<EmailNotificationDto> notifications = service.getAllNotifications();
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    /**
     * GET /email/{interfaceSeqId} - Get notification by ID
     */
    @GetMapping("/{interfaceSeqId}")
    public ResponseEntity<EmailNotificationDto> getNotificationById(
            @PathVariable Long interfaceSeqId) {
        EmailNotificationDto notification = service.getNotificationById(interfaceSeqId);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    /**
     * GET /email/app/{appSeqId} - Get notifications by application sequence ID
     */
    @GetMapping("/app/{appSeqId}")
    public ResponseEntity<List<EmailNotificationDto>> getNotificationsByAppSeqId(
            @PathVariable Long appSeqId) {
        List<EmailNotificationDto> notifications = service.getNotificationsByAppSeqId(appSeqId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    /**
     * GET /email/email-id - Get notifications by email ID (query param)
     */
    @GetMapping("/search/email")
    public ResponseEntity<List<EmailNotificationDto>> getNotificationsByEmailId(
            @RequestParam String emailId) {
        List<EmailNotificationDto> notifications = service.getNotificationsByEmailId(emailId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    /**
     * GET /email/search/ssn - Get notifications by SSN (query param)
     */
    @GetMapping("/search/ssn")
    public ResponseEntity<List<EmailNotificationDto>> getNotificationsBySsn(
            @RequestParam String ssn) {
        List<EmailNotificationDto> notifications = service.getNotificationsBySsn(ssn);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    /**
     * GET /email/search/stage - Get notifications by stage (query param)
     */
    @GetMapping("/search/stage")
    public ResponseEntity<List<EmailNotificationDto>> getNotificationsByStage(
            @RequestParam String stage) {
        List<EmailNotificationDto> notifications = service.getNotificationsByStage(stage);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    /**
     * GET /email/search/app-stage - Get notifications by app ID and stage (query params)
     */
    @GetMapping("/search/app-stage")
    public ResponseEntity<List<EmailNotificationDto>> getNotificationsByAppSeqIdAndStage(
            @RequestParam Long appSeqId,
            @RequestParam String stage) {
        List<EmailNotificationDto> notifications = service.getNotificationsByAppSeqIdAndStage(appSeqId, stage);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }
}
