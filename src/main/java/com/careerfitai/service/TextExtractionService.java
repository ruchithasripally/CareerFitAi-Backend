package com.careerfitai.service;

import com.careerfitai.exception.FileProcessingException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class TextExtractionService {

    private static final Logger log = LoggerFactory.getLogger(TextExtractionService.class);

    /**
     * Extracts readable text from an uploaded resume file (PDF or DOCX).
     */
    public String extractText(MultipartFile file, String fileType) {
        if (FileValidationService.TYPE_PDF.equalsIgnoreCase(fileType)) {
            return extractTextFromPdf(file);
        } else if (FileValidationService.TYPE_DOCX.equalsIgnoreCase(fileType)) {
            return extractTextFromDocx(file);
        } else {
            throw new FileProcessingException("Unsupported file type for text extraction: " + fileType);
        }
    }

    /**
     * Extracts text from a PDF file using Apache PDFBox 3.x.
     */
    public String extractTextFromPdf(MultipartFile file) {
        log.info("Extracting text from PDF file: {}", file.getOriginalFilename());
        try (InputStream inputStream = file.getInputStream();
             RandomAccessReadBuffer buffer = new RandomAccessReadBuffer(inputStream);
             PDDocument document = Loader.loadPDF(buffer)) {

            if (document.isEncrypted()) {
                throw new FileProcessingException("The uploaded PDF is password-protected and cannot be analyzed. Please upload an unprotected document.");
            }

            PDFTextStripper textStripper = new PDFTextStripper();
            textStripper.setSortByPosition(true);
            String extractedText = textStripper.getText(document);

            if (extractedText == null || extractedText.trim().isEmpty()) {
                throw new FileProcessingException("The uploaded PDF contains no readable text. Scanned or image-only documents without a text/OCR layer are not supported.");
            }

            log.info("Extracted {} characters from PDF: {}", extractedText.length(), file.getOriginalFilename());
            return extractedText.trim();

        } catch (FileProcessingException e) {
            throw e;
        } catch (IOException e) {
            log.error("Failed to extract text from PDF: {}", e.getMessage(), e);
            throw new FileProcessingException("Corrupted or unreadable PDF document. Please ensure the file is a valid PDF.", e);
        } catch (Exception e) {
            log.error("Unexpected error during PDF processing: {}", e.getMessage(), e);
            throw new FileProcessingException("Failed to process the PDF document: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts text from a DOCX file using Apache POI.
     */
    public String extractTextFromDocx(MultipartFile file) {
        log.info("Extracting text from DOCX file: {}", file.getOriginalFilename());
        try (InputStream inputStream = file.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            String extractedText = extractor.getText();

            if (extractedText == null || extractedText.trim().isEmpty()) {
                throw new FileProcessingException("The uploaded DOCX document contains no readable text.");
            }

            log.info("Extracted {} characters from DOCX: {}", extractedText.length(), file.getOriginalFilename());
            return extractedText.trim();

        } catch (FileProcessingException e) {
            throw e;
        } catch (IOException e) {
            log.error("Failed to extract text from DOCX: {}", e.getMessage(), e);
            throw new FileProcessingException("Corrupted or unreadable DOCX document. Please ensure the file is a valid Word document.", e);
        } catch (Exception e) {
            log.error("Unexpected error during DOCX processing: {}", e.getMessage(), e);
            throw new FileProcessingException("Failed to process the DOCX document: " + e.getMessage(), e);
        }
    }
}
