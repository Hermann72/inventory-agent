package com.hero.inventoryagent.api;

import com.hero.inventoryagent.agent.AgentService;
import com.hero.inventoryagent.movement.MovementExtractionService;
import com.hero.inventoryagent.movement.StockMovementDraft;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private static final Pattern USER_ID = Pattern.compile("^[A-Za-z0-9_-]{1,20}$");
    private static final Pattern CONVERSATION_ID = Pattern.compile("^[a-f0-9]{8}$");

    private final AgentService agentService;
    private final MovementExtractionService movementExtractionService;

    public AgentController(AgentService agentService, MovementExtractionService movementExtractionService) {
        this.agentService = agentService;
        this.movementExtractionService = movementExtractionService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestHeader("X-User-Id") String userId,
                                             @RequestBody ChatRequest request) {
        if (!USER_ID.matcher(userId).matches()
                || request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String conversationId = request.conversationId() != null
                ? request.conversationId()
                : agentService.newConversationId();

        if (!CONVERSATION_ID.matcher(conversationId).matches()) {
            return ResponseEntity.badRequest().build();
        }

        String answer = agentService.chat(userId, conversationId, request.message());
        return ResponseEntity.ok(new ChatResponse(conversationId, answer));
    }

    @PostMapping("/movements/extract")
    public ResponseEntity<StockMovementDraft> extract(@RequestBody ExtractRequest request) {
        if (request.text() == null || request.text().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(movementExtractionService.extract(request.text()));
    }
}