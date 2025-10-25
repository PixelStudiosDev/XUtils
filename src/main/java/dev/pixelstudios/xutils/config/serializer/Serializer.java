package dev.pixelstudios.xutils.config.serializer;

import dev.pixelstudios.xutils.config.Configuration;

public interface Serializer<T> {

    default String serialize(T object) {
        throw new UnsupportedOperationException("Serialization method not implemented for this object type!");
    }

    default void serialize(T object, Configuration config, String path) {
        String serialized = serialize(object);
        config.set(path, serialized);
    }

    default T deserialize(String serialized) {
        throw new UnsupportedOperationException("Deserialization method not implemented for this object type!");
    }

    default T deserialize(Configuration config, String path) {
        String serialized = config.getString(path);
        return serialized != null ? deserialize(serialized) : null;
    }

}

