package org.orthodoxengineering.restclient.httpurlrestclient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.beanplanet.core.mediatypes.MediaTypes;
import org.beanplanet.core.net.http.*;
import org.beanplanet.testing.beans.TestBean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.orthodoxengineering.restclient.*;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Arrays.asList;
import static org.beanplanet.core.mediatypes.MediaTypes.Application.JSON;
import static org.beanplanet.core.mediatypes.MediaTypes.Application.XML;
import static org.beanplanet.core.net.http.HttpStatusCode.OK;
import static org.beanplanet.core.net.http.HttpStatusCode.Successful.CREATED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
public class GetMethodTest {
    private static HttpUrlRestClient client;

    @BeforeAll
    static void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ObjectMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        client = HttpUrlRestClient.builder().objectMapper(objectMapper).xmlMapper(xmlMapper).build();
    }
    @Test
    void get_uri_noEntity_successful(final WireMockRuntimeInfo wireMockRuntimeInfo) {
        // Given
        stubFor(get("/getmethod").willReturn(ok()));

        // When
        final int statusCode = client.getForStatusCode(wireMockRuntimeInfo.getHttpBaseUrl() + "/getmethod");

        // Then
        assertThat(statusCode, equalTo(OK));
    }

    @Test
    void get_uri_responseHandler_withStringEntity_successful(final WireMockRuntimeInfo wireMockRuntimeInfo) {
        // Given
        stubFor(get("/getmethod").willReturn(ok().withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.Text.PLAIN.getName()).withBody("Hello World!")));

        // When
        final HttpEntity entity = client.get(wireMockRuntimeInfo.getHttpBaseUrl() + "/getmethod", HttpMessage::getEntity);

        // Then
        assertThat(entity, instanceOf(StringEntity.class));
        assertThat(entity.readFullyAsString(), equalTo("Hello World!"));
    }

    @Test
    void get_uri_statusCode_type_withStringEntity_successful(final WireMockRuntimeInfo wireMockRuntimeInfo) {
        // Given
        stubFor(get("/getmethod").willReturn(ok().withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.Text.PLAIN.getName()).withBody("Hello World!")));

        // When
        final String body = client.get(wireMockRuntimeInfo.getHttpBaseUrl() + "/getmethod", OK, String.class);

        // Then
        assertThat(body, equalTo("Hello World!"));
    }

    @Test
    void get_uri_statusCode_type_withStringEntity_failsWhenStatusCodeDoesNotMatchExpected(final WireMockRuntimeInfo wireMockRuntimeInfo) {
        // Given
        stubFor(get("/getmethod").willReturn(badRequest().withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.Text.PLAIN.getName()).withBody("Hello World!")));

        // Then
        assertThrows(RestException.class, () -> client.get(wireMockRuntimeInfo.getHttpBaseUrl() + "/getmethod", OK, String.class));
    }

    @Test
    void get_uri_type__withStringEntity_successful(final WireMockRuntimeInfo wireMockRuntimeInfo) {
        // Given
        stubFor(get("/getmethod").willReturn(ok().withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.Text.PLAIN.getName()).withBody("Hello World!")));

        // When
        final String body = client.get(wireMockRuntimeInfo.getHttpBaseUrl() + "/getmethod", String.class);

        // Then
        assertThat(body, equalTo("Hello World!"));
    }

    @Test
    void get_uri_type__withStringEntity_failsWhenStatusCodeIsNotSuccess(final WireMockRuntimeInfo wireMockRuntimeInfo) {
        // Given
        stubFor(get("/getmethod").willReturn(serverError().withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.Text.PLAIN.getName()).withBody("Hello World!")));

        // Then
        assertThrows(RestException.class, () -> client.get(wireMockRuntimeInfo.getHttpBaseUrl() + "/getmethod", String.class));
    }

    @Test
    void get_uri_responseHandler_withArbitraryDataEntity_successful(final WireMockRuntimeInfo wireMockRuntimeInfo) {
        // Given
        stubFor(get("/getmethod").willReturn(ok().withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.Application.OCTET_STREAM.getName()).withBody("Hello World!")));

        // When
        final HttpEntity entity = client.get(wireMockRuntimeInfo.getHttpBaseUrl() + "/getmethod", HttpMessage::getEntity);

        // Then
        assertThat(entity, instanceOf(ArbitraryDataEntity.class));
        assertThat(entity.readFullyAsString(), equalTo("Hello World!"));
    }

    @Test
    void get_uri_withJsonEntity_successful(final WireMockRuntimeInfo wireMockRuntimeInfo) {
        // Given
        final String body = "{ \"theName\": 12345}";
        stubFor(get("/getmethod").willReturn(ok().withHeader(HttpHeaders.CONTENT_TYPE, JSON.getName()).withBody(body)));

        // When
        final HttpEntity entity = client.get(wireMockRuntimeInfo.getHttpBaseUrl() + "/getmethod", HttpMessage::getEntity);

        // Then
        assertThat(entity, instanceOf(JsonEntity.class));
        assertThat(entity.readFullyAsString(), equalTo(body));
    }

    @Test
    void get_uri_statusCode_type_withJsonEntity_successful(final WireMockRuntimeInfo wireMockRuntimeInfo) throws Exception{
        // Given
        final TestBean expected = new TestBean("theStringProperty");
        stubFor(get("/getmethod").willReturn(ok().withHeader(HttpHeaders.CONTENT_TYPE, JSON.getName()).withBody(new ObjectMapper().writeValueAsString(expected))));

        // When
        final TestBean actual = client.get(wireMockRuntimeInfo.getHttpBaseUrl() + "/getmethod", OK, TestBean.class);

        // Then
        assertThat(actual, equalTo(expected));
    }

    @Test
    void get_uri_statusCode_jacksonType_withJsonEntity_successful(final WireMockRuntimeInfo wireMockRuntimeInfo) {
        // Given
        final String body = "{ \"theName\": 12345}";
        stubFor(get("/getmethod").willReturn(created().withHeader(HttpHeaders.CONTENT_TYPE, JSON.getName()).withBody(body)));

        // When
        final JsonNode actual = client.get(wireMockRuntimeInfo.getHttpBaseUrl() + "/getmethod", CREATED, JsonNode.class);

        // Then
        assertThat(actual, instanceOf(JsonNode.class));
        assertThat(actual, instanceOf(ObjectNode.class));
        assertThat(actual.get("theName"), instanceOf(IntNode.class));
    }

    @Test
    void get_uri_withXmlEntity_successful(final WireMockRuntimeInfo wireMockRuntimeInfo) {
        // Given
        final String body = "<doc><theName>12345</theName></doc>";
        stubFor(get("/getmethod").willReturn(ok().withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.Application.XML.getName()).withBody(body)));

        // When
        final HttpEntity entity = client.get(wireMockRuntimeInfo.getHttpBaseUrl() + "/getmethod", HttpMessage::getEntity);

        // Then
        assertThat(entity, instanceOf(XmlEntity.class));
        assertThat(entity.readFullyAsString(), equalTo(body));
    }

    @Test
    void getForList_uri_type_withJsonEntity_successful(final WireMockRuntimeInfo wireMockRuntimeInfo) throws Exception {
        // Given
        final List<SimpleBean> expected = asList(new SimpleBean("item1"), new SimpleBean("item2"), new SimpleBean("item3"));
        stubFor(get("/getmethod").willReturn(ok().withHeader(HttpHeaders.CONTENT_TYPE, JSON.getName()).withBody(client.getObjectMapper().writeValueAsString(expected))));

        // When
        final List<SimpleBean> actual = client.getForList(wireMockRuntimeInfo.getHttpBaseUrl() + "/getmethod", SimpleBean.class);

        // Then
        assertThat(actual, equalTo(expected));
    }

    @Test
    void getForList_uri_type_withXmlEntity_successful(final WireMockRuntimeInfo wireMockRuntimeInfo) throws Exception {
        // Given
        final List<SimpleBean> expected = asList(new SimpleBean("item1"), new SimpleBean("item2"), new SimpleBean("item3"));
        stubFor(get("/getmethod").willReturn(ok().withHeader(HttpHeaders.CONTENT_TYPE, XML.getName()).withBody(client.getXmlMapper().writeValueAsString(expected))));

        // When
        final List<SimpleBean> actual = client.getForList(wireMockRuntimeInfo.getHttpBaseUrl() + "/getmethod", SimpleBean.class);

        // Then
        assertThat(actual, equalTo(expected));
    }

    @Test
    void getForList_uri_statusCode_type_withJsonEntity_successful(final WireMockRuntimeInfo wireMockRuntimeInfo) throws Exception {
        // Given
        final List<SimpleBean> expected = asList(new SimpleBean("item1"), new SimpleBean("item2"), new SimpleBean("item3"));
        stubFor(get("/getmethod").willReturn(ok().withHeader(HttpHeaders.CONTENT_TYPE, JSON.getName()).withBody(client.getObjectMapper().writeValueAsString(expected))));

        // When
        final List<SimpleBean> actual = client.getForList(wireMockRuntimeInfo.getHttpBaseUrl() + "/getmethod", OK, SimpleBean.class);

        // Then
        assertThat(actual, equalTo(expected));
    }

    @Test
    void getForList_uri_statusCode_type_withXmlEntity_successful(final WireMockRuntimeInfo wireMockRuntimeInfo) throws Exception {
        // Given
        final List<SimpleBean> expected = asList(new SimpleBean("item1"), new SimpleBean("item2"), new SimpleBean("item3"));
        stubFor(get("/getmethod").willReturn(ok().withHeader(HttpHeaders.CONTENT_TYPE, XML.getName()).withBody(client.getXmlMapper().writeValueAsString(expected))));

        // When
        final List<SimpleBean> actual = client.getForList(wireMockRuntimeInfo.getHttpBaseUrl() + "/getmethod", OK, SimpleBean.class);

        // Then
        assertThat(actual, equalTo(expected));
    }
}
