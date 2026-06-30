package prozed.io.core.internal.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import prozed.io.core.api.exception.HttpException;
import prozed.io.core.api.web.HttpMethod;
import prozed.io.core.api.web.PathParam;
import prozed.io.core.api.web.PayloadParam;
import prozed.io.test.utils.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NodeRouterTest {

    @InjectMocks
    private NodeRouter router;

    @Test
    void testValidatePayloadParamCount() {
        // given
        String path = "/test";
        Method method = ReflectionUtils.getMethod(NodeRouterTest.class, "twoPayloadParamsMethod", String.class, String.class);

        // when & then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> router.addRoute(path, method, HttpMethod.GET)
        );

        assertTrue(exception.getMessage().contains("has 2 @PayloadParam annotations"));
    }

    @Test
    void testAmbiguousWildcard() {
        // given
        Method method1 = ReflectionUtils.getMethod(NodeRouterTest.class, "testMethod1", String.class);
        Method method2 = ReflectionUtils.getMethod(NodeRouterTest.class, "testMethod2", String.class);
        router.addRoute("/test/{id}", method1, HttpMethod.GET);
        // when & then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> router.addRoute("/test/{name}", method2, HttpMethod.GET)
        );

        assertTrue(exception.getMessage().contains("Ambiguous wildcard route: path '/test/{name}' conflicts with existing wildcard '{id}' at segment '{name}'"));
    }

    @Test
    void testSameMethodAlreadyExists() {
        // given
        String path = "/test";
        Method method1 = ReflectionUtils.getMethod(NodeRouterTest.class, "testMethod1", String.class);
        Method method2 = ReflectionUtils.getMethod(NodeRouterTest.class, "testMethod2", String.class);
        router.addRoute(path, method1, HttpMethod.GET);
        // when & then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> router.addRoute(path, method2, HttpMethod.GET)
        );

        assertTrue(exception.getMessage().contains("path {/test} with method {GET} already exists"));
    }

    @Test
    void testSameMethodWithDifferentHttpMethod() {
        // given
        String path = "/test";
        Method method1 = ReflectionUtils.getMethod(NodeRouterTest.class, "testMethod1", String.class);
        Method method2 = ReflectionUtils.getMethod(NodeRouterTest.class, "testMethod2", String.class);
        router.addRoute(path, method1, HttpMethod.GET);
        // when
        router.addRoute(path, method2, HttpMethod.DELETE);

        // then
        Node root = (Node) ReflectionUtils.getField(router, "root");
        assertNotNull(root);
    }

    @Test
    void testLookupPrefixNodeNo404() {
        // given
        Method method = ReflectionUtils.getMethod(NodeRouterTest.class, "testMethod1", String.class);
        router.addRoute("/user/{id}", method, HttpMethod.GET);

        // when
        HttpException ex = assertThrows(
                HttpException.class,
                () -> router.lookup(HttpMethod.GET, "/user", new HashMap<>())
        );

        // then
        assertEquals(HttpServletResponse.SC_NOT_FOUND, ex.getHttpCode());
        assertTrue(ex.getMessage().contains("path not found"));
    }

    @Test
    void testLookupWrongMethod405() {
        // given
        Method method = ReflectionUtils.getMethod(NodeRouterTest.class, "testMethod1", String.class);
        router.addRoute("/user", method, HttpMethod.POST);

        // when
        HttpException ex = assertThrows(
                HttpException.class,
                () -> router.lookup(HttpMethod.GET, "/user", new HashMap<>())
        );

        // then
        assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, ex.getHttpCode());
        assertTrue(ex.getMessage().contains("method not found"));
    }

    @Test
    void testUnboundParam() {
        // given
        Method method = ReflectionUtils.getMethod(NodeRouterTest.class, "unboundParamMethod", int.class);

        // when
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> router.addRoute("/test", method, HttpMethod.GET)
        );

        // then
        assertTrue(exception.getMessage().contains("Unbound parameter"));
    }

    @Test
    void testZeroParamMethodIsValid() {
        // given
        Method method = ReflectionUtils.getMethod(NodeRouterTest.class, "zeroParamMethod");

        // when
        router.addRoute("/health", method, HttpMethod.GET);

        // then
        assertNotNull(ReflectionUtils.getField(router, "root"));
    }

    @Test
    void testHttpRequestParamIsValid() {
        // given
        Method method = ReflectionUtils.getMethod(NodeRouterTest.class, "httpRequestServlet", HttpServletRequest.class);

        // when
        router.addRoute("/test", method, HttpMethod.GET);

        // then
        assertNotNull(ReflectionUtils.getField(router, "root"));
    }

    @Test
    void testHttpResponseParamIsValid() {
        // given
        Method method = ReflectionUtils.getMethod(NodeRouterTest.class, "httpResponseServlet", HttpServletResponse.class);

        // when
        router.addRoute("/test", method, HttpMethod.GET);

        // then
        assertNotNull(ReflectionUtils.getField(router, "root"));
    }

    @Test
    void testBothHttpParamsIsValid() {
        // given
        Method method = ReflectionUtils.getMethod(NodeRouterTest.class, "bothHttpParams", HttpServletRequest.class, HttpServletResponse.class);

        // when
        router.addRoute("/test", method, HttpMethod.GET);

        // then
        assertNotNull(ReflectionUtils.getField(router, "root"));
    }

    @Test
    void testHttpParamWithPathParamIsValid() {
        // given
        Method method = ReflectionUtils.getMethod(NodeRouterTest.class, "httpWithPathParam", String.class, HttpServletRequest.class);

        // when
        router.addRoute("/test/{id}", method, HttpMethod.GET);

        // then
        assertNotNull(ReflectionUtils.getField(router, "root"));
    }

    public void twoPayloadParamsMethod(@PayloadParam String param1, @PayloadParam String param2) {

    }

    public void unboundParamMethod(int page) {
    }

    public void httpRequestServlet(HttpServletRequest request) {
    }

    public void httpResponseServlet(HttpServletResponse response) {
    }

    public void bothHttpParams(HttpServletRequest request, HttpServletResponse response) {
    }

    public void httpWithPathParam(@PathParam("id") String id, HttpServletRequest request) {
    }

    public void zeroParamMethod() {
    }

    public void testMethod1(@PathParam("id") String id) {
    }

    public void testMethod2(@PathParam("name") String id) {
    }

}