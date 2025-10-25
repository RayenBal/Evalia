package com.example.evaliaproject.ai;

import com.example.evaliaproject.ai.ChatDtos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ChatController {

    private final WebClient mistral;
    private final AiContextService aiContext;

    @Value("${mistral.model:mistral-large-latest}")
    private String model;

    public ChatController(WebClient mistralClient, AiContextService aiContextService) {
        this.mistral = mistralClient;
        this.aiContext = aiContextService;
    }

    @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatAnswer> chat(@RequestBody ChatRequest req, Authentication authentication) {
        List<Map<String, String>> msgs = new ArrayList<>();

        // ✅ System + context examples
        msgs.add(Map.of("role", "system", "content", aiContext.systemPrompt()));
        msgs.addAll(aiContext.fewShotExamples());

        if (req.messages != null) {
            for (ChatMessage m : req.messages) {
                msgs.add(Map.of("role", m.role, "content", m.content));
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", msgs);
        body.put("temperature", 0.4);
        body.put("top_p", 0.9);
        body.put("max_tokens", 200);

        try {
            Map<String, Object> raw = mistral.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            ChatAnswer answer = new ChatAnswer();
            Map<String, Object> choice = ((List<Map<String, Object>>) raw.get("choices")).get(0);
            Map<String, Object> msg = (Map<String, Object>) choice.get("message");

            answer.content = clean((String) msg.get("content"));
            answer.model = model;
            answer.finishReason = (String) choice.get("finish_reason");

            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            ChatAnswer err = new ChatAnswer();
            err.content = "Désolé, une erreur est survenue. Réessayez plus tard.";
            return ResponseEntity.internalServerError().body(err);
        }
    }

    private String clean(String text) {
        if (text == null) return "";
        return text.replaceAll("[*_#`~<>]", "")
                   .replaceAll("\\s{2,}", " ")
                   .trim();
    }
}

