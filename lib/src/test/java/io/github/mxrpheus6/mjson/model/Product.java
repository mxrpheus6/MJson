package io.github.mxrpheus6.mjson.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class Product {
    private UUID id;
    private String name;
    private Double price;
    private Map<UUID, BigDecimal> storageQuantity;
}
