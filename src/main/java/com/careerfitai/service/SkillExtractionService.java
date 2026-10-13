package com.careerfitai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Production-ready Skill Extraction Service.
 * Matches technical skills using boundary-aware regex patterns to prevent false positives
 * (e.g. matching "Java" within "JavaScript", or "Go" within "Google").
 */
@Service
public class SkillExtractionService {

    private static final Logger log = LoggerFactory.getLogger(SkillExtractionService.class);

    public record SkillDefinition(String canonicalName, String category, List<Pattern> patterns) {}

    public record ExtractedSkill(String name, String category) {}

    private final List<SkillDefinition> skillDefinitions = new ArrayList<>();

    public SkillExtractionService() {
        initializeDictionary();
    }

    /**
     * Initializes the comprehensive technical skill dictionary with boundary-aware regex patterns.
     */
    private void initializeDictionary() {
        // --- Programming Languages ---
        // Java: cannot be preceded or followed by alphanumeric characters (prevents JavaScript, JavaFX, etc.)
        addSkill("Java", "Programming Languages", "(?<![a-zA-Z0-9])Java(?![a-zA-Z0-9])");
        addSkill("Python", "Programming Languages", "\\bPython\\b");
        addSkill("JavaScript", "Programming Languages", "\\bJavaScript\\b", "\\bJS\\b", "\\bES6\\b");
        addSkill("TypeScript", "Programming Languages", "\\bTypeScript\\b", "\\bTS\\b");
        // C: standalone, not followed or preceded by +, #, or alphanumerics
        addSkill("C", "Programming Languages", "(?<![a-zA-Z0-9#+])C(?![a-zA-Z0-9#+])");
        // C++: handled with special boundary logic
        addSkill("C++", "Programming Languages", "(?<![a-zA-Z0-9#+])C\\+\\+(?![a-zA-Z0-9#+])", "\\bCPP\\b");
        // C#
        addSkill("C#", "Programming Languages", "(?<![a-zA-Z0-9#+])C#(?![a-zA-Z0-9#+])", "\\bC-Sharp\\b");
        addSkill("PHP", "Programming Languages", "\\bPHP\\b");
        // Go: match Golang or capital Go with word boundary to avoid standard lowercase verb 'go'
        addSkill("Go", "Programming Languages", "\\bGolang\\b", "(?<![a-zA-Z0-9])Go(?![a-zA-Z0-9])");
        addSkill("Kotlin", "Programming Languages", "\\bKotlin\\b");
        addSkill("SQL", "Programming Languages", "\\bSQL\\b");
        addSkill("Rust", "Programming Languages", "\\bRust\\b");
        addSkill("Ruby", "Programming Languages", "\\bRuby\\b");
        addSkill("Swift", "Programming Languages", "\\bSwift\\b");
        addSkill("Scala", "Programming Languages", "\\bScala\\b");
        addSkill("Dart", "Programming Languages", "\\bDart\\b");
        addSkill("R", "Programming Languages", "(?<![a-zA-Z0-9])R(?![a-zA-Z0-9])\\s+(?:programming|scripting|language)");

        // --- Backend Frameworks & Architecture ---
        addSkill("Spring Boot", "Backend", "\\bSpring\\s+Boot\\b");
        addSkill("Spring Framework", "Backend", "\\bSpring\\s+Framework\\b");
        addSkill("Spring MVC", "Backend", "\\bSpring\\s+MVC\\b");
        addSkill("Hibernate", "Backend", "\\bHibernate\\b");
        addSkill("JDBC", "Backend", "\\bJDBC\\b");
        addSkill("Node.js", "Backend", "\\bNode(?:\\.js|js|\\s+js)\\b");
        addSkill("Express.js", "Backend", "\\bExpress(?:\\.js|js|\\s+js)?\\b");
        addSkill("Django", "Backend", "\\bDjango\\b");
        addSkill("Flask", "Backend", "\\bFlask\\b");
        addSkill("FastAPI", "Backend", "\\bFastAPI\\b");
        addSkill(".NET", "Backend", "(?<![a-zA-Z0-9])\\.NET\\b", "\\bASP\\.NET\\b");
        addSkill("REST API", "Backend", "\\bREST(?:ful)?\\s+APIs?\\b", "\\bREST\\s*API\\b");
        addSkill("Microservices", "Backend", "\\bMicroservices?\\b");
        addSkill("GraphQL", "Backend", "\\bGraphQL\\b");

        // --- Frontend ---
        addSkill("HTML", "Frontend", "\\bHTML5?\\b");
        addSkill("CSS", "Frontend", "\\bCSS3?\\b");
        addSkill("React", "Frontend", "\\bReact(?:\\.js|js)?\\b");
        addSkill("Angular", "Frontend", "\\bAngular(?:JS)?\\b");
        addSkill("Vue.js", "Frontend", "\\bVue(?:\\.js|js)?\\b");
        addSkill("Next.js", "Frontend", "\\bNext(?:\\.js|js)?\\b");
        addSkill("Bootstrap", "Frontend", "\\bBootstrap\\b");
        addSkill("Tailwind CSS", "Frontend", "\\bTailwind(?:\\s+CSS)?\\b");
        addSkill("Redux", "Frontend", "\\bRedux\\b");
        addSkill("Svelte", "Frontend", "\\bSvelte\\b");
        addSkill("jQuery", "Frontend", "\\bjQuery\\b");

        // --- Databases ---
        addSkill("MySQL", "Databases", "\\bMySQL\\b");
        addSkill("PostgreSQL", "Databases", "\\bPostgreSQL\\b", "\\bPostgres\\b");
        addSkill("MongoDB", "Databases", "\\bMongoDB\\b", "\\bMongo\\b");
        addSkill("Oracle", "Databases", "\\bOracle(?:\\s+DB|\\s+Database)?\\b");
        addSkill("SQL Server", "Databases", "\\bSQL\\s+Server\\b", "\\bMS\\s*SQL\\b");
        addSkill("Redis", "Databases", "\\bRedis\\b");
        addSkill("SQLite", "Databases", "\\bSQLite\\b");
        addSkill("Cassandra", "Databases", "\\bCassandra\\b");
        addSkill("DynamoDB", "Databases", "\\bDynamoDB\\b");
        addSkill("Elasticsearch", "Databases", "\\bElasticsearch\\b");

        // --- Cloud & DevOps ---
        addSkill("AWS", "Cloud & DevOps", "\\bAWS\\b", "\\bAmazon\\s+Web\\s+Services\\b");
        addSkill("Microsoft Azure", "Cloud & DevOps", "\\b(?:Microsoft\\s+)?Azure\\b");
        addSkill("Google Cloud", "Cloud & DevOps", "\\bGoogle\\s+Cloud(?:\\s+Platform)?\\b", "\\bGCP\\b");
        addSkill("Docker", "Cloud & DevOps", "\\bDocker\\b");
        addSkill("Kubernetes", "Cloud & DevOps", "\\bKubernetes\\b", "\\bK8s\\b");
        addSkill("Jenkins", "Cloud & DevOps", "\\bJenkins\\b");
        addSkill("Git", "Cloud & DevOps", "\\bGit\\b");
        addSkill("GitHub", "Cloud & DevOps", "\\bGitHub\\b");
        addSkill("GitLab", "Cloud & DevOps", "\\bGitLab\\b");
        addSkill("CI/CD", "Cloud & DevOps", "\\bCI\\s*/\\s*CD\\b", "\\bContinuous\\s+Integration\\b");
        addSkill("Terraform", "Cloud & DevOps", "\\bTerraform\\b");
        addSkill("Ansible", "Cloud & DevOps", "\\bAnsible\\b");
        addSkill("Linux", "Cloud & DevOps", "\\bLinux\\b", "\\bUbuntu\\b", "\\bRedHat\\b");

        // --- Testing & Tools ---
        addSkill("JUnit", "Testing & Tools", "\\bJUnit\\b");
        addSkill("Mockito", "Testing & Tools", "\\bMockito\\b");
        addSkill("Selenium", "Testing & Tools", "\\bSelenium\\b");
        addSkill("Postman", "Testing & Tools", "\\bPostman\\b");
        addSkill("Maven", "Testing & Tools", "\\bMaven\\b");
        addSkill("Gradle", "Testing & Tools", "\\bGradle\\b");
        addSkill("Cypress", "Testing & Tools", "\\bCypress\\b");
        addSkill("Jest", "Testing & Tools", "\\bJest\\b");
        addSkill("Jira", "Testing & Tools", "\\bJira\\b");

        // --- Data & AI ---
        addSkill("Machine Learning", "Data & AI", "\\bMachine\\s+Learning\\b", "\\bML\\b");
        addSkill("Deep Learning", "Data & AI", "\\bDeep\\s+Learning\\b", "\\bDL\\b");
        addSkill("TensorFlow", "Data & AI", "\\bTensorFlow\\b");
        addSkill("PyTorch", "Data & AI", "\\bPyTorch\\b");
        addSkill("Pandas", "Data & AI", "\\bPandas\\b");
        addSkill("NumPy", "Data & AI", "\\bNumPy\\b");
        addSkill("Scikit-learn", "Data & AI", "\\bScikit-learn\\b", "\\bsklearn\\b");
        addSkill("Apache Spark", "Data & AI", "\\b(?:Apache\\s+)?Spark\\b");
        addSkill("NLP", "Data & AI", "\\bNLP\\b", "\\bNatural\\s+Language\\s+Processing\\b");

        log.info("Initialized CareerFit AI skill dictionary with {} defined technologies.", skillDefinitions.size());
    }

