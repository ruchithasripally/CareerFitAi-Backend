package com.careerfitai.controller;

import com.careerfitai.dto.ResumeResponseDto;
import com.careerfitai.exception.EmptyFileException;
import com.careerfitai.exception.InvalidFileException;
import com.careerfitai.exception.ResourceNotFoundException;
import com.careerfitai.service.ResumeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ResumeController.class, HealthController.class})
class ResumeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResumeService resumeService;

    @Test
    @DisplayName("POST /api/v1/resumes/analyze should return 201 Created on valid upload")
    void shouldReturn201OnSuccessfulUpload() throws Exception {
        byte[] pdfBytes = new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D, 0x31, 0x2E, 0x37};
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", pdfBytes);

        ResumeResponseDto responseDto = new ResumeResponseDto(
                1L,
                "resume.pdf",
                "PDF",
                List.of("Java", "MySQL", "Spring Boot"),
                3,
                LocalDateTime.now()
        );

        when(resumeService.processAndSaveResume(any())).thenReturn(responseDto);

        mockMvc.perform(multipart("/api/v1/resumes/analyze").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Resume uploaded successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.filename").value("resume.pdf"))
                .andExpect(jsonPath("$.data.skillCount").value(3))
                .andExpect(jsonPath("$.data.skills[0]").value("Java"))
                .andExpect(jsonPath("$.data.skills[1]").value("MySQL"))
                .andExpect(jsonPath("$.data.skills[2]").value("Spring Boot"));
    }

    @Test
    @DisplayName("POST /api/v1/resumes/analyze should return 400 Bad Request when file is empty")
    void shouldReturn400WhenFileIsEmpty() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.pdf", "application/pdf", new byte[0]);

        when(resumeService.processAndSaveResume(any()))
                .thenThrow(new EmptyFileException("No file provided. Please select a valid PDF or DOCX resume to upload."));

        mockMvc.perform(multipart("/api/v1/resumes/analyze").file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("EMPTY_FILE"));
    }

    @Test
    @DisplayName("POST /api/v1/resumes/analyze should return 400 Bad Request when file type is invalid")
    void shouldReturn400WhenFileTypeIsInvalid() throws Exception {
        MockMultipartFile txtFile = new MockMultipartFile("file", "resume.txt", "text/plain", "Sample text".getBytes());

        when(resumeService.processAndSaveResume(any()))
                .thenThrow(new InvalidFileException("Unsupported file format (.txt). CareerFit AI only accepts PDF (.pdf) and Word (.docx) documents."));

        mockMvc.perform(multipart("/api/v1/resumes/analyze").file(txtFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("INVALID_FILE_TYPE"));
    }

    @Test
    @DisplayName("GET /api/v1/resumes/{id} should return 200 OK when found")
    void shouldReturn200WhenResumeFound() throws Exception {
        ResumeResponseDto responseDto = new ResumeResponseDto(
                42L,
                "john_doe.docx",
                "DOCX",
                List.of("Docker", "Kubernetes", "Python"),
                3,
                LocalDateTime.now()
        );

        when(resumeService.getResumeById(eq(42L))).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/resumes/42").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(42))
                .andExpect(jsonPath("$.data.filename").value("john_doe.docx"))
                .andExpect(jsonPath("$.data.skills[0]").value("Docker"));
    }

    @Test
    @DisplayName("GET /api/v1/resumes/{id} should return 404 Not Found when ID does not exist")
    void shouldReturn404WhenResumeNotFound() throws Exception {
        when(resumeService.getResumeById(eq(999L)))
                .thenThrow(new ResourceNotFoundException("Resume analysis with ID 999 was not found."));

        mockMvc.perform(get("/api/v1/resumes/999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    @DisplayName("GET /api/v1/health should return 200 OK")
    void shouldReturnHealthStatusOk() throws Exception {
        mockMvc.perform(get("/api/v1/health").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("CareerFit AI Backend"));
    }
}
