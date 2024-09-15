package org.orthodoxengineering.restclient;

import org.beanplanet.core.net.http.EntityProvider;
import org.beanplanet.core.net.http.HttpRequest;
import org.beanplanet.core.net.http.HttpResponse;

import java.util.List;

/**
 * Definition of a REST client, supporting all the common RESTful HTTP methods.
 *
 * @author Gary Watson
 */
public interface RestClient {
//    HttpResponse request(HttpRequest request);

    default int getForStatusCode(String uri) {
        return get(uri, HttpResponse::getStatusCode);
    }

    /**
     * Makes a GET request to the given endpoint and invokes the given response handler to handle the response.
     *
     * @param uri the URI of the endpoint to call.
     * @param handler the response handler that will be called to handle the response.
     * @return the response returned from invocation the response handler.
     * @throws RestException if the request was not successful or if some other error occurs making the request.
     * @see org.beanplanet.core.net.http.HttpStatusCode#isSuccessful(int)
     */
    <T> T get(String uri, ResponseHandler<T> handler) throws RestException;

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
    <T> T get(String uri, Class<T> responseType) throws RestException;

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
    <T> T get(String uri, int expectedHttpStatusCode, Class<T> responseType);

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
    <T> List<T> getForList(String uri, Class<T> componentType);

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
    <T> List<T> getForList(String uri, int expectedHttpStatusCode, Class<T> componentType);

    //    <T> T get(String uri, Class<T> responseType);

//    <T> T get(RestResponseHandler<T> handler);
//    <T> T put(RestResponseHandler<T> handler);
//    <T> T post(RestResponseHandler<T> handler);
//
//    <T> T get(Class<T> responseType);
//    <T> T get(int expectedHttpStatusCode, Class<T> responseType) throws IllegalStateException;
//    <T> T put(Class<T> responseType);
//    <T> T put(int expectedHttpStatusCode, Class<T> responseType) throws IllegalStateException;
//    <T> T post(Class<T> responseType);
//    <T> T post(int expectedHttpStatusCode, Class<T> responseType) throws IllegalStateException;


    //    /**
//     * Configure the service for requests matching the given predicate.
//     *
//     * @param requestMatcher a matcher of requests to configuration.
//     * @param configuration the configuration which will be applied to requests matching the given request matcher.
//     * @return the rest service for invocation chaining.
//     */
//    RestService config(Predicate<HttpRequest> requestMatcher, Configuration configuration);
//
//    /**
//     * Gets the default configuration which will be applied to requests not accepted by any other request matcher.
//     *
//     * @return the default configuration which will be applied to requests not accepted by any other request matcher.
//     */
//    Configuration config();
//
//    /**
//     * Configure the default configuration which will be applied to requests not accepted by any other request matcher. This is equivalent to a call
//     * <code>configure(true, Configuration)</code>, matching all requests after all other matchers have first been given the opportunity to match first.
//     *
//     * @param configuration the default configuration which will be applied to requests not accepted by any other request matcher.
//     * @return the rest service for invocation chaining.
//     */
//    RestService config(Configuration configuration);
//
//    RestBuilder createRestBuilder();
    class ResetClientBuilder {
        private HttpUrlRestClient.HttpUrlRestClientBuilder delegate = HttpUrlRestClient.builder();

        public ResetClientBuilder entityFactory(EntityProvider entityFactory) {
            delegate.entityFactory(entityFactory);
            return this;
        }

        RestClient build() {
            return delegate.build();
        }
    }
}