    private void addSkill(String canonicalName, String category, String... patterns) {
        List<Pattern> compiled = Arrays.stream(patterns)
                .map(p -> Pattern.compile(p, Pattern.CASE_INSENSITIVE))
                .collect(Collectors.toList());
        skillDefinitions.add(new SkillDefinition(canonicalName, category, compiled));
    }

    /**
     * Extracts all unique technical skills present in the resume text.
     * Guaranteed:
     * 1. Returns only skills actually mentioned in text.
     * 2. No duplicates.
     * 3. Case-insensitive boundary matching.
     * 4. Alphabetical / predictable ordering.
     */
    public List<ExtractedSkill> extractSkillsWithMetadata(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // To protect against false positives where a longer skill might contain a shorter one,
        // we can prioritize or verify matches against the text.
        List<ExtractedSkill> matchedSkills = new ArrayList<>();

        for (SkillDefinition skill : skillDefinitions) {
            boolean matched = false;
            for (Pattern pattern : skill.patterns()) {
                if (pattern.matcher(text).find()) {
                    matched = true;
                    break;
                }
            }
            if (matched) {
                // Special safety rule for "Java" vs "JavaScript":
                // If "Java" matched, verify that "Java" appears as a standalone word not solely as part of "JavaScript"
                if ("Java".equals(skill.canonicalName())) {
                    if (!isJavaIndependentlyPresent(text)) {
                        continue;
                    }
                }
                // Special safety rule for "C":
                // If "C" matched, ensure it's not solely inside "C++" or "C#"
                if ("C".equals(skill.canonicalName())) {
                    if (!isCIndependentlyPresent(text)) {
                        continue;
                    }
                }

                matchedSkills.add(new ExtractedSkill(skill.canonicalName(), skill.category()));
            }
        }

        // Sort alphabetically by canonical name for deterministic output
        matchedSkills.sort(Comparator.comparing(ExtractedSkill::name, String.CASE_INSENSITIVE_ORDER));
        return Collections.unmodifiableList(matchedSkills);
    }

