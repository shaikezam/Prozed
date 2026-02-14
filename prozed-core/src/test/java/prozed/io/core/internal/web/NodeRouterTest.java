package prozed.io.core.internal.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import prozed.io.core.api.web.HttpMethod;
import prozed.io.core.api.web.PayloadParam;
import prozed.io.core.internal.utils.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

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

    public void twoPayloadParamsMethod(@PayloadParam("someParam") String param1, @PayloadParam("someParam2") String param2) {

    }

}