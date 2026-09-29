# ForensiChain

## AI-Powered Digital Evidence Investigation Platform

ForensiChain is a digital forensics and evidence management platform designed to help investigators securely collect, manage, analyze, verify, and report digital evidence.

The system combines **Spring Boot, MySQL, Digital Forensics, Cybersecurity, OCR, AI-assisted investigation analysis, and AWS deployment** into a single investigation workflow.

---

## Technology Stack

### Backend

* Java
* Spring Boot
* Spring MVC
* Spring Data JPA
* Maven

### Frontend

* HTML
* CSS
* Thymeleaf
* Bootstrap
* Bootstrap Icons
* JavaScript

### Database

* MySQL
* AWS RDS

### Digital Forensics & Security

* SHA-256
* Tesseract OCR
* Forensic metadata extraction
* Chain of custody
* Audit logging
* Evidence integrity verification
* Tamper detection

### Reporting & Analysis

* Apache PDFBox / OpenPDF
* AI Investigation Analysis
* Automated Investigation Summaries

### Cloud & Deployment

* AWS EC2
* AWS RDS
* Linux
* AWS Cloud Infrastructure

---

## System Architecture

```text
                         USER
                           |
                           v
              +-------------------------+
              |    ForensiChain Web UI  |
              |   Thymeleaf + Bootstrap |
              +------------+------------+
                           |
                           v
              +-------------------------+
              |     Spring Boot API     |
              +------------+------------+
                           |
          +----------------+----------------+
          |                |                |
          v                v                v
   Case Management   Evidence Management   Authentication
                           |
                           v
              +-------------------------+
              |    Digital Forensics    |
              |                         |
              |  SHA-256 Hashing        |
              |  OCR                    |
              |  Metadata Extraction    |
              |  Risk Assessment        |
              +------------+------------+
                           |
              +------------+------------+
              |                         |
              v                         v
       Chain of Custody           AI Analysis
       & Audit Logs              & Summaries
              |                         |
              +------------+------------+
                           |
                           v
              +-------------------------+
              |      MySQL Database     |
              |        AWS RDS          |
              +------------+------------+
                           |
                           v
              +-------------------------+
              |      Evidence Files     |
              |       AWS EC2           |
              +-------------------------+
```

---

## Digital Evidence Workflow

```text
Upload Evidence
       |
       v
Validate Evidence File
       |
       v
Generate SHA-256 Hash
       |
       v
Store Evidence Metadata
       |
       +--------------------+
       |                    |
       v                    v
      OCR          Forensic Metadata
       |                    |
       +---------+----------+
                 |
                 v
          Risk Assessment
                 |
                 v
        Store Evidence Record
                 |
                 v
        Chain of Custody
                 |
                 v
       Integrity Verification
                 |
          +------+------+
          |             |
          v             v
        VALID        TAMPERED
          |             |
          +------+------+
                 |
                 v
       AI Investigation Analysis
                 |
                 v
       PDF Investigation Report
```

---

## Evidence Integrity

ForensiChain uses **SHA-256 cryptographic hashing** to generate a unique fingerprint for every uploaded evidence file.

When evidence is uploaded:

```text
Evidence File
     |
     v
SHA-256 Hash
     |
     v
Stored with Evidence Record
```

During verification, the physical evidence file is hashed again.

```text
Stored SHA-256
      |
      | Compare
      v
Current SHA-256
```

If both hashes match:

**VALID - File Not Modified**

If the hashes are different:

**TAMPERED - File Changed**

This provides a mechanism for detecting modifications to registered digital evidence.

---

## OCR Analysis

For supported image evidence, ForensiChain uses **Tesseract OCR** to automatically extract readable text.

The extracted content is stored with the evidence record and displayed in the evidence details page.

### Example Workflow

```text
Image Evidence
      |
      v
Tesseract OCR
      |
      v
Extracted Text
      |
      v
Stored with Evidence
      |
      v
Available for Investigation Analysis
```

The OCR engine is configured for the AWS EC2 Linux environment using the installed Tesseract language data.

---

## Forensic Metadata

ForensiChain extracts forensic metadata from uploaded evidence.

Depending on the file type, metadata can include:

* File name
* File size
* File type
* MIME type
* File extension
* Image dimensions
* Color information
* Compression information
* File timestamps
* Other available forensic metadata

This information helps investigators understand the characteristics of the original evidence.

---

## Chain of Custody

ForensiChain maintains an audit trail of important evidence-related actions.

Examples include:

* `UPLOAD`
* `DOWNLOAD`
* `VERIFY`
* `DELETE`

Each action can be associated with:

* User
* Evidence
* Timestamp
* Action performed

