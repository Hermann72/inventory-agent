package com.hero.inventoryagent.movement;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.time.LocalDate;

public record MovementExtraction(
        @JsonPropertyDescription("INBOUND, OUTBOUND or ADJUSTMENT; null if unclear")
        MovementType type,
        @JsonPropertyDescription("Product SKU exactly as written by the user; null if not stated")
        String sku,
        @JsonPropertyDescription("Number of units; negative for ADJUSTMENT that decreases stock; null if not stated")
        Integer quantity,
        @JsonPropertyDescription("Supplier, customer or store involved; null if not stated")
        String counterparty,
        @JsonPropertyDescription("Date of the movement in ISO format; null if no date or relative date is mentioned")
        LocalDate date
) {}