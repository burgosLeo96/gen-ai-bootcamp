package com.epam.training.gen.ai.config.services;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.aiservices.openai.textcompletion.OpenAITextGenerationService;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.textcompletion.TextGenerationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenAIChatCompletionServiceConfig {

    @Value("#{${client-azureopenai-deployment-name}}")
    Map<String, String> modelIdsMap;

    @Bean
    public ChatCompletionService chatCompletionService(OpenAIAsyncClient aiAsyncClient) {
        return OpenAIChatCompletion.builder()
                .withModelId(modelIdsMap.get("chatCompletionService"))
                .withOpenAIAsyncClient(aiAsyncClient)
                .build();
    }

    @Bean
    public TextGenerationService embeddedChatCompletionService(OpenAIAsyncClient aiAsyncClient) {
        return OpenAITextGenerationService.builder()
                .withModelId(modelIdsMap.get("textGenerationService"))
                .withOpenAIAsyncClient(aiAsyncClient)
                .build();
    }
}
