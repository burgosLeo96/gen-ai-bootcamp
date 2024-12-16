package com.epam.training.gen.ai.config;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.InvocationReturnMode;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.services.AIServiceCollection;
import com.microsoft.semantickernel.services.AIServiceSelector;
import com.microsoft.semantickernel.services.OrderedAIServiceSelector;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.textcompletion.TextGenerationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class SemanticKernelConfig {

    @Bean
    public Kernel semanticKernel(ChatCompletionService chatCompletionService, TextGenerationService textGenerationService) {
        Function<AIServiceCollection, AIServiceSelector> serviceSelector = OrderedAIServiceSelector::new;

        return Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .withAIService(TextGenerationService.class, textGenerationService)
                .withServiceSelector(serviceSelector)
                .build();
    }

    @Bean
    public InvocationContext invocationContext() {
        return new InvocationContext.Builder()
                .withReturnMode(InvocationReturnMode.LAST_MESSAGE_ONLY)
                .withPromptExecutionSettings(initializeExecutionSettings())
                .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                .build();
    }

    private PromptExecutionSettings initializeExecutionSettings() {
        return PromptExecutionSettings
                .builder()
                .withTemperature(0.7)
                .build();
    }
}
