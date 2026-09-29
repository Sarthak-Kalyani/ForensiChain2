package com.sdcems.sdcems.repository;

import com.sdcems.sdcems.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {

    List<AuditLog> findByEvidenceIdOrderByTimestampAsc(Integer evidenceId);

}