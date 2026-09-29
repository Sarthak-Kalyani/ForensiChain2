package com.sdcems.sdcems.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Evidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fileName;

    @Column(length = 1000)
    private String hashValue;

    private Integer userId;

    @Column(columnDefinition = "LONGTEXT")
    private String metadata;

    // Relationship with Investigation Case
    @ManyToOne
    @JoinColumn(name = "case_id")
    private InvestigationCase investigationCase;

    // ===========================
    // File Metadata
    // ===========================

    private Long fileSize;

    private String fileExtension;

    private String contentType;

    // ===========================
    // OCR Extracted Text
    // ===========================

    @Column(columnDefinition = "LONGTEXT")
    private String extractedText;

    // ===========================
    // Evidence Status
    // ===========================

    private String status = "ACTIVE";

    private LocalDateTime uploadedAt = LocalDateTime.now();

    // ===========================
    // Getters & Setters
    // ===========================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public InvestigationCase getInvestigationCase() {
        return investigationCase;
    }

    public void setInvestigationCase(InvestigationCase investigationCase) {
        this.investigationCase = investigationCase;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getMetadata() {
    return metadata;
}

public void setMetadata(String metadata) {
    this.metadata = metadata;
}
}