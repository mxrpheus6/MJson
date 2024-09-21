package io.github.mxrpheus6.mjson;

import sun.reflect.ReflectionFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MJsonMapper {

    private ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();

    public <T> T deserialize(String jsonString, Class<?> clazz) throws Exception {
        Map<String, Object> jsonMap = parseJson(jsonString);
        return deserializeObject(jsonMap, clazz);
    }

    private <T> T deserializeObject(Map<String, Object> jsonMap, Class<?> clazz) throws Exception {
        T instance = (T) reflectionFactory.newConstructorForSerialization(clazz, Object.class.getDeclaredConstructor()).newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) ||
                    Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);
            String fieldName = field.getName();

            if (jsonMap.containsKey(fieldName)) {
                Object value = jsonMap.get(fieldName);
                Class<?> fieldType = field.getType();

                if (value instanceof String && !fieldType.equals(String.class)) {
                    value = convertStringToType((String) value, fieldType);
                }

                if (isPrimitiveOrWrapper(fieldType)) {
                    field.set(instance, convertToType(value, fieldType));
                } else if (fieldType == List.class) {
                    Type genericType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    field.set(instance, deserializeList((List<?>) value, (Class<?>) genericType));
                } else if (fieldType == OffsetDateTime.class) {
                    field.set(instance, OffsetDateTime.parse(value.toString()));
                } else if (fieldType == Set.class) {
                    Type genericType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    field.set(instance, new LinkedHashSet<>(deserializeList((List<?>) value, (Class<?>) genericType)));
                } else if (fieldType == Map.class) {
                    Type[] genericTypes = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                    field.set(instance, deserializeMap((Map<?, ?>) value, (Class<?>) genericTypes[0], (Class<?>) genericTypes[1]));
                } else if (fieldType.isArray()) {
                    Class<?> componentType = fieldType.getComponentType();
                    field.set(instance, deserializeArray((List<?>) value, componentType));
                } else if (value instanceof Map) {
                    field.set(instance, deserializeObject((Map<String, Object>) value, fieldType));
                } else {
                    field.set(instance, value);
                }
            }
        }
        return instance;
    }

    private Object convertStringToType(String value, Class<?> fieldType) {
        value = value.replace("{", "");
        value = value.replace("}", "");
        if (fieldType == UUID.class) {
            return UUID.fromString(value);
        } else if (fieldType == LocalDate.class) {
            return LocalDate.parse(value);
        } else if (fieldType == LocalDateTime.class) {
            return LocalDateTime.parse(value);
        } else if (fieldType == OffsetDateTime.class) {
            return OffsetDateTime.parse(value);
        } else if (fieldType == ZonedDateTime.class) {
            return ZonedDateTime.parse(value);
        } else if (Enum.class.isAssignableFrom(fieldType)) {
            return Enum.valueOf((Class<Enum>) fieldType, value);
        }

        throw new IllegalArgumentException("Unsupported field type: " + fieldType.getName());
    }

    private <T> T[] deserializeArray(List<?> jsonArray, Class<T> componentType) throws Exception {
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(componentType, jsonArray.size());

        for (int i = 0; i < jsonArray.size(); i++) {
            if (isPrimitiveOrWrapper(componentType)) {
                array[i] = convertToType(jsonArray.get(i), componentType);
            } else {
                array[i] = deserializeObject((Map<String, Object>) jsonArray.get(i), componentType);
            }
        }
        return array;
    }

    private <K, V> Map<K, V> deserializeMap(Map<?, ?> jsonMap, Class<K> keyClass, Class<V> valueClass) throws Exception {
        Map<K, V> map = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : jsonMap.entrySet()) {
            K key = convertToType(entry.getKey(), keyClass);
            V value;

            if (isPrimitiveOrWrapper(valueClass)) {
                value = convertToType(entry.getValue(), valueClass);
            } else if (entry.getValue() instanceof Map) {
                // Если значение — карта, десериализуем
                value = deserializeObject((Map<String, Object>) entry.getValue(), valueClass);
            } else {
                if (isPrimitiveOrWrapper(entry.getValue().getClass())) {
                    value = convertToType(entry.getValue(), valueClass);
                } else {
                    throw new IllegalArgumentException("Expected a Map for key: " + key + ", but got: " + entry.getValue().getClass().getName());
                }
            }

            map.put(key, value);
        }
        return map;
    }

    private <T> List<T> deserializeList(List<?> jsonArray, Class<?> clazz) throws Exception {
        List<T> list = new ArrayList<>();
        for (Object element : jsonArray) {
            if (element instanceof Map) {
                list.add(deserializeObject((Map<String, Object>) element, clazz));
            } else {
                list.add((T) convertToType(element, clazz));
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private <T> T convertToType(Object value, Class<?> clazz) {
        if (clazz == Integer.class || clazz == int.class) {
            return (T) Integer.valueOf(value.toString());
        } else if (clazz == Long.class || clazz == long.class) {
            return (T) Long.valueOf(value.toString());
        } else if (clazz == Double.class || clazz == double.class) {
            return (T) Double.valueOf(value.toString());
        } else if (clazz == Float.class || clazz == float.class) {
            return (T) Float.valueOf(value.toString());
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            return (T) Boolean.valueOf(value.toString());
        } else if (clazz == String.class) {
            return (T) value;
        }
        return (T) value;
    }

    private Map<String, Object> parseJson(String jsonString) {
        Map<String, Object> jsonMap = new LinkedHashMap<>();
        jsonString = jsonString.trim().substring(1, jsonString.length() - 1);

        StringBuilder currentKey = new StringBuilder();
        StringBuilder currentValue = new StringBuilder();
        boolean inQuotes = false;
        int braceCount = 0;

        for (int i = 0; i < jsonString.length(); i++) {
            char currentChar = jsonString.charAt(i);

            if (currentChar == '"') {
                inQuotes = !inQuotes;
                currentValue.append(currentChar);
                continue;
            }

            if (!inQuotes) {
                if (currentChar == '{' || currentChar == '[') {
                    braceCount++;
                } else if (currentChar == '}' || currentChar == ']') {
                    braceCount--;
                }

                if (currentChar == ':' && braceCount == 0) {
                    currentKey.append(currentValue.toString().trim().replace("\"", ""));
                    currentValue.setLength(0);
                } else if (currentChar == ',' && braceCount == 0) {
                    jsonMap.put(currentKey.toString(), parseJsonValue(currentValue.toString().trim()));
                    currentValue.setLength(0);
                    currentKey.setLength(0);
                } else {
                    currentValue.append(currentChar);
                }
            } else {
                currentValue.append(currentChar);
            }
        }

        if (currentKey.length() > 0) {
            jsonMap.put(currentKey.toString(), parseJsonValue(currentValue.toString().trim()));
        }

        return jsonMap;
    }

    private Object parseJsonValue(String value) {
        if (value.startsWith("{")) {
            return parseJson(value);
        } else if (value.startsWith("[")) {
            return parseJsonArray(value);
        } else if (value.startsWith("\"")) {
            return value.substring(1, value.length() - 1);
        } else if (value.equals("true") || value.equals("false")) {
            return Boolean.parseBoolean(value);
        } else {
            return parseNumber(value);
        }
    }

    private Object parseNumber(String value) {
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            return value;
        }
    }

    private List<Object> parseJsonArray(String value) {
        List<Object> list = new ArrayList<>();
        value = value.trim().substring(1, value.length() - 1);

        StringBuilder currentElement = new StringBuilder();
        int braceCount = 0;
        boolean inQuotes = false;

        for (int i = 0; i < value.length(); i++) {
            char currentChar = value.charAt(i);
            if (currentChar == '"') {
                inQuotes = !inQuotes;
            }

            if (!inQuotes) {
                if (currentChar == '{' || currentChar == '[') {
                    braceCount++;
                } else if (currentChar == '}' || currentChar == ']') {
                    braceCount--;
                }

                if (currentChar == ',' && braceCount == 0) {
                    list.add(parseJsonValue(currentElement.toString().trim()));
                    currentElement.setLength(0);
                } else {
                    currentElement.append(currentChar);
                }
            } else {
                currentElement.append(currentChar);
            }
        }

        if (currentElement.length() > 0) {
            list.add(parseJsonValue(currentElement.toString().trim()));
        }

        return list;
    }

    private boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() || type == String.class ||
                type == Integer.class || type == Long.class ||
                type == Double.class || type == Float.class ||
                type == Boolean.class || type == Character.class;
    }
}