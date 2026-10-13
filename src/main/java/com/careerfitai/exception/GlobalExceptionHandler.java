package com.careerfitai.exception;

import com.careerfitai.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EmptyFileException.class)
    public ResponseEntity<ErrorResponseDto> handleEmptyFile(EmptyFileException ex, HttpServletRequest request) {
        log.warn("Empty file upload attempt: {}", ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                ex.getMessage(),
                "EMPTY_FILE",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidFile(InvalidFileException ex, HttpServletRequest request) {
        log.warn("Invalid file upload attempt: {}", ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                ex.getMessage(),
                "INVALID_FILE_TYPE",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                ex.getMessage(),
                "RESOURCE_NOT_FOUND",
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<ErrorResponseDto> handleFileProcessing(FileProcessingException ex, HttpServletRequest request) {
        log.error("File processing failed: {}", ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                ex.getMessage(),
                "FILE_PROCESSING_ERROR",
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDto> handleMaxSizeExceeded(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.warn("Max upload size exceeded: {}", ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                "Uploaded file exceeds the maximum allowed limit of 5 MB. Please upload a smaller file.",
                "PAYLOAD_TOO_LARGE",
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        log.warn("Unsupported media type: {}", ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                "Content-Type is unsupported. Please upload a multipart/form-data request with a PDF or DOCX file.",
                "UNSUPPORTED_MEDIA_TYPE",
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Illegal argument: {}", ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                ex.getMessage(),
                "BAD_REQUEST",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation error: {}", ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                "Validation failed for request parameters.",
                "VALIDATION_ERROR",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception caught while processing {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorResponseDto error = new ErrorResponseDto(
                "An unexpected server error occurred while processing your request. Please try again later.",
                "INTERNAL_SERVER_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
