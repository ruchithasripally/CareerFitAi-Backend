package com.careerfitai.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ResumeResponseDto {

    private Long id;
    private String filename;
    private String fileType;
    private List<String> skills;
    private int skillCount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private Map<String, List<String>> categorizedSkills;

    public ResumeResponseDto() {
    }

    public ResumeResponseDto(Long id, String filename, String fileType, List<String> skills,
                             int skillCount, LocalDateTime createdAt) {
        this.id = id;
        this.filename = filename;
        this.fileType = fileType;
        this.skills = skills;
        this.skillCount = skillCount;
        this.createdAt = createdAt;
    }

    public ResumeResponseDto(Long id, String filename, String fileType, List<String> skills,
                             int skillCount, LocalDateTime createdAt, Map<String, List<String>> categorizedSkills) {
        this.id = id;
        this.filename = filename;
        this.fileType = fileType;
        this.skills = skills;
        this.skillCount = skillCount;
        this.createdAt = createdAt;
        this.categorizedSkills = categorizedSkills;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public int getSkillCount() {
        return skillCount;
    }

    public void setSkillCount(int skillCount) {
        this.skillCount = skillCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, List<String>> getCategorizedSkills() {
        return categorizedSkills;
    }

    public void setCategorizedSkills(Map<String, List<String>> categorizedSkills) {
        this.categorizedSkills = categorizedSkills;
    }
}
