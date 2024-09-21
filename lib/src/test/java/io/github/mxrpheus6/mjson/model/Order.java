package io.github.mxrpheus6.mjson.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class Order {
    private UUID id;
    private List<Product> products;
    private OffsetDateTime createDate;
}
