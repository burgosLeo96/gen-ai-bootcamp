package com.epam.training.gen.ai.config;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureOpenAIClientConfig {

    @Value("${client-azureopenai-key}")
    private String clientKey;

    @Value("${client-azureopenai-endpoint}")
    private String clientEndpoint;

    @Bean
    public OpenAIAsyncClient aiAsyncClient() {
        return new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(clientKey))
                .endpoint(clientEndpoint)
                .buildAsyncClient();
    }

}
