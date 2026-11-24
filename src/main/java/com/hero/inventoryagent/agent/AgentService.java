package com.hero.inventoryagent.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
public class AgentService {

    private final ChatClient chatClient;

    public AgentService(@Qualifier("agentChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String newConversationId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    public String chat(String userId, String conversationId, String message) {
        String memoryKey = userId + ":" + conversationId;
        return chatClient.prompt()
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, memoryKey))
                .toolContext(Map.of("userId", userId))
                .call()
                .content();
    }
}