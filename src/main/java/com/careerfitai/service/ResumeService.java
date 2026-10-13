package com.careerfitai.service;

import com.careerfitai.dto.ResumeResponseDto;
import com.careerfitai.entity.Resume;
import com.careerfitai.entity.ResumeSkill;
import com.careerfitai.exception.ResourceNotFoundException;
import com.careerfitai.repository.ResumeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class ResumeService {

    private static final Logger log = LoggerFactory.getLogger(ResumeService.class);

    private final FileValidationService fileValidationService;
    private final TextExtractionService textExtractionService;
    private final SkillExtractionService skillExtractionService;
    private final ResumeRepository resumeRepository;

    public ResumeService(FileValidationService fileValidationService,
                         TextExtractionService textExtractionService,
                         SkillExtractionService skillExtractionService,
                         ResumeRepository resumeRepository) {
        this.fileValidationService = fileValidationService;
        this.textExtractionService = textExtractionService;
        this.skillExtractionService = skillExtractionService;
        this.resumeRepository = resumeRepository;
    }

    /**
     * Validates file, extracts text, identifies technical skills, persists analysis to MySQL,
     * and returns the result DTO.
     */
    @Transactional
    public ResumeResponseDto processAndSaveResume(MultipartFile file) {
        log.info("Processing resume upload: filename={}, size={} bytes",
                file.getOriginalFilename(), file.getSize());

        // Step 1: Validate file format, size, and magic bytes
        String fileType = fileValidationService.validateFile(file);

        // Step 2: Extract text from PDF or DOCX
        String extractedText = textExtractionService.extractText(file, fileType);

        // Step 3: Extract technical skills
        List<SkillExtractionService.ExtractedSkill> extractedSkills =
                skillExtractionService.extractSkillsWithMetadata(extractedText);

        log.info("Successfully extracted {} skills from resume '{}'",
                extractedSkills.size(), file.getOriginalFilename());

        // Step 4: Persist to MySQL database
        Resume resume = new Resume(file.getOriginalFilename(), fileType, file.getSize(), extractedText);
        for (SkillExtractionService.ExtractedSkill skill : extractedSkills) {
            resume.addSkill(new ResumeSkill(skill.name(), skill.category()));
        }

        Resume savedResume = resumeRepository.save(resume);
        log.info("Persisted resume analysis with ID: {}", savedResume.getId());

        // Step 5: Convert to DTO
        return mapToDto(savedResume);
    }

    /**
     * Retrieves a saved resume analysis by ID from MySQL.
     */
    @Transactional(readOnly = true)
    public ResumeResponseDto getResumeById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid resume ID supplied: " + id);
        }

        Resume resume = resumeRepository.findByIdWithSkills(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume analysis with ID " + id + " was not found."));

        return mapToDto(resume);
    }

    /**
     * Maps a Resume entity to the user-facing ResumeResponseDto.
     * Sanitizes response by not exposing the raw resume text.
     */
    private ResumeResponseDto mapToDto(Resume resume) {
        List<String> skillNames = new ArrayList<>();
        Map<String, List<String>> categorizedSkills = new LinkedHashMap<>();

        if (resume.getSkills() != null) {
            for (ResumeSkill skill : resume.getSkills()) {
                skillNames.add(skill.getSkillName());
                String category = skill.getCategory() != null ? skill.getCategory() : "General";
                categorizedSkills.computeIfAbsent(category, k -> new ArrayList<>()).add(skill.getSkillName());
            }
        }

        // Sort skill names
        skillNames.sort(String.CASE_INSENSITIVE_ORDER);

        return new ResumeResponseDto(
                resume.getId(),
                resume.getOriginalFilename(),
                resume.getFileType(),
                skillNames,
                skillNames.size(),
                resume.getCreatedAt(),
                categorizedSkills
        );
    }
}
