package com.hero.inventoryagent.product;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ProductRepository {

    private static final String COLUMNS = "id, sku, name, stock, min_stock";

    private final JdbcClient jdbc;

    public ProductRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Product> findBySku(String sku) {
        return jdbc.sql("SELECT " + COLUMNS + " FROM product WHERE sku = :sku")
                .param("sku", sku)
                .query(Product.class)
                .optional();
    }

    public List<Product> search(List<String> words, int limit) {
        StringBuilder where = new StringBuilder();
        StringBuilder score = new StringBuilder();
        Map<String, Object> params = new HashMap<>();

        for (int i = 0; i < words.size(); i++) {
            String param = "w" + i;
            String condition = "(name ILIKE :" + param + " OR sku ILIKE :" + param + ")";
            if (i > 0) {
                where.append(" OR ");
                score.append(" + ");
            }
            where.append(condition);
            score.append("CASE WHEN ").append(condition).append(" THEN 1 ELSE 0 END");
            params.put(param, "%" + escapeLike(words.get(i)) + "%");
        }

        return jdbc.sql("""
                    SELECT %s FROM product
                    WHERE %s
                    ORDER BY (%s) DESC, name
                    LIMIT :limit""".formatted(COLUMNS, where, score))
                .params(params)
                .param("limit", limit)
                .query(Product.class)
                .list();
    }

    public List<Product> findBelowMinimum() {
        return jdbc.sql("""
                        SELECT %s FROM product
                        WHERE stock < min_stock
                        ORDER BY (min_stock - stock) DESC, sku""".formatted(COLUMNS))
                .query(Product.class)
                .list();
    }

    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}