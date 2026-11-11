package com.hero.inventoryagent.movement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record StockMovementDraft(
        MovementType type,
        String sku,
        Integer quantity,
        String counterparty,
        LocalDate date,
        List<String> missingFields
) {
        public static StockMovementDraft from(MovementExtraction e) {
                List<String> missing = new ArrayList<>();
                if (e.type() == null)     missing.add("type");
                if (e.sku() == null)      missing.add("sku");
                if (e.quantity() == null) missing.add("quantity");
                return new StockMovementDraft(e.type(), e.sku(), e.quantity(),
                        e.counterparty(), e.date(), List.copyOf(missing));
        }
}