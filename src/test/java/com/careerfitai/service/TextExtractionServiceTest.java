package com.careerfitai.service;

import com.careerfitai.exception.FileProcessingException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TextExtractionServiceTest {

    private TextExtractionService textExtractionService;

    @BeforeEach
    void setUp() {
        textExtractionService = new TextExtractionService();
    }

    @Test
    @DisplayName("Should extract text from valid PDF document")
    void shouldExtractTextFromPdf() throws IOException {
        byte[] pdfBytes = createSamplePdf("John Doe - Senior Java and Spring Boot Engineer");
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", pdfBytes);

        String text = textExtractionService.extractTextFromPdf(file);

        assertNotNull(text);
        assertTrue(text.contains("John Doe"));
        assertTrue(text.contains("Spring Boot"));
    }

    @Test
    @DisplayName("Should extract text from valid DOCX document")
    void shouldExtractTextFromDocx() throws IOException {
        byte[] docxBytes = createSampleDocx("Jane Smith - Cloud Architect with Docker and Kubernetes skills");
        MockMultipartFile file = new MockMultipartFile("file", "resume.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", docxBytes);

        String text = textExtractionService.extractTextFromDocx(file);

        assertNotNull(text);
        assertTrue(text.contains("Jane Smith"));
        assertTrue(text.contains("Kubernetes"));
    }

    @Test
    @DisplayName("Should throw FileProcessingException for empty/whitespace-only PDF")
    void shouldThrowForEmptyPdf() throws IOException {
        byte[] emptyPdfBytes = createSamplePdf(""); // Page with no text
        MockMultipartFile file = new MockMultipartFile("file", "empty.pdf", "application/pdf", emptyPdfBytes);

        FileProcessingException ex = assertThrows(FileProcessingException.class,
                () -> textExtractionService.extractTextFromPdf(file));
        assertTrue(ex.getMessage().contains("no readable text"));
    }

    @Test
    @DisplayName("Should throw FileProcessingException for corrupted PDF file")
    void shouldThrowForCorruptPdf() {
        byte[] corruptedBytes = new byte[]{0x25, 0x50, 0x44, 0x46, 0x00, 0x00, 0x00};
        MockMultipartFile file = new MockMultipartFile("file", "corrupt.pdf", "application/pdf", corruptedBytes);

        assertThrows(FileProcessingException.class, () -> textExtractionService.extractTextFromPdf(file));
    }

    // --- Helper utilities to create in-memory documents for testing ---

    private byte[] createSamplePdf(String content) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            if (content != null && !content.trim().isEmpty()) {
                try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                    stream.beginText();
                    stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                    stream.newLineAtOffset(50, 700);
                    stream.showText(content);
                    stream.endText();
                }
            }

            document.save(out);
            return out.toByteArray();
        }
    }

    private byte[] createSampleDocx(String content) throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(content);

            document.write(out);
            return out.toByteArray();
        }
    }
}
