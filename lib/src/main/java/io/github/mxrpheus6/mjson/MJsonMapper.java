package io.github.mxrpheus6.mjson;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class MJsonMapper {

    /**
     * Serializes the provided object into JSON format.
     * This method takes any Java object and converts it into its corresponding JSON representation
     * as a string. The serialization includes all accessible fields of the object.
     *
     * @param obj The object to be serialized into JSON
     * @return A string containing the serialized JSON representation of the object
     * @throws IllegalAccessException If the method is unable to access a field of the provided object
     */
    public String serialize(Object obj) throws IllegalAccessException {
        Class<?> objClass = obj.getClass();
        Map<String, String> jsonElements = new LinkedHashMap<>();
        for (Field field: objClass.getDeclaredFields()) {
            if (Modifier.isFinal(field.getModifiers()) ||
                Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            jsonElements.put(field.getName(), formatValue(field.get(obj)));
        }

        StringBuilder json = new StringBuilder();
        json.append("{");
        for (Map.Entry<String, String> entry: jsonElements.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\":")
                    .append(entry.getValue()).append(",");
        }
        json.deleteCharAt(json.length() - 1);
        json.append("}");

        return json.toString();
    }

    private String formatValue(Object value) {
        if (value instanceof String) {
            return "\"" + value + "\"";
        }

        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }

        if (value instanceof Collection) {
            return formatCollection((Collection<?>) value);
        }
        if (value instanceof Map) {
            return formatMap((Map<?, ?>) value);
        }

        return "\"" + value.toString() + "\"";
    }

    private String formatCollection(Collection<?> collection) {
        StringBuilder json = new StringBuilder();
        json.append("[");

        for (Object item: collection) {
            json.append(formatValue(item)).append(",");
        }

        json.deleteCharAt(json.length() - 1);
        json.append("]");
        return json.toString();
    }

    private String formatMap(Map<?, ?> map) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        for (Map.Entry<?, ?> entry: map.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\":")
                    .append(formatValue(entry.getValue())).append(",");
        }

        json.deleteCharAt(json.length() - 1);
        json.append("}");
        return json.toString();
    }
}