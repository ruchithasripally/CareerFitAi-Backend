package com.careerfitai.entity;

import jakarta.persistence.*;

/**
 * Entity representing an individual detected skill linked to a Resume.
 */
@Entity
@Table(name = "resume_skills")
public class ResumeSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(name = "skill_name", nullable = false, length = 100)
    private String skillName;

    @Column(name = "category", length = 100)
    private String category;

    public ResumeSkill() {
    }

    public ResumeSkill(String skillName, String category) {
        this.skillName = skillName;
        this.category = category;
    }

    public ResumeSkill(Resume resume, String skillName, String category) {
        this.resume = resume;
        this.skillName = skillName;
        this.category = category;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
