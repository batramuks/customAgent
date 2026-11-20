package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "IORIG_NOTIFICATION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IOrigNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq")
    @SequenceGenerator(name = "notification_seq", sequenceName = "IORIG_NOTIFICATION_SEQ", allocationSize = 1)
    @Column(name = "INTF_SEQ_ID")
    private Long interfaceSeqId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_SEQ_ID", referencedColumnName = "APP_SEQ_ID", nullable = false, insertable = true, updatable = false)
    private Application application;

    @Column(name = "INTF_EMAIL_ID", length = 100)
    private String emailId;

    @Column(name = "INTF_SSN", length = 40)
    private String ssn;

    @Column(name = "INTF_STAGE", length = 100)
    private String stage;

    @Column(name = "INTF_SUBJECT", length = 255)
    private String subject;

    @Lob
    @Column(name = "INTF_CONTENT")
    private String content;

    @Column(name = "INTF_LANG_CODE", length = 255)
    private String languageCode;

    @Column(name = "INTF_CREATED_BY", length = 40)
    private String createdBy;

    @Column(name = "INTF_CREATED_ON", insertable = false, updatable = false)
    private LocalDateTime createdOn;
}
