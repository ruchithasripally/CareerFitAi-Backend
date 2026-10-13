package com.careerfitai.service;

import com.careerfitai.exception.EmptyFileException;
import com.careerfitai.exception.InvalidFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class FileValidationServiceTest {

    private FileValidationService fileValidationService;

    @BeforeEach
    void setUp() {
        fileValidationService = new FileValidationService();
    }

    @Test
    @DisplayName("Should throw EmptyFileException for null or empty file")
    void shouldThrowEmptyFileExceptionForEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "resume.pdf", "application/pdf", new byte[0]);

        assertThrows(EmptyFileException.class, () -> fileValidationService.validateFile(emptyFile));
        assertThrows(EmptyFileException.class, () -> fileValidationService.validateFile(null));
    }

    @Test
    @DisplayName("Should throw InvalidFileException when file exceeds 5MB")
    void shouldThrowWhenFileSizeExceedsLimit() {
        byte[] oversized = new byte[(int) (5 * 1024 * 1024 + 1024)];
        // Fill header with valid PDF magic bytes
        oversized[0] = 0x25; oversized[1] = 0x50; oversized[2] = 0x44; oversized[3] = 0x46;

        MockMultipartFile largeFile = new MockMultipartFile("file", "large.pdf", "application/pdf", oversized);

        InvalidFileException ex = assertThrows(InvalidFileException.class, () -> fileValidationService.validateFile(largeFile));
        assertTrue(ex.getMessage().contains("exceeds the 5 MB size limit"));
    }

    @Test
    @DisplayName("Should throw InvalidFileException for unsupported extensions")
    void shouldThrowForUnsupportedExtension() {
        byte[] content = "Some resume text".getBytes();
        MockMultipartFile txtFile = new MockMultipartFile("file", "resume.txt", "text/plain", content);
        MockMultipartFile exeFile = new MockMultipartFile("file", "resume.exe", "application/octet-stream", content);

        assertThrows(InvalidFileException.class, () -> fileValidationService.validateFile(txtFile));
        assertThrows(InvalidFileException.class, () -> fileValidationService.validateFile(exeFile));
    }

    @Test
    @DisplayName("Should accept valid PDF header")
    void shouldAcceptValidPdf() {
        byte[] pdfHeader = new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D, 0x31, 0x2E, 0x37};
        MockMultipartFile pdfFile = new MockMultipartFile("file", "resume.pdf", "application/pdf", pdfHeader);

        String type = fileValidationService.validateFile(pdfFile);
        assertEquals(FileValidationService.TYPE_PDF, type);
    }

    @Test
    @DisplayName("Should accept valid DOCX header")
    void shouldAcceptValidDocx() {
        byte[] zipHeader = new byte[]{0x50, 0x4B, 0x03, 0x04, 0x14, 0x00, 0x06, 0x00};
        MockMultipartFile docxFile = new MockMultipartFile("file", "resume.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", zipHeader);

        String type = fileValidationService.validateFile(docxFile);
        assertEquals(FileValidationService.TYPE_DOCX, type);
    }
}
