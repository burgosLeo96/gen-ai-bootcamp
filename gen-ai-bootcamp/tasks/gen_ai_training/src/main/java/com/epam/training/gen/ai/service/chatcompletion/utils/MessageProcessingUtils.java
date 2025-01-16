package com.epam.training.gen.ai.service.chatcompletion.utils;

import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public final class MessageProcessingUtils {

    public static List<ChatMessageContent<?>> processChatResponse(List<ChatMessageContent<?>> chatMessageContentList) {
        if(chatMessageContentList == null) {
            log.warn("Chat response is null. Returning empty list");
            return List.of();
        }

        return chatMessageContentList
                .stream()
                .filter(MessageProcessingUtils::messageGeneratedByAssistant)
                .toList();
    }

    public static List<String> getAssistantResponseMessagesContent(List<ChatMessageContent<?>> messageContents){
        log.info("Extracting assistant messages content");
        return messageContents
                .stream()
                .map(ChatMessageContent::getContent)
                .filter(Objects::nonNull)
                .flatMap(message -> Arrays.stream(message.split("\n+")))
                .toList();
    }

    private static boolean messageGeneratedByAssistant(ChatMessageContent<?> messageContent) {
        return messageContent.getAuthorRole() == AuthorRole.ASSISTANT && messageContent.getContent() != null;
    }
}
