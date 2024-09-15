package org.orthodoxengineering.restclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.beanplanet.core.io.IoException;
import org.beanplanet.core.io.IoUtil;
import org.beanplanet.core.io.resource.ByteArrayOutputStreamResource;
import org.beanplanet.core.lang.conversion.TypeConverter;
import org.beanplanet.core.net.http.EntityProvider;
import org.beanplanet.core.net.http.HttpResponse;
import org.beanplanet.core.util.MultiValueListMapImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUrlRestClient extends AbstractRestClient implements RestClient {
    HttpUrlRestClient(final EntityProvider entityFactory, final TypeConverter typeConverter, final ObjectMapper objectMapper, final ObjectMapper xmlMapper) {
        super(entityFactory, typeConverter, objectMapper, xmlMapper);
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
    @Override
    public <T> T get(String uri, ResponseHandler<T> handler) {
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            final HttpResponse response = new HttpResponse(con.getResponseCode(), new MultiValueListMapImpl<>(con.getHeaderFields()))
                    .withReasonPhrase(con.getResponseMessage());

            ByteArrayOutputStreamResource content = new ByteArrayOutputStreamResource();
            try(InputStream conIs = con.getInputStream()) {
                IoUtil.transfer(conIs, content.getOutputStream());
                return handler.handleResponse(response.withEntity(getEntityFactory().createEntity(response, content)));
            } catch (IOException ignoredEx) {
                return handler.handleResponse(response);
            }
        } catch(IOException ioEx) {
            throw new IoException(ioEx);
        }
    }

//    @Override
//    public HttpResponse request(HttpRequest request) {
//        try {
//            HttpURLConnection con = (HttpURLConnection) request.getRequestUri().toURL().openConnection();
//            con.setRequestMethod("GET");
//            ByteArrayOutputStreamResource baosr = new ByteArrayOutputStreamResource();
//            IoUtil.transfer(con.getInputStream(), baosr.getOutputStream());
//            return new HttpResponse()
//                    .withStatusCode(con.getResponseCode())
//                    .withEntity(baosr);
//        } catch(IOException ioEx) {
//            throw new IoException(ioEx);
//        }
//    }
//
//    @Override
//    public <T> T get(String uri, Class<T> responseType) {
//        try {
//            URL url = new URL(uri);
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setRequestMethod("GET");
//            ByteArrayOutputStreamResource baosr = new ByteArrayOutputStreamResource();
//            IoUtil.transfer(con.getInputStream(), baosr.getOutputStream());
//            return new HttpResponse()
//                    .withStatusCode(con.getResponseCode())
//                    .withEntity(baosr);
//        } catch(IOException ioEx) {
//            throw new IoException(ioEx);
//        }
//    }
//
//    @Override
//    public <T> T get(String uti, int expectedHttpStatusCode, Class<T> responseType) {
//        return null;
//    }

    public static HttpUrlRestClientBuilder builder() {
        return new HttpUrlRestClientBuilder();
    }

    public static class HttpUrlRestClientBuilder {
        private EntityProvider entityFactory = null;
        private TypeConverter typeConverter = null;
        private ObjectMapper objectMapper = null;
        private ObjectMapper xmlMapper = null;

        public HttpUrlRestClientBuilder entityFactory(final EntityProvider entityFactory) {
            this.entityFactory = entityFactory;
            return this;
        }

        public HttpUrlRestClientBuilder typeConverter(final TypeConverter typeConverter) {
            this.typeConverter = typeConverter;
            return this;
        }

        public HttpUrlRestClientBuilder objectMapper(final ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public HttpUrlRestClientBuilder xmlMapper(final ObjectMapper xmlMapper) {
            this.xmlMapper = xmlMapper;
            return this;
        }

        public HttpUrlRestClient build() {
            return new HttpUrlRestClient(entityFactory, typeConverter, objectMapper, xmlMapper);
        }
    }
}
