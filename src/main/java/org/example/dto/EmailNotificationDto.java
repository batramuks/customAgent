package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailNotificationDto {
    private Long interfaceSeqId;
    private Long appSeqId;
    private String appId;
    private String applicationStatus;
    private String processState;
    private String emailId;
    private String ssn;
    private String stage;
    private String subject;
    private String content;
    private String languageCode;
    private String createdBy;
    private LocalDateTime createdOn;
}
