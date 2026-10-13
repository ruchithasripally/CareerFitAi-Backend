package com.careerfitai.controller;

import com.careerfitai.dto.ApiResponse;
import com.careerfitai.dto.ResumeResponseDto;
import com.careerfitai.service.ResumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {

    private static final Logger log = LoggerFactory.getLogger(ResumeController.class);

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    /**
     * Uploads and analyzes a resume (PDF or DOCX).
     * Extracts text, identifies technical skills, persists results to MySQL, and returns detected skills.
     */
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ResumeResponseDto>> analyzeResume(
            @RequestParam("file") MultipartFile file) {
        log.info("Received resume upload request: {}",
                (file != null ? file.getOriginalFilename() : "null"));

        ResumeResponseDto result = resumeService.processAndSaveResume(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resume uploaded successfully", result));
    }

    /**
     * Retrieves an existing resume analysis by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResumeResponseDto>> getResumeById(
            @PathVariable("id") Long id) {
        log.info("Retrieving resume analysis by ID: {}", id);

        ResumeResponseDto result = resumeService.getResumeById(id);

        return ResponseEntity.ok(ApiResponse.success("Resume retrieved successfully", result));
    }
}
