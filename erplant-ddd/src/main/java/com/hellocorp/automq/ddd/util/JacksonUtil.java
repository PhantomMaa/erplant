package com.hellocorp.automq.ddd.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * Json utils implement by Jackson.
 */
@Slf4j
public final class JacksonUtil {

    static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JavaTimeModule());
    }

    /**
     * Object to json string.
     *
     * @param obj obj
     * @return json string
     */
    public static String toJsonString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("toJsonString mapper.writeValueAsString occur exception", e);
            throw new JsonSerializationException(obj.getClass(), e);
        }
    }

    /**
     * Object to json string byte array.
     *
     * @param obj obj
     * @return json string byte array
     */
    public static byte[] toJsonBytes(Object obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            log.warn("toJsonBytes mapper.writeValueAsBytes occur exception", e);
            throw new JsonSerializationException(obj.getClass(), e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param cls  class of object
     * @param <T>  General type
     * @return object
     */
    public static <T> T toObj(byte[] json, Class<T> cls) {
        try {
            return mapper.readValue(json, cls);
        } catch (Exception e) {
            log.warn("toObj mapper.readValue occur exception", e);
            throw new JsonSerializationException(cls, e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param cls  {@link Type} of object
     * @param <T>  General type
     * @return object
     */
    public static <T> T toObj(byte[] json, Type cls) {
        try {
            return mapper.readValue(json, mapper.constructType(cls));
        } catch (Exception e) {
            log.warn("toObj mapper.readValue occur exception", e);
            throw new JsonSerializationException(cls.getClass(), e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param inputStream json string input stream
     * @param cls         class of object
     * @param <T>         General type
     * @return object
     */
    public static <T> T toObj(InputStream inputStream, Class<T> cls) {
        try {
            return mapper.readValue(inputStream, cls);
        } catch (IOException e) {
            log.warn("toObj mapper.readValue occur exception", e);
            throw new JsonSerializationException(cls, e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json          json string byte array
     * @param typeReference {@link TypeReference} of object
     * @param <T>           General type
     * @return object
     */
    public static <T> T toObj(byte[] json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (Exception e) {
            log.warn("toObj mapper.readValue occur exception", e);
            throw new JsonSerializationException(typeReference.getType().getClass(), e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param cls  class of object
     * @param <T>  General type
     * @return object
     */
    public static <T> T toObj(String json, Class<T> cls) {
        if (json == null) {
            return null;
        }

        try {
            return mapper.readValue(json, cls);
        } catch (IOException e) {
            log.warn("toObj mapper.readValue occur exception", e);
            throw new JsonSerializationException(cls, e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param type {@link Type} of object
     * @param <T>  General type
     * @return object
     */
    public static <T> T toObj(String json, Type type) {
        try {
            return mapper.readValue(json, mapper.constructType(type));
        } catch (IOException e) {
            log.warn("toObj mapper.readValue occur exception", e);
            throw new JsonSerializationException(type.getClass(), e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json          json string
     * @param typeReference {@link TypeReference} of object
     * @param <T>           General type
     * @return object
     */
    public static <T> T toObj(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (IOException e) {
            log.warn("toObj mapper.readValue occur exception", e);
            throw new JsonSerializationException(typeReference.getClass(), e);
        }
    }

    public static <T> T toObj(Object obj, Class<T> type) {
        try {
            return mapper.convertValue(obj, type);
        } catch (IllegalArgumentException e) {
            log.warn("toObj mapper.readValue occur exception", e);
            throw new JsonSerializationException(type, e);
        }
    }

    public static <T> T toObj(Object obj, TypeReference<T> type) {
        try {
            return mapper.convertValue(obj, type);
        } catch (IllegalArgumentException e) {
            log.warn("toObj mapper.readValue occur exception", e);
            throw new JsonSerializationException(type.getClass(), e);
        }
    }

    /**
     * Json string deserialize to List.
     *
     * @param json json string
     * @param <T>  General type
     * @return List
     */
    public static <T> List<T> toListObjs(String json, Class<T[]> cls) {
        try {
            return Arrays.asList(mapper.readValue(json, cls));
        } catch (IOException e) {
            log.warn("toObj mapper.readValue occur exception json:{}", json, e);
            throw new JsonSerializationException(List.class, e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param inputStream json string input stream
     * @param type        {@link Type} of object
     * @param <T>         General type
     * @return object
     */
    public static <T> T toObj(InputStream inputStream, Type type) {
        try {
            return mapper.readValue(inputStream, mapper.constructType(type));
        } catch (IOException e) {
            log.warn("toObj mapper.readValue occur exception", e);
            throw new JsonSerializationException(type.getClass(), e);
        }
    }

    /**
     * Json string deserialize to Jackson {@link JsonNode}.
     *
     * @param json json string
     * @return {@link JsonNode}
     */
    public static JsonNode toObj(String json) {
        try {
            return mapper.readTree(json);
        } catch (IOException e) {
            log.warn("toObj mapper.readTree occur exception, json : {}", json, e);
            throw new JsonSerializationException(JsonNode.class, e);
        }
    }

    /**
     * Register sub type for child class.
     *
     * @param clz  child class
     * @param type type name of child class
     */
    public static void registerSubtype(Class<?> clz, String type) {
        mapper.registerSubtypes(new NamedType(clz, type));
    }

    /**
     * Create a new empty Jackson {@link ObjectNode}.
     *
     * @return {@link ObjectNode}
     */
    public static ObjectNode createEmptyJsonNode() {
        return new ObjectNode(mapper.getNodeFactory());
    }

    /**
     * Create a new empty Jackson {@link ArrayNode}.
     *
     * @return {@link ArrayNode}
     */
    public static ArrayNode createEmptyArrayNode() {
        return new ArrayNode(mapper.getNodeFactory());
    }

    /**
     * Parse object to Jackson {@link JsonNode}.
     *
     * @param obj object
     * @return {@link JsonNode}
     */
    public static JsonNode transferToJsonNode(Object obj) {
        return mapper.valueToTree(obj);
    }

    /**
     * construct java type -> Jackson Java Type.
     *
     * @param type java type
     * @return JavaType {@link JavaType}
     */
    public static JavaType constructJavaType(Type type) {
        return mapper.constructType(type);
    }

    public static JavaType constructJavaType(Class<?> rawType, JavaType... parameterTypes) {
        return mapper.getTypeFactory().constructParametricType(rawType, parameterTypes);
    }

    public static boolean isValidJsonString(String jsonString) {
        try {
            mapper.readTree(jsonString);
        } catch (JacksonException e) {
            return false;
        }
        return true;
    }

    public static class JsonSerializationException extends RuntimeException {

        private static final String MSG_FOR_SPECIFIED_CLASS = "json serialize/deserialize for class [%s] fail";

        public JsonSerializationException(Class<?> serializedClass, Throwable throwable) {
            super(throwable);
        }

        public JsonSerializationException(Throwable cause) {
            super(cause);
        }
    }
}
