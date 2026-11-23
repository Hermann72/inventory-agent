package com.hero.inventoryagent.tools;

import com.hero.inventoryagent.movement.InboundResult;
import com.hero.inventoryagent.movement.StockMovementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class InventoryActionTools {

    private static final Logger log = LoggerFactory.getLogger(InventoryActionTools.class);

    private final StockMovementService movementService;

    public InventoryActionTools(StockMovementService movementService) {
        this.movementService = movementService;
    }

    @Tool(description = """
            Register an INBOUND stock movement (units received) for ONE product identified by its exact SKU.
            Call it only when the user explicitly asks to register or receive units.
            If the user named a product instead of giving a SKU, call searchProducts first;
            if several products match, ask the user which one before calling this tool.""")
    public InboundResult registerInbound(
            @ToolParam(description = "Exact product SKU") String sku,
            @ToolParam(description = "Number of units received, a positive integer") Integer quantity,
            @ToolParam(description = "Optional reference such as supplier or purchase order", required = false) String reference,
            ToolContext toolContext) {
        String userId = (String) toolContext.getContext().get("userId");
        InboundResult result = movementService.registerInbound(sku, quantity, reference, userId);
        log.info("Tool registerInbound(sku={}, quantity={}, reference={}, userId={}) -> success={}, error={}",
                sku, quantity, reference, userId, result.success(), result.error());
        return result;
    }
}