package com.careerfitai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkillExtractionServiceTest {

    private SkillExtractionService skillExtractionService;

    @BeforeEach
    void setUp() {
        skillExtractionService = new SkillExtractionService();
    }

    @Test
    @DisplayName("Should detect multiple technical skills from resume text")
    void shouldDetectMultipleSkills() {
        String text = """
                Senior Full Stack Engineer with 5 years experience building scalable web applications.
                Core stack: Java, Spring Boot, MySQL, React, Docker, and AWS.
                Experienced in building REST APIs and microservices architectures with JUnit testing.
                """;

        List<String> skills = skillExtractionService.extractSkills(text);

        assertNotNull(skills);
        assertTrue(skills.contains("Java"));
        assertTrue(skills.contains("Spring Boot"));
        assertTrue(skills.contains("MySQL"));
        assertTrue(skills.contains("React"));
        assertTrue(skills.contains("Docker"));
        assertTrue(skills.contains("AWS"));
        assertTrue(skills.contains("REST API"));
        assertTrue(skills.contains("Microservices"));
        assertTrue(skills.contains("JUnit"));
    }

    @Test
    @DisplayName("Should detect skills case-insensitively")
    void shouldDetectSkillsCaseInsensitively() {
        String text = "Hands-on experience with python, DOCKER, kubernetes, and poSTGResQL.";

        List<String> skills = skillExtractionService.extractSkills(text);

        assertTrue(skills.contains("Python"));
        assertTrue(skills.contains("Docker"));
        assertTrue(skills.contains("Kubernetes"));
        assertTrue(skills.contains("PostgreSQL"));
    }

    @Test
    @DisplayName("Should remove duplicates when skill appears multiple times")
    void shouldRemoveDuplicates() {
        String text = "I love Java. Java is great for enterprise development. Writing Java code daily.";

        List<String> skills = skillExtractionService.extractSkills(text);

        assertEquals(1, skills.stream().filter(s -> s.equalsIgnoreCase("Java")).count());
    }

    @Test
    @DisplayName("Should NOT detect Java when only JavaScript is present (Prevent false positives)")
    void shouldNotDetectJavaWhenOnlyJavaScriptPresent() {
        String text = """
                Frontend Developer experienced in JavaScript, TypeScript, HTML, and CSS.
                Built interactive single page apps with JavaScript and React.
                """;

        List<String> skills = skillExtractionService.extractSkills(text);

        assertTrue(skills.contains("JavaScript"), "JavaScript should be detected");
        assertFalse(skills.contains("Java"), "Java MUST NOT be detected when only JavaScript is present");
    }

    @Test
    @DisplayName("Should detect both Java and JavaScript when both are genuinely present")
    void shouldDetectBothJavaAndJavaScriptWhenBothPresent() {
        String text = "Full Stack developer working with Java backend and JavaScript frontend.";

        List<String> skills = skillExtractionService.extractSkills(text);

        assertTrue(skills.contains("Java"));
        assertTrue(skills.contains("JavaScript"));
    }

    @Test
    @DisplayName("Should NOT detect C when only C++ or C# or CSS is present")
    void shouldNotDetectCWhenOnlyCppOrCSharpOrCssPresent() {
        String text = "Skills: C++, C#, CSS, and HTML.";

        List<String> skills = skillExtractionService.extractSkills(text);

        assertTrue(skills.contains("C++"));
        assertTrue(skills.contains("C#"));
        assertTrue(skills.contains("CSS"));
        assertFalse(skills.contains("C"), "Standalone C should not be detected from C++ or C#");
    }

    @Test
    @DisplayName("Should detect standalone C when explicitly listed")
    void shouldDetectStandaloneC() {
        String text = "Languages: C, Python, and Go.";

        List<String> skills = skillExtractionService.extractSkills(text);

        assertTrue(skills.contains("C"));
        assertTrue(skills.contains("Python"));
        assertTrue(skills.contains("Go"));
    }

    @Test
    @DisplayName("Should return empty list when resume has no known technical skills")
    void shouldReturnEmptyWhenNoTechnicalSkillsFound() {
        String text = """
                Experienced office administrator specializing in customer relations,
                scheduling appointments, handling incoming telephone calls, and filing paperwork.
                """;

        List<String> skills = skillExtractionService.extractSkills(text);

        assertNotNull(skills);
        assertTrue(skills.isEmpty(), "No technical skills should be detected");
    }

    @Test
    @DisplayName("Should support common aliases like Postgres, K8s, and GCP")
    void shouldSupportCommonAliases() {
        String text = "Experienced managing deployments with K8s on GCP, connecting to Postgres.";

        List<String> skills = skillExtractionService.extractSkills(text);

        assertTrue(skills.contains("Kubernetes"));
        assertTrue(skills.contains("Google Cloud"));
        assertTrue(skills.contains("PostgreSQL"));
    }

    @Test
    @DisplayName("Should return skills in predictable sorted order")
    void shouldReturnSkillsSorted() {
        String text = "Python, Java, Docker, AWS, React, C++";

        List<String> skills = skillExtractionService.extractSkills(text);

        for (int i = 0; i < skills.size() - 1; i++) {
            assertTrue(skills.get(i).compareToIgnoreCase(skills.get(i + 1)) <= 0);
        }
    }
}
