package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class BaseClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private TestClient testClient;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        testClient = new TestClient(restTemplate);
    }

    @Test
    void testGetMethods() {
        mockServer.expect(requestTo("/test"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"key\":\"value\"}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = testClient.testGet("/test");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        mockServer.reset();

        mockServer.expect(requestTo("/test/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "123"))
                .andRespond(withSuccess("{\"key\":\"value\"}", MediaType.APPLICATION_JSON));

        response = testClient.testGetWithUser("/test/1", 123L);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetWithParamsAndErrorHandling() {
        mockServer.expect(requestTo("/test?id=1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withBadRequest().body("Error message"));

        ResponseEntity<Object> response = testClient.testGetWithParams("/test?id=1", null, Map.of("id", 1));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testPostMethods() {
        mockServer.expect(requestTo("/post"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string("body"))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .body("{\"status\":\"created\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = testClient.testPostNoUserNoParams("/post", "body");
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        mockServer.reset();

        mockServer.expect(requestTo("/post/user"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().string("body"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        response = testClient.testPostWithUser("/post/user", 1L, "body");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        mockServer.reset();

        mockServer.expect(requestTo("/post/params"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        response = testClient.testPostWithParams("/post/params", 1L, Map.of(), "body");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testPutMethods() {
        mockServer.expect(requestTo("/put"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = testClient.testPut("/put", 1L, "body");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        mockServer.reset();

        mockServer.expect(requestTo("/put/params"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        response = testClient.testPutWithParams("/put/params", 1L, Map.of(), "body");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testPatchMethods() {
        mockServer.expect(requestTo("/patch"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = testClient.testPatchNoUserNoParams("/patch", "body");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        mockServer.reset();

        mockServer.expect(requestTo("/patch/user"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "2"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        response = testClient.testPatchWithUserOnly("/patch/user", 2L);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        mockServer.reset();

        mockServer.expect(requestTo("/patch/user/body"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "2"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        response = testClient.testPatchWithUserAndBody("/patch/user/body", 2L, "body");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        mockServer.reset();

        mockServer.expect(requestTo("/patch/all"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "2"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        response = testClient.testPatchAll("/patch/all", 2L, Map.of(), "body");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteMethods() {
        mockServer.expect(requestTo("/delete"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = testClient.testDelete("/delete");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        mockServer.reset();

        mockServer.expect(requestTo("/delete/user"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header("X-Sharer-User-Id", "3"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        response = testClient.testDeleteWithUser("/delete/user", 3L);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        mockServer.reset();

        mockServer.expect(requestTo("/delete/all"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header("X-Sharer-User-Id", "3"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        response = testClient.testDeleteAll("/delete/all", 3L, Map.of());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testPrepareGatewayResponseWithNon2xxAndNoBody() {
        mockServer.expect(requestTo("/error"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity<Object> response = testClient.testGet("/error");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals(new byte[0], (byte[]) response.getBody());
    }

    private static class TestClient extends BaseClient {
        public TestClient(RestTemplate rest) {
            super(rest);
        }

        public ResponseEntity<Object> testGet(String path) {
            return get(path);
        }

        public ResponseEntity<Object> testGetWithUser(String path, long userId) {
            return get(path, userId);
        }

        public ResponseEntity<Object> testGetWithParams(String path, Long userId, Map<String, Object> params) {
            return get(path, userId, params);
        }

        public <T> ResponseEntity<Object> testPostNoUserNoParams(String path, T body) {
            return post(path, body);
        }

        public <T> ResponseEntity<Object> testPostWithUser(String path, long userId, T body) {
            return post(path, userId, body);
        }

        public <T> ResponseEntity<Object> testPostWithParams(String path, Long userId, Map<String, Object> params, T body) {
            return post(path, userId, params, body);
        }

        public <T> ResponseEntity<Object> testPut(String path, long userId, T body) {
            return put(path, userId, body);
        }

        public <T> ResponseEntity<Object> testPutWithParams(String path, long userId, Map<String, Object> params, T body) {
            return put(path, userId, params, body);
        }

        public <T> ResponseEntity<Object> testPatchNoUserNoParams(String path, T body) {
            return patch(path, body);
        }

        public ResponseEntity<Object> testPatchWithUserOnly(String path, long userId) {
            return patch(path, userId);
        }

        public <T> ResponseEntity<Object> testPatchWithUserAndBody(String path, long userId, T body) {
            return patch(path, userId, body);
        }

        public <T> ResponseEntity<Object> testPatchAll(String path, Long userId, Map<String, Object> params, T body) {
            return patch(path, userId, params, body);
        }

        public ResponseEntity<Object> testDelete(String path) {
            return delete(path);
        }

        public ResponseEntity<Object> testDeleteWithUser(String path, long userId) {
            return delete(path, userId);
        }

        public ResponseEntity<Object> testDeleteAll(String path, Long userId, Map<String, Object> params) {
            return delete(path, userId, params);
        }
    }
}
