package com.hero.inventoryagent.movement;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class StockMovementRepository {

    private final JdbcClient jdbc;

    public StockMovementRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public long insert(long productId, MovementType type, int quantity,
                       int stockAfter, String reference, String createdBy) {
        return jdbc.sql("""
                        INSERT INTO stock_movement
                            (product_id, type, quantity, stock_after, reference, created_by)
                        VALUES (:productId, :type, :quantity, :stockAfter, :reference, :createdBy)
                        RETURNING id""")
                .param("productId", productId)
                .param("type", type.name())
                .param("quantity", quantity)
                .param("stockAfter", stockAfter)
                .param("reference", reference)
                .param("createdBy", createdBy)
                .query(Long.class)
                .single();
    }
}