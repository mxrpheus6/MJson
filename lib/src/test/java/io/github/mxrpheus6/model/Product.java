package io.github.mxrpheus6.model;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class Product {
    private UUID id;
    private String name;
    private Double price;
    private Map<UUID, BigDecimal> storageQuantity;

    public Product(UUID id, String name, Double price, Map<UUID, BigDecimal> storageQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.storageQuantity = storageQuantity;
    }
}

