package com.hero.inventoryagent.movement;

public record InboundResult(boolean success, String sku, String name,
                            Integer quantity, Integer newStock,
                            Long movementId, String error) {

    static InboundResult ok(String sku, String name, int quantity, int newStock, long movementId) {
        return new InboundResult(true, sku, name, quantity, newStock, movementId, null);
    }

    static InboundResult failed(String sku, String error) {
        return new InboundResult(false, sku, null, null, null, null, error);
    }
}
