package org.example.repository;

import org.example.entity.IOrigNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IOrigNotificationRepository extends JpaRepository<IOrigNotification, Long> {
    
    /**
     * Find notification by interface sequence ID
     */
    Optional<IOrigNotification> findByInterfaceSeqId(Long interfaceSeqId);
    
    /**
     * Find all notifications by application (using 1-to-many relationship)
     */
    List<IOrigNotification> findByApplication_AppSeqId(Long appSeqId);
    
    /**
     * Find all notifications by email ID
     */
    List<IOrigNotification> findByEmailId(String emailId);
    
    /**
     * Find all notifications by SSN
     */
    List<IOrigNotification> findBySsn(String ssn);
    
    /**
     * Find all notifications by stage/status
     */
    List<IOrigNotification> findByStage(String stage);
    
    /**
     * Find all notifications by application and stage
     */
    List<IOrigNotification> findByApplication_AppSeqIdAndStage(Long appSeqId, String stage);
}
