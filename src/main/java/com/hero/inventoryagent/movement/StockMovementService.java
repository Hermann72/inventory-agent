package com.hero.inventoryagent.movement;

import com.hero.inventoryagent.product.ProductRepository;
import com.hero.inventoryagent.product.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockMovementService {

    static final int MAX_QUANTITY = 10_000;

    private final ProductRepository productRepository;
    private final StockMovementRepository movementRepository;

    public StockMovementService(ProductRepository productRepository,
                                StockMovementRepository movementRepository) {
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
    }

    @Transactional
    public InboundResult registerInbound(String sku, Integer quantity, String reference, String userId) {
        if (sku == null || sku.isBlank()) {
            return InboundResult.failed(sku, "A SKU is required");
        }
        String normalizedSku = ProductService.normalizeSku(sku);

        if (quantity == null || quantity <= 0) {
            return InboundResult.failed(normalizedSku, "Quantity must be a positive number");
        }
        if (quantity > MAX_QUANTITY) {
            return InboundResult.failed(normalizedSku,
                    "Quantity exceeds the maximum of " + MAX_QUANTITY + " units per movement");
        }

        return productRepository.increaseStock(normalizedSku, quantity)
                .map(product -> {
                    long movementId = movementRepository.insert(product.id(), MovementType.INBOUND,
                            quantity, product.stock(), reference, userId);
                    return InboundResult.ok(product.sku(), product.name(), quantity,
                            product.stock(), movementId);
                })
                .orElseGet(() -> InboundResult.failed(normalizedSku,
                        "No product found with SKU " + normalizedSku));
    }
}