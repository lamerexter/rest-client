package org.orthodoxengineering.restclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.beanplanet.core.io.IoException;
import org.beanplanet.core.io.resource.Resource;
import org.beanplanet.core.io.resource.StringResource;
import org.beanplanet.core.lang.conversion.TypeConversionException;
import org.beanplanet.core.net.http.ContentType;
import org.beanplanet.core.net.http.ContentWrappingHttpEntity;

import java.io.IOException;
import java.io.Reader;

import static org.beanplanet.core.mediatypes.MediaTypes.Application.JSON;

/**
 * A very basic HTTP entity which wraps an underlying JSON content resource.
 */
public class JsonEntity extends ContentWrappingHttpEntity {
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();
    private final ObjectMapper objectMapper;

    protected JsonEntity() {
        this((Resource) null);
    }

    /**
     * Creates a JSON HTTP entity which wraps the JSON string content provided.
     *
     * @param content the underlying content of this entity.
     */
    public JsonEntity(final CharSequence content) {
        this(null, content == null ? null : new StringResource(content.toString()));
    }

    /**
     * Creates a JSON HTTP entity which wraps the JSON string content provided.
     *
     * @param objectMapper the type mapper to be used to convert from this entity, which may be null, in which case a system type mapper will be used.
     * @param content       the underlying content of this entity.
     */
    public JsonEntity(final ObjectMapper objectMapper, final CharSequence content) {
        this(objectMapper, content == null ? null : new StringResource(content.toString()));
    }

    /**
     * Creates a JSON HTTP entity which wraps the JSON content provided.
     *
     * @param content entity body content, which must be JSOB compatible.
     */
    public JsonEntity(final Resource content) {
        this(content, ContentType.from(JSON));
    }

    /**
     * Creates a JSON HTTP entity which wraps the JSON content provided.
     *
     * @param objectMapper the type mapper to be used to convert from this entity, which may be null, in which case the system type mapper will be used.
     * @param content       entity body content, which must be JSOB compatible.
     */
    public JsonEntity(final ObjectMapper objectMapper, final Resource content) {
        this(objectMapper, content, ContentType.from(JSON));
    }

    /**
     * Creates a JSON HTTP entity which wraps the JSON content provided.
     *
     * @param content     entity body content, which must be JSOB compatible.
     * @param contentType a JSON compatible content type, such as <code>application/json</code>
     */
    public JsonEntity(final Resource content, ContentType contentType) {
        this(null, content, contentType);
    }

    /**
     * Creates a JSON HTTP entity which wraps the JSON content provided.
     *
     * @param objectMapper the type mapper to be used to convert from this entity, which may be null, in which case the system type mapper will be used.
     * @param content       entity body content, which must be JSOB compatible.
     * @param contentType   a JSON compatible content type, such as <code>application/json</code>
     */
    public JsonEntity(final ObjectMapper objectMapper, final Resource content, ContentType contentType) {
        super(content, contentType);
        this.objectMapper = (objectMapper != null ? objectMapper : DEFAULT_MAPPER);
    }

    /**
     * Reads the content of the JSON entity, converting to the given type, if possible.
     *
     * @param type the type toconvert the entity body to.
     * @return an instance of the type requested.
     * @throws TypeConversionException if an error occurs converting the entity to the requested type.
     */
    public <T> T readContentAs(final Class<T> type) throws TypeConversionException {
        try (final Reader bodyReader = getContent().getReader()) {
            return objectMapper.readValue(bodyReader, type);
        } catch (IOException ioEx) {
            throw new TypeConversionException("Failed to convert the JSON entity to the requested type ["+type+"]: ", ioEx);
        }
    }
}
