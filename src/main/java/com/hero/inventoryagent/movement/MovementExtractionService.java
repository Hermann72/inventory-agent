package com.hero.inventoryagent.movement;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MovementExtractionService {

    private final ChatClient chatClient;

    public MovementExtractionService(@Qualifier("extractionChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public StockMovementDraft extract(String text) {
        MovementExtraction extraction = chatClient.prompt()
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .user(text)
                .call()
                .entity(MovementExtraction.class);
        return StockMovementDraft.from(extraction);
    }
}