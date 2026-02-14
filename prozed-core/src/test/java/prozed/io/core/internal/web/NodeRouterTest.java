package prozed.io.core.internal.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import prozed.io.core.api.web.HttpMethod;
import prozed.io.core.api.web.PathParam;
import prozed.io.core.api.web.PayloadParam;
import prozed.io.core.internal.utils.ReflectionUtils;

import java.lang.reflect.Method;

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

    public void twoPayloadParamsMethod(@PayloadParam("someParam") String param1, @PayloadParam("someParam2") String param2) {

    }

    public void testMethod1(@PathParam("id") String id) {
    }

    public void testMethod2(@PathParam("name") String id) {
    }

}