This provides a historical record of evidence activity.

---

## Investigation Management

Evidence is associated with investigation cases.

Each investigation case can contain information such as:

* Case number
* Case title
* Case type
* Priority
* Status
* Assigned officer
* Investigator
* Description
* Evidence records

This allows investigators to organize digital evidence according to individual investigations.

---

## AI Investigation Analysis

ForensiChain includes AI-assisted investigation analysis using available evidence information such as:

* Evidence metadata
* OCR-extracted content
* SHA-256 integrity information
* File characteristics
* Investigation context

The analysis can assist investigators by generating structured investigation summaries and identifying useful information from collected evidence.

---

## PDF Investigation Reports

ForensiChain supports generation of investigation reports in PDF format.

Reports can contain information related to:

* Investigation case
* Evidence
* Evidence metadata
* Integrity information
* OCR results
* Security analysis
* Investigation findings

---

## Security

ForensiChain applies multiple security mechanisms to protect evidence and investigation data.

### Evidence Integrity

SHA-256 hashing is used to detect evidence modification.

### Authentication

Users must authenticate before accessing protected evidence functionality.

### Session-Based Access

The application maintains the authenticated user's session and uses it for access control.

### Ownership Validation

Evidence access and operations are validated against the authenticated user.

### Audit Logging

Important evidence operations are recorded for investigation traceability.

---

## Cloud Deployment

ForensiChain is deployed on AWS using:

```text
                    Internet
                       |
                       v
                AWS EC2 Instance
                       |
                       v
                Spring Boot App
                   Port 8080
                       |
             +---------+---------+
             |                   |
             v                   v
        AWS RDS MySQL      Evidence Storage
                              on EC2
```

### Application Server

AWS EC2 hosts the Spring Boot application.

### Database

AWS RDS hosts the MySQL database used by the application.

### Current Application URL

http://43.204.140.81:8080

> The current deployment uses the EC2 public IP address. A domain name and HTTPS configuration can be added for production deployment.

---

## Project Structure

```text
ForensiChain/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── sdcems/
│       │           └── sdcems/
│       │               ├── controller/
│       │               ├── model/
│       │               ├── repository/
│       │               └── service/
│       │
│       └── resources/
│           ├── templates/
│           └── application.properties
│
├── uploads/
│
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

---

## Main Application Modules

```text
Authentication
     |
     v
Dashboard
     |
     +---- Investigation Cases
     |
     +---- Evidence Upload
     |
     +---- Evidence Details
     |
     +---- OCR
     |
     +---- Metadata Analysis
     |
     +---- Integrity Verification
     |
     +---- Chain of Custody
     |
     +---- AI Analysis
     |
     +---- PDF Reports
```

---

## Example Evidence Verification

After uploading evidence, ForensiChain generates a SHA-256 fingerprint.

### Example

```text
Evidence ID: 3

Original SHA-256:

ef5f7e3298c7a3f0fa342a2faf2da64aa1af7163fa31edcdc29a180d31297e69
```

During verification, the current file is hashed again.

If the hashes match:

```text
VALID - File Not Modified
```

If the file has been modified:

```text
TAMPERED - File Changed
```

---

## Current Project Status

The core ForensiChain investigation platform has been implemented.

### Completed

* [x] User authentication
* [x] Investigation case management
* [x] Digital evidence upload
* [x] Evidence storage
* [x] SHA-256 hashing
* [x] Integrity verification
* [x] Tamper detection logic
* [x] Forensic metadata extraction
* [x] Tesseract OCR
* [x] Evidence details
* [x] Chain-of-custody tracking
* [x] Audit logging
* [x] Evidence download
* [x] Soft deletion
* [x] AI investigation analysis
* [x] PDF report generation
* [x] AWS EC2 deployment
* [x] AWS RDS MySQL integration

---

## Current Testing

The application is currently undergoing final end-to-end testing, including:

* Evidence tampering verification
* Access-control testing
* Download testing
* Deleted evidence testing
* Multiple file-type testing
* OCR testing
* Investigation workflow testing

---

## Future Enhancements

Potential production improvements include:

* HTTPS with a custom domain
* Nginx reverse proxy
* Automated application startup using systemd
* Role-Based Access Control
* Multi-user investigator management
* Secure object storage using Amazon S3
* Advanced AI investigation capabilities
* Real-time security monitoring
* Advanced evidence correlation
* CloudWatch monitoring and logging

---

## Author

**Sarthak Kalyani**

### ForensiChain

AI-Powered Digital Evidence Investigation Platform

**Spring Boot • MySQL • Digital Forensics • Cybersecurity • AI • AWS**

© 2026 ForensiChain
