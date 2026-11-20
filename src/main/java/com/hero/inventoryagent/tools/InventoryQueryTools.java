package com.hero.inventoryagent.tools;

import com.hero.inventoryagent.product.Product;
import com.hero.inventoryagent.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryQueryTools {

    private static final Logger log = LoggerFactory.getLogger(InventoryQueryTools.class);

    private final ProductService productService;

    public InventoryQueryTools(ProductService productService) {
        this.productService = productService;
    }

    public record StockResult(boolean found, String sku, String name,
                              Integer stock, Integer minStock, String message) {}

    public record ProductSummary(String sku, String name, int stock, int minStock) {
        static ProductSummary from(Product p) {
            return new ProductSummary(p.sku(), p.name(), p.stock(), p.minStock());
        }
    }

    public record ProductList(int total, List<ProductSummary> products) {
        static ProductList of(List<Product> products) {
            List<ProductSummary> items = products.stream().map(ProductSummary::from).toList();
            return new ProductList(items.size(), items);
        }
    }

    @Tool(description = """
            Get the current stock of ONE product by its exact SKU, for example SKU-1234 or HDMI-2M.
            Use it only when the user provides a SKU. If the user names a product instead, call searchProducts first.""")
    public StockResult getStock(@ToolParam(description = "Exact product SKU") String sku) {
        log.info("Tool getStock(sku={})", sku);
        return productService.findBySku(sku)
                .map(p -> new StockResult(true, p.sku(), p.name(), p.stock(), p.minStock(), null))
                .orElseGet(() -> new StockResult(false, sku, null, null, null,
                        "No product found with SKU " + sku));
    }

    @Tool(description = """
        Search products by name or partial SKU. Returns at most 5 matches with SKU, name, stock and minimum stock.
        Use it when the user refers to a product by its name or description instead of its SKU.
        Product names are stored in English: translate the user's words to English and search with one or two keywords.""")
    public ProductList searchProducts(
            @ToolParam(description = "One or two English keywords from the product name, for example 'hdmi' or 'monitor'") String query) {
        ProductList result = ProductList.of(productService.search(query));
        log.info("Tool searchProducts(query={}) -> {} results", query, result.total());
        return result;
    }

    @Tool(description = "List ALL products whose stock is below their minimum stock level, most urgent first.")
    public ProductList listProductsBelowMinimum() {
        ProductList result = ProductList.of(productService.findBelowMinimum());
        log.info("Tool listProductsBelowMinimum() -> {} results", result.total());
        return result;
    }
}