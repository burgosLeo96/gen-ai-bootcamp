package com.epam.training.gen.ai.service.chatcompletion;

import com.epam.training.gen.ai.service.chatcompletion.utils.MessageProcessingUtils;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureChatCompletionService {

    private final ChatHistory chatHistory = new ChatHistory();
    private final ChatCompletionService chatCompletionService;
    private final InvocationContext invocationContext;
    private final Kernel kernel;

    public List<String> getChatResponse(String prompt) {
        log.info("Processing prompt request");
        this.addUserPromptToHistory(prompt);
        var chatResponse = chatCompletionService.getChatMessageContentsAsync(chatHistory, kernel, invocationContext).block();
        log.info("Chat response received. Processing assistant messages");

        var assistantResponse = MessageProcessingUtils.processChatResponse(chatResponse);
        this.addAssistantResponsesToHistory(assistantResponse);
        return MessageProcessingUtils.getAssistantResponseMessagesContent(assistantResponse);
    }

    private void addAssistantResponsesToHistory(List<ChatMessageContent<?>> messagesContent) {
        this.chatHistory.addAll(messagesContent);
    }

    private void addUserPromptToHistory(String prompt) {
        this.chatHistory.addUserMessage(prompt);
    }
}