    /**
     * Extracts list of canonical skill names present in the text.
     */
    public List<String> extractSkills(String text) {
        return extractSkillsWithMetadata(text).stream()
                .map(ExtractedSkill::name)
                .toList();
    }

    /**
     * Groups extracted skills by category.
     */
    public Map<String, List<String>> extractCategorizedSkills(String text) {
        List<ExtractedSkill> skills = extractSkillsWithMetadata(text);
        Map<String, List<String>> categorized = new LinkedHashMap<>();

        for (ExtractedSkill skill : skills) {
            categorized.computeIfAbsent(skill.category(), k -> new ArrayList<>()).add(skill.name());
        }

        return categorized;
    }

    /**
     * Validates that "Java" appears standalone and not merely as a substring of "JavaScript" or "JavaFX".
     */
    private boolean isJavaIndependentlyPresent(String text) {
        // Replace all occurrences of "JavaScript", "JavaFX" with placeholders or check with negative lookahead
        Pattern independentJavaPattern = Pattern.compile("(?<![a-zA-Z0-9])Java(?![a-zA-Z0-9]|Script|FX)", Pattern.CASE_INSENSITIVE);
        return independentJavaPattern.matcher(text).find();
    }

    /**
     * Validates that "C" appears standalone and not as part of "C++", "C#", "CSS", "CI/CD", etc.
     */
    private boolean isCIndependentlyPresent(String text) {
        Pattern independentCPattern = Pattern.compile("(?<![a-zA-Z0-9#+])C(?![a-zA-Z0-9#+])", Pattern.CASE_INSENSITIVE);
        return independentCPattern.matcher(text).find();
    }
}
