package com.hero.inventoryagent.product;

public record Product(long id, String sku, String name, int stock, int minStock) {}