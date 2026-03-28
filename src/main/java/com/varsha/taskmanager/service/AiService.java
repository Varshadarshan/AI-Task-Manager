package com.varsha.taskmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ✅ Generate day-wise learning plan using Groq AI
    public List<String> generateLearningPlan(String title, String description, LocalDate deadline) {

        long totalDays = ChronoUnit.DAYS.between(LocalDate.now(), deadline);
        if (totalDays <= 0) totalDays = 30;

        String prompt = buildPrompt(title, description, totalDays);

        try {
            // Build request body
            Map<String, Object> requestMap = Map.of(
                    "model", "llama-3.3-70b-versatile",
                    "messages", List.of(Map.of(
                            "role", "user",
                            "content", prompt
                    )),
                    "temperature", 0.7,
                    "max_tokens", 2000
            );

            String requestBody = objectMapper.writeValueAsString(requestMap);

            // Debug log
            System.out.println("=== Calling Groq API for: " + title + " (" + totalDays + " days) ===");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + groqApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            System.out.println("=== Groq Response Status: " + response.statusCode() + " ===");
            System.out.println("=== Groq Raw Response: " + response.body().substring(0, Math.min(500, response.body().length())) + " ===");

            if (response.statusCode() != 200) {
                System.out.println("Groq API error, using fallback plan");
                return generateFallbackPlan(title, totalDays);
            }

            // Parse response
            JsonNode root = objectMapper.readTree(response.body());
            String content = root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            System.out.println("=== Groq Content: " + content.substring(0, Math.min(300, content.length())) + " ===");

            List<String> topics = parseTopics(content, totalDays);
            System.out.println("=== Parsed " + topics.size() + " topics ===");
            return topics;

        } catch (Exception e) {
            System.out.println("=== Groq API Exception: " + e.getMessage() + " ===");
            return generateFallbackPlan(title, totalDays);
        }
    }

    // ✅ Build prompt
    private String buildPrompt(String title, String description, long totalDays) {
        return "Create a " + totalDays + "-day learning plan for: " + title + ".\n"
                + "Goal: " + description + "\n\n"
                + "Return ONLY a numbered list. One topic per line. No extra text.\n"
                + "Format exactly like this:\n"
                + "1. Topic name here\n"
                + "2. Topic name here\n"
                + "3. Topic name here\n\n"
                + "Start now:";
    }

    // ✅ Parse numbered list from AI response
    private List<String> parseTopics(String content, long totalDays) {
        List<String> topics = new ArrayList<>();
        String[] lines = content.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Match "1. topic" or "1) topic" or "1 - topic"
            if (line.matches("^\\d+[.)\\-\\s].*")) {
                String topic = line.replaceFirst("^\\d+[.)\\-\\s]+", "").trim();
                if (!topic.isEmpty()) {
                    topics.add(topic);
                }
            }
        }

        System.out.println("=== Topics parsed: " + topics.size() + " ===");

        if (topics.isEmpty()) {
            return generateFallbackPlan("Learning", totalDays);
        }

        // Pad if needed
        while (topics.size() < totalDays) {
            topics.add("Review and Practice - Day " + (topics.size() + 1));
        }

        return topics.subList(0, (int) Math.min(topics.size(), totalDays));
    }

    // ✅ Fallback plan
    private List<String> generateFallbackPlan(String title, long totalDays) {
        List<String> topics = new ArrayList<>();
        String[] genericTopics = {
                "Introduction & Setup", "Core Concepts - Part 1", "Core Concepts - Part 2",
                "Practice Exercises", "Advanced Topics - Part 1", "Advanced Topics - Part 2",
                "Project Work", "Review & Revision", "Mock Tests", "Final Assessment"
        };
        for (int i = 1; i <= totalDays; i++) {
            String topic = genericTopics[(i - 1) % genericTopics.length];
            topics.add(title + ": " + topic);
        }
        return topics;
    }
}