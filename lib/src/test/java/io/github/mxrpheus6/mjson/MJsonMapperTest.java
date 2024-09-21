package io.github.mxrpheus6.mjson;

import io.github.mxrpheus6.mjson.model.Customer;
import io.github.mxrpheus6.mjson.model.Order;
import io.github.mxrpheus6.mjson.model.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class MJsonMapperTest {

    @Test
    void deserialize_success() throws Exception {
        Product product = Product.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"))
                .name("iPhone 5S")
                .price(499.99)
                .storageQuantity(Map.ofEntries(
                        Map.entry(UUID.fromString("123e4567-e89b-12d3-a456-426614174003"), BigDecimal.valueOf(1500.0))
                ))
                .build();

        Order order = Order.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"))
                .products(List.of(product))
                .createDate(OffsetDateTime.parse("2024-09-19T14:23:15.123Z"))
                .build();


        MJsonMapper mJsonMapper = new MJsonMapper();
        Customer customerExpected = Customer.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .firstName("Steve")
                .lastName("Jobs")
                .dateBirth(LocalDate.of(1955, 10, 5))
                .orders(List.of(order))
                .build();

        Customer customerFromJson = mJsonMapper.deserialize(
                "{\"id\":\"123e4567-e89b-12d3-a456-426614174000\",\"firstName\":\"Steve\",\"lastName\":\"Jobs\",\"dateBirth\":\"1955-10-05\",\"orders\":[{\"id\":\"123e4567-e89b-12d3-a456-426614174001\",\"products\":[{\"id\":\"123e4567-e89b-12d3-a456-426614174002\",\"name\":\"iPhone 5S\",\"price\":499.99,\"storageQuantity\":{\"123e4567-e89b-12d3-a456-426614174003\":1500.00}}],\"createDate\":\"2024-09-19T14:23:15.123Z\"}]}",
                Customer.class);

        Assertions.assertEquals(customerExpected.getId(), customerFromJson.getId());
        Assertions.assertEquals(customerExpected.getFirstName(), customerFromJson.getFirstName());
        Assertions.assertEquals(customerExpected.getLastName(), customerFromJson.getLastName());
        Assertions.assertEquals(customerExpected.getDateBirth(), customerFromJson.getDateBirth());
        Assertions.assertEquals(customerExpected.getOrders().size(), customerFromJson.getOrders().size());
    }

}
