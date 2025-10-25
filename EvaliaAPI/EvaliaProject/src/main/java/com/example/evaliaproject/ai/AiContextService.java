package com.example.evaliaproject.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class AiContextService {

    private String systemPrompt =
        "Tu es l’assistant d’Evalia. Réponds en français, clairement et brièvement (2–4 phrases). " +
        "Reste strictement dans le périmètre fonctionnel d’Evalia. " +
        "N’utilise pas de mise en forme Markdown ni de caractères spéciaux (*, **, #, _).";

    private final List<Map<String, String>> examples = new ArrayList<>();

    public AiContextService() {
        // ✅ Load directly from /mnt/hdd/Downloads/evalia_context_description.jsonl
        loadJsonMessages("/mnt/hdd/Downloads/evalia_context_description.jsonl");
    }

    private void loadJsonMessages(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                System.err.println("⚠️ AI context file not found at " + path);
                return;
            }

            String json = new String(java.nio.file.Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
            ObjectMapper om = new ObjectMapper();
            Map<String, Object> root = om.readValue(json, new TypeReference<>() {});

            @SuppressWarnings("unchecked")
            List<Map<String, String>> msgs = (List<Map<String, String>>) root.get("messages");

            if (msgs != null && !msgs.isEmpty()) {
                msgs.stream()
                    .filter(m -> "system".equalsIgnoreCase(m.get("role")))
                    .map(m -> m.getOrDefault("content", "").trim())
                    .findFirst()
                    .ifPresent(s -> systemPrompt = s);

                for (Map<String, String> m : msgs) {
                    String role = m.getOrDefault("role", "");
                    String content = m.getOrDefault("content", "").trim();
                    if (!role.isBlank() && !content.isBlank() && !"system".equalsIgnoreCase(role)) {
                        examples.add(Map.of("role", role, "content", content));
                    }
                }
            }

            System.out.println("✅ AI context loaded: " + examples.size() + " example messages");

        } catch (Exception e) {
            System.err.println("❌ Error loading Evalia context: " + e.getMessage());
        }
    }

    public String systemPrompt() {
        return systemPrompt;
    }

    public List<Map<String, String>> fewShotExamples() {
        return Collections.unmodifiableList(examples);
    }
}

