# MJsonMapper

MJsonMapper — учебная библиотека на Java для сериализации и десериализации объектов в формат JSON в рамках моего обучения в Clevertec. Использование этой библиотеки в своих проектах может быть небезопасным.

## Возможности

- **Сериализация** Java объектов в JSON строки.
- **Десериализация** JSON строк в Java объекты.

## Функции

### 1. `serialize(Object obj)`

Сериализует Java объект в JSON строку.

#### Параметры
- `obj`: Java объект для сериализации.

#### Возвращает
- JSON строку, представляющую объект.

#### Пример
```java
MJsonMapper mJsonMapper = new MJsonMapper();
Customer customer = new Customer("John", "Doe", LocalDate.of(1990, 1, 1));
String jsonString = mJsonMapper.serialize(customer);
System.out.println(jsonString);
```

### 2. `deserialize(String jsonString, Class<T> clazz)`

Десериализует JSON строку в Java объект указанного класса.

#### Параметры
- `jsonString`: JSON строка для десериализации.
- `clazz`: Класс объекта, который необходимо создать.

#### Возвращает
- Экземпляр указанного класса, заполненный данными из JSON строки.

#### Пример
```java
String jsonString = "{\"id\":\"123e4567-e89b-12d3-a456-426614174000\",\"firstName\":\"John\",\"lastName\":\"Doe\"}";
Customer customer = mJsonMapper.deserialize(jsonString, Customer.class);
System.out.println(customer.getFirstName()); // Выводит: John
```