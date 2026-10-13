package com.careerfitai.service;

import com.careerfitai.exception.EmptyFileException;
import com.careerfitai.exception.InvalidFileException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Service
public class FileValidationService {

    public static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB
    public static final String TYPE_PDF = "PDF";
    public static final String TYPE_DOCX = "DOCX";

    // Magic bytes for PDF: %PDF- (0x25, 0x50, 0x44, 0x46, 0x2D)
    private static final byte[] PDF_MAGIC_BYTES = new byte[]{0x25, 0x50, 0x44, 0x46};

    // Magic bytes for DOCX (ZIP archive): PK\x03\x04 (0x50, 0x4B, 0x03, 0x04)
    private static final byte[] ZIP_MAGIC_BYTES = new byte[]{0x50, 0x4B, 0x03, 0x04};

    /**
     * Validates the uploaded file for existence, size, extension, and binary format integrity.
     * Returns canonical file type ("PDF" or "DOCX").
     */
    public String validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmptyFileException("No file provided. Please select a valid PDF or DOCX resume to upload.");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new InvalidFileException("The uploaded file exceeds the 5 MB size limit. Current size: "
                    + String.format("%.2f MB", file.getSize() / (1024.0 * 1024.0)));
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new InvalidFileException("Uploaded file name is invalid.");
        }

        String lowerName = originalFilename.trim().toLowerCase();
        String detectedType;

        if (lowerName.endsWith(".pdf")) {
            detectedType = TYPE_PDF;
        } else if (lowerName.endsWith(".docx")) {
            detectedType = TYPE_DOCX;
        } else {
            String extension = lowerName.contains(".") ? lowerName.substring(lowerName.lastIndexOf(".")) : "unknown";
            throw new InvalidFileException("Unsupported file format (" + extension + "). CareerFit AI only accepts PDF (.pdf) and Word (.docx) documents.");
        }

        // Verify magic bytes
        validateMagicBytes(file, detectedType);

        return detectedType;
    }

    private void validateMagicBytes(MultipartFile file, String expectedType) {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[4];
            int read = is.read(header);
            if (read < 4) {
                throw new InvalidFileException("File content is corrupted or unreadable.");
            }

            if (TYPE_PDF.equals(expectedType)) {
                if (!Arrays.equals(header, PDF_MAGIC_BYTES)) {
                    throw new InvalidFileException("The uploaded file has a .pdf extension but does not contain valid PDF content.");
                }
            } else if (TYPE_DOCX.equals(expectedType)) {
                if (!Arrays.equals(header, ZIP_MAGIC_BYTES)) {
                    throw new InvalidFileException("The uploaded file has a .docx extension but does not contain a valid DOCX package.");
                }
            }
        } catch (IOException e) {
            throw new InvalidFileException("Failed to read and verify uploaded file headers: " + e.getMessage());
        }
    }
}
