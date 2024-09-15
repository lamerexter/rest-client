package org.orthodoxengineering.restclient.typeconverters;

import org.beanplanet.core.lang.conversion.annotations.TypeConverter;
import org.beanplanet.core.net.http.HttpEntity;
import org.beanplanet.core.net.http.StringEntity;
import org.orthodoxengineering.restclient.JsonEntity;
import org.orthodoxengineering.restclient.XmlEntity;

import java.lang.reflect.Array;
import java.util.stream.Stream;

/**
 * Http entity content type converters.
 */
@TypeConverter
public class EntityTypeConverters {

    // TODO: Remove this as only needed because the system default 'String' type converter (based on Object::toString) kicks in first!
    //       Can remove after priority-based type converters are implemented
    @TypeConverter
    public static String stringEntityToString(StringEntity entity) {
        return (entity == null ? null : entity.readFullyAsString());
    }

    @TypeConverter
    public static String entityToString(HttpEntity entity) {
        return (entity == null ? null : entity.readFullyAsString());
    }

    // TODO: Remove this as only needed because the system default 'String' type converter (based on Object::toString) kicks in first!
    //       Can remove after priority-based type converters are implemented
    @TypeConverter
    public static String jsonEntityToString(JsonEntity entity) {
        return (entity == null ? null : entity.readFullyAsString());
    }

    /**
     * Converts the specified JSON entity to the given target type.
     *
     * @param entity     the entity to be converted.
     * @param targetType the target type to be created, which is assumed compatible with and capable of being converted from a JSON representation.
     */
    @SuppressWarnings("unchecked")
    @TypeConverter
    public static <T> T jsonEntityToArbitraryType(final JsonEntity entity, final Class<T> targetType) {
        return (entity == null ? null : entity.readContentAs(targetType));
    }

    // TODO: Remove this as only needed because the system default 'String' type converter (based on Object::toString) kicks in first!
    //       Can remove after priority-based type converters are implemented
    @TypeConverter
    public static String xmlEntityToString(XmlEntity entity) {
        return (entity == null ? null : entity.readFullyAsString());
    }

    /**
     * Converts the specified JSON entity to the given target type.
     *
     * @param entity     the entity to be converted.
     * @param targetType the target type to be created, which is assumed compatible with and capable of being converted from a JSON representation.
     */
    @SuppressWarnings("unchecked")
    @TypeConverter
    public static <T> T xmlEntityToArbitraryType(final XmlEntity entity, final Class<T> targetType) {
        return (entity == null ? null : entity.readContentAs(targetType));
    }
}
