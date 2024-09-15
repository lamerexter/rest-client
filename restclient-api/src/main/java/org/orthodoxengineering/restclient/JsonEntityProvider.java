package org.orthodoxengineering.restclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.beanplanet.core.io.resource.Resource;
import org.beanplanet.core.mediatypes.MediaTypes;
import org.beanplanet.core.net.http.AbstractRuleMatchingEntityProvider;
import org.beanplanet.core.net.http.EntityMatchers;
import org.beanplanet.core.net.http.HttpEntity;
import org.beanplanet.core.net.http.HttpMessage;

/**
 * A factory for creating HTTP entity bodies using the Jackson library.
 */
public class JsonEntityProvider extends AbstractRuleMatchingEntityProvider {
    private final ObjectMapper objectMapper;

    public JsonEntityProvider() {
        this(new ObjectMapper());
    }

    public JsonEntityProvider(final ObjectMapper objectMapper) {
        super(EntityMatchers.mediaTypes(MediaTypes.Application.JSON.getName()));
        this.objectMapper = objectMapper;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public HttpEntity createEntity(HttpMessage message, Resource content) {
        return new JsonEntity(objectMapper, content);
    }
//
//    public <T> T createObjectForEntity(HttpMessage message, HttpEntity entity, Class<T> clazz) {
//        try {
//            String entityString = EntityUtils.toString(entity, "UTF-8");
//            return clazz.isAssignableFrom(String.class) ? (T)entityString : objectMapper.readValue(entityString, clazz);
//        } catch (IOException ioEx) {
//            throw new RuntimeException(ioEx);
//        }
//    }
}
