package io.github.mxrpheus6.mjson;

import io.github.mxrpheus6.model.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

class MJsonMapperTest {

    @Test void someLibraryMethodReturnsTrue() throws IllegalAccessException {
        MJsonMapper mJsonMapper = new MJsonMapper();
        UUID productId = UUID.fromString("1cde057b-e8a2-4e0f-b1d1-0e5883e4fe5d");
        UUID storageId1 = UUID.fromString("2cde057b-e8a2-4e0f-b1d1-0e5883e4fe5d");
        UUID storageId2 = UUID.fromString("3cde057b-e8a2-4e0f-b1d1-0e5883e4fe5d");
        BigDecimal bigDecimal1 = new BigDecimal("1234567890123456789012345678901234567890");
        BigDecimal bigDecimal2 = new BigDecimal("0987654321098765432109876543210987654321");
        Map<UUID, BigDecimal> storageQuantity = new LinkedHashMap<>();
        storageQuantity.put(storageId1, bigDecimal1);
        storageQuantity.put(storageId2, bigDecimal2);

        Product product = new Product(productId, "Coca-Cola", 1.05, storageQuantity);
        System.out.println(mJsonMapper.serialize(product));
        Assertions.assertEquals("{\"id\":\"1cde057b-e8a2-4e0f-b1d1-0e5883e4fe5d\",\"name\":\"Coca-Cola\",\"price\":1.05,\"storageQuantity\":{\"2cde057b-e8a2-4e0f-b1d1-0e5883e4fe5d\":1234567890123456789012345678901234567890,\"3cde057b-e8a2-4e0f-b1d1-0e5883e4fe5d\":987654321098765432109876543210987654321}}", mJsonMapper.serialize(product));
    }
}
