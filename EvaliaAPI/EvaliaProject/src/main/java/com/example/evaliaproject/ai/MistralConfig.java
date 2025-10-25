package com.example.evaliaproject.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MistralConfig {

  @Value("${mistral.api.base:https://api.mistral.ai}")
  private String baseUrl;

  @Value("${mistral.api.key}")
  private String apiKey;

  @Bean
  public WebClient mistralClient() {

    // === 🧠 Startup Logging ===
    System.out.println("\n===============================");
    System.out.println("🚀 MISTRAL AI CONFIGURATION LOADED");
    System.out.println("-------------------------------");
    if (apiKey != null && apiKey.length() > 8) {
      System.out.println("🔑 API Key Prefix: " + apiKey.substring(0, 6) + "******");
    } else {
      System.out.println("⚠️  API Key is missing or invalid!");
    }
    System.out.println("🌐 Base URL: " + baseUrl);
    System.out.println("===============================\n");

    // === Build WebClient ===
    return WebClient.builder()
      .baseUrl(baseUrl)
      .defaultHeader("Authorization", "Bearer " + apiKey)
      .defaultHeader("Content-Type", "application/json")
      .build();
  }
}
