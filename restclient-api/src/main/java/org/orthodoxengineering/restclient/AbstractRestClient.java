package org.orthodoxengineering.restclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.beanplanet.core.lang.Assert;
import org.beanplanet.core.lang.conversion.TypeConverter;
import org.beanplanet.core.net.http.*;

import java.lang.reflect.Array;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static org.beanplanet.core.lang.conversion.SystemTypeConverter.systemTypeConverter;

public abstract class AbstractRestClient implements RestClient {
    private final EntityProvider entityFactory;
    private final TypeConverter typeConverter;
    private final ObjectMapper objectMapper;
    private final ObjectMapper xmlMapper;

    AbstractRestClient(final EntityProvider entityFactory, final TypeConverter typeConverter, final ObjectMapper objectMapper, final ObjectMapper xmlMapper) {
        this.typeConverter = typeConverter != null ? typeConverter : systemTypeConverter();
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
        this.xmlMapper = xmlMapper != null ? xmlMapper : new XmlMapper();
        this.entityFactory = entityFactory != null ? entityFactory :  new EntityProviderRegistry()
                .addProvider(new JsonEntityProvider(objectMapper))
                .addProvider(new XmlEntityProvider(xmlMapper))
                .addProvider(new StringEntityProvider())
                .addProvider(new ArbitraryDataEntityProvider(".*/.*"));
    }

    AbstractRestClient(final EntityProvider entityFactory) {
        this(entityFactory, null, null, null);
    }

    public EntityProvider getEntityFactory() {
        return entityFactory;
    }

    public TypeConverter getTypeConverter() {
        return typeConverter;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public ObjectMapper getXmlMapper() {
        return xmlMapper;
    }

    /**
     * Makes a GET request to the given endpoint and converts the response to the given type. The request
     * is considered 'successful' if the response code lies within the <code>2xx</code> range of success codes, as determined by <a href="https://httpwg.org/specs/rfc9110.html#overview.of.status.codes">RFC 9110 - HTTP Semantics</a> and by an
     * associated call to {@link org.beanplanet.core.net.http.HttpStatusCode#isSuccessful(int)}.
     *
     * @param uri the URI of the endpoint to call.
     * @param responseType the type to convert the body of the successful response to.
     * @return the response type.
     * @throws RestException if the request was not successful or if some other error occurs making the request.
     * @see org.beanplanet.core.net.http.HttpStatusCode#isSuccessful(int)
     */
    @Override
    public <T> T get(String uri, Class<T> responseType) throws RestException {
        return get(uri, r -> { checkStatusCode(r, HttpStatusCode::isSuccessful); return getTypeConverter().convert(r.getEntity(), responseType);});
    }

    /**
     * Makes a GET request to the given endpoint and converts the response to the given type. The request
     * is considered 'successful' if the response code is <code>expectedHttpStatusCode</code>.
     *
     * @param uri the URI of the endpoint to call.
     * @param expectedHttpStatusCode the component type of the list to convert the body of the successful response to.
     * @param responseType the type to convert the body of the successful response to.
     * @return the response type.
     * @throws RestException if the request was not successful or if some other error occurs making the request.
     * @see org.beanplanet.core.net.http.HttpStatusCode#isSuccessful(int)
     */
    @Override
    public <T> T get(String uri, int expectedHttpStatusCode, Class<T> responseType) {
        return get(uri, r -> { checkStatusCode(r, expectedHttpStatusCode); return getTypeConverter().convert(r.getEntity(), responseType);});
    }

    /**
     * Makes a GET request to the given endpoint and converts the response to a list of the given component type. The request
     * is considered 'successful' if the response code lies within the <code>2xx</code> range of success codes, as determined by <a href="https://httpwg.org/specs/rfc9110.html#overview.of.status.codes">RFC 9110 - HTTP Semantics</a> and by an
     * associated call to {@link org.beanplanet.core.net.http.HttpStatusCode#isSuccessful(int)}.
     *
     * @param uri the URI of the endpoint to call.
     * @param componentType the component type of the list to convert the body of the successful response to.
     * @return a list of the given component type.
     * @throws RestException if the request was not successful or if some other error occurs making the request.
     * @see org.beanplanet.core.net.http.HttpStatusCode#isSuccessful(int)
     */
    @Override
    public <T> List<T> getForList(String uri, Class<T> componentType) {
        final HttpResponse response = get(uri, r -> r);
        Assert.assertTrue(HttpStatusCode.isSuccessful(response.getStatusCode()), () -> "Expected 'successful' HTTP response code: actual = " + response.getStatusCode());

        return asList((T[])getTypeConverter().convert(response.getEntity(), ((T[])Array.newInstance(componentType, 0)).getClass()));
    }

    /**
     * Makes a GET request to the given endpoint and converts the response to a list of the given component type. The request
     * is considered 'successful' if the response code is <code>expectedHttpStatusCode</code>.
     *
     * @param uri the URI of the endpoint to call.
     * @param expectedHttpStatusCode the component type of the list to convert the body of the successful response to.
     * @return a list of the given component type.
     * @throws RestException if the request was not successful or if some other error occurs making the request.
     * @see org.beanplanet.core.net.http.HttpStatusCode#isSuccessful(int)
     */
    @Override
    public <T> List<T> getForList(String uri, int expectedHttpStatusCode, Class<T> componentType) {
        return get(uri, r -> { checkStatusCode(r, expectedHttpStatusCode); return asList((T[])getTypeConverter().convert(r.getEntity(), ((T[])Array.newInstance(componentType, 0)).getClass()));});
    }

    private void checkStatusCode(HttpResponse response, Predicate<Integer> statusCodeCheck) {
        if ( statusCodeCheck.test(response.getStatusCode()) ) return;

        throw new RestException("Expected 'successful' HTTP response code, but received " + response.getStatusCode());
    }

    private void checkStatusCode(HttpResponse response, int expectedHttpStatusCode) {
        if ( expectedHttpStatusCode == response.getStatusCode() ) return;

        throw new RestException("Unexpected HTTP response code: expected = " + expectedHttpStatusCode + ", actual = " + response.getStatusCode());
    }
}
