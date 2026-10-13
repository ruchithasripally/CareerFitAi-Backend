package com.careerfitai.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SampleFileGenerator {

    @Test
    void generateSampleResumes() throws IOException {
        File samplesDir = new File("samples");
        if (!samplesDir.exists()) {
            samplesDir.mkdirs();
        }

        // Generate sample PDF: Alex Morgan (Full Stack Java Engineer)
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                cs.newLineAtOffset(50, 750);
                cs.showText("Alex Morgan - Senior Full Stack Software Engineer");

                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
                cs.newLineAtOffset(0, -30);
                cs.showText("Email: alex.morgan@example.com | GitHub: github.com/alexmorgan");

                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 13);
                cs.newLineAtOffset(0, -30);
                cs.showText("TECHNICAL SKILLS");

                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
                cs.newLineAtOffset(0, -20);
                cs.showText("Languages: Java, Python, TypeScript, SQL, C++");

                cs.newLineAtOffset(0, -20);
                cs.showText("Backend Frameworks: Spring Boot, Spring Framework, Hibernate, REST API, Microservices");

                cs.newLineAtOffset(0, -20);
                cs.showText("Frontend Technologies: React, HTML, CSS, Tailwind CSS");

                cs.newLineAtOffset(0, -20);
                cs.showText("Databases: MySQL, PostgreSQL, Redis, MongoDB");

                cs.newLineAtOffset(0, -20);
                cs.showText("Cloud & DevOps: Docker, Kubernetes, AWS, Git, GitHub, CI/CD, Linux");

                cs.newLineAtOffset(0, -20);
                cs.showText("Testing & Build: JUnit, Mockito, Maven, Postman");

                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 13);
                cs.newLineAtOffset(0, -30);
                cs.showText("PROFESSIONAL EXPERIENCE");

                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
                cs.newLineAtOffset(0, -20);
                cs.showText("Senior Software Engineer at CloudScale Solutions (2021 - Present)");
                cs.newLineAtOffset(0, -15);
                cs.showText("Architected microservices using Spring Boot and Docker, deployed on AWS with Kubernetes.");
                cs.newLineAtOffset(0, -15);
                cs.showText("Integrated MySQL and Redis cache for low-latency queries across REST API endpoints.");

                cs.endText();
            }
            doc.save(new File(samplesDir, "sample_resume_alex_morgan.pdf"));
        }

        // Generate sample DOCX: Jordan Taylor (DevOps & Cloud Engineer)
        try (XWPFDocument docx = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(new File(samplesDir, "sample_resume_jordan_taylor.docx"))) {

            XWPFParagraph title = docx.createParagraph();
            XWPFRun r1 = title.createRun();
            r1.setBold(true);
            r1.setFontSize(16);
            r1.setText("Jordan Taylor — DevOps & Cloud Platform Specialist");

            XWPFParagraph contact = docx.createParagraph();
            XWPFRun r2 = contact.createRun();
            r2.setFontSize(10);
            r2.setText("San Francisco, CA | jordan.taylor@example.com | linkedin.com/in/jordantaylor");

            XWPFParagraph skillsHeader = docx.createParagraph();
            XWPFRun r3 = skillsHeader.createRun();
            r3.setBold(true);
            r3.setFontSize(13);
            r3.setText("CORE COMPETENCIES & TECHNICAL EXPERTISE");

            XWPFParagraph skillsBody = docx.createParagraph();
            XWPFRun r4 = skillsBody.createRun();
            r4.setText("Cloud Platforms: AWS, Google Cloud, Microsoft Azure\n" +
                    "Containerization & Orchestration: Docker, Kubernetes, Helm\n" +
                    "Infrastructure as Code & CI/CD: Terraform, Ansible, Jenkins, GitLab, GitHub, CI/CD\n" +
                    "Programming & Scripting: Python, Go, Bash, Linux\n" +
                    "Databases & Observability: PostgreSQL, MongoDB, Elasticsearch\n" +
                    "Monitoring & Version Control: Git, Jira");

            docx.write(out);
        }
    }
}
