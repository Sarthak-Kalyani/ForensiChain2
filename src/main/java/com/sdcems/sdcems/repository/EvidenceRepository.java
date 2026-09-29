package com.sdcems.sdcems.repository;

import com.sdcems.sdcems.model.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EvidenceRepository extends JpaRepository<Evidence, Integer> {

    // ==========================
    // Investigation Case Queries
    // ==========================

    List<Evidence> findByInvestigationCase_Id(Integer caseId);

    List<Evidence> findByInvestigationCase_IdAndStatus(Integer caseId, String status);

    // ==========================
    // Dashboard Statistics
    // ==========================

    long countByStatus(String status);

    @Query("SELECT COALESCE(SUM(e.fileSize),0) FROM Evidence e")
    Long getTotalStorage();

    @Query("SELECT COUNT(e) FROM Evidence e WHERE e.fileExtension='JPG'")
    long countJpgFiles();

    @Query("SELECT COUNT(e) FROM Evidence e WHERE e.fileExtension='PNG'")
    long countPngFiles();

    @Query("SELECT COUNT(e) FROM Evidence e WHERE e.fileExtension='PDF'")
    long countPdfFiles();

}