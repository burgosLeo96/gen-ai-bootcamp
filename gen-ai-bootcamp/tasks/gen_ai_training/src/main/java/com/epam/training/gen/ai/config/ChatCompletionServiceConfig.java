package com.epam.training.gen.ai.config;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatCompletionServiceConfig {

    @Value("${client-azureopenai-deployment-name}")
    private String modelId;

    @Bean
    public ChatCompletionService chatCompletionService(OpenAIAsyncClient aiAsyncClient) {
        return OpenAIChatCompletion.builder()
                .withModelId(modelId)
                .withOpenAIAsyncClient(aiAsyncClient)
                .build();
    }

}
