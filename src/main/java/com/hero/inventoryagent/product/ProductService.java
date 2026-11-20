package com.hero.inventoryagent.product;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ProductService {

    static final int MAX_SEARCH_RESULTS = 5;
    static final int MAX_SEARCH_WORDS = 5;

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Optional<Product> findBySku(String sku) {
        if (sku == null || sku.isBlank()) {
            return Optional.empty();
        }
        return repository.findBySku(normalizeSku(sku));
    }

    public List<Product> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<String> words = Arrays.stream(text.trim().toLowerCase(Locale.ROOT).split("\\s+"))
                .filter(w -> w.length() >= 2)
                .distinct()
                .limit(MAX_SEARCH_WORDS)
                .toList();
        if (words.isEmpty()) {
            return List.of();
        }
        return repository.search(words, MAX_SEARCH_RESULTS);
    }

    public List<Product> findBelowMinimum() {
        return repository.findBelowMinimum();
    }

    static String normalizeSku(String sku) {
        return sku.trim().toUpperCase(Locale.ROOT);
    }
}