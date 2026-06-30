package prozed.io.core.internal.web;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import prozed.io.core.api.web.PathParam;
import prozed.io.core.api.web.PayloadParam;
import prozed.io.core.api.web.QueryParam;
import prozed.io.test.utils.RandomUtils;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeExecutorWrapperTest {

    public static final String TEMP_HEADER = "temp";
    private final Gson gson = new Gson();

    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;

    @Test
    void testExecuteWithPathParamInteger() throws Exception {
        // given
        Integer input = RandomUtils.randomInt();
        Map<String, String> pathParams = Map.of("id", Integer.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethod", Integer.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithPathParamInt() throws Exception {
        // given
        int input = RandomUtils.randomInt();
        Map<String, String> pathParams = Map.of("id", Integer.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethod", int.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamInteger() throws Exception {
        // given
        Integer input = RandomUtils.randomInt();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Integer.toString(input));
        Method method = TestController.class.getMethod("queryParamMethod", Integer.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamInt() throws Exception {
        // given
        int input = RandomUtils.randomInt();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Integer.toString(input));
        Method method = TestController.class.getMethod("queryParamMethod", int.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    // double

    @Test
    void testExecuteWithPathParamDouble() throws Exception {
        // given
        Double input = RandomUtils.randomDouble();
        Map<String, String> pathParams = Map.of("id", Double.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethod", Double.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithPathParamDoublePrimitive() throws Exception {
        // given
        double input = RandomUtils.randomDouble();
        Map<String, String> pathParams = Map.of("id", Double.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethod", double.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamDouble() throws Exception {
        // given
        Double input = RandomUtils.randomDouble();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Double.toString(input));
        Method method = TestController.class.getMethod("queryParamMethod", Double.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamDoublePrimitive() throws Exception {
        // given
        double input = RandomUtils.randomDouble();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Double.toString(input));
        Method method = TestController.class.getMethod("queryParamMethod", double.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    // long

    @Test
    void testExecuteWithPathParamLong() throws Exception {
        // given
        Long input = RandomUtils.randomLong();
        Map<String, String> pathParams = Map.of("id", Long.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethod", Long.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithPathParamLongPrimitive() throws Exception {
        // given
        long input = RandomUtils.randomLong();
        Map<String, String> pathParams = Map.of("id", Long.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethod", long.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamLong() throws Exception {
        // given
        Long input = RandomUtils.randomLong();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Long.toString(input));
        Method method = TestController.class.getMethod("queryParamMethod", Long.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamLongPrimitive() throws Exception {
        // given
        long input = RandomUtils.randomLong();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Long.toString(input));
        Method method = TestController.class.getMethod("queryParamMethod", long.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    // float

    @Test
    void testExecuteWithPathParamFloat() throws Exception {
        // given
        Float input = RandomUtils.randomFloat();
        Map<String, String> pathParams = Map.of("id", Float.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethod", Float.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithPathParamFloatPrimitive() throws Exception {
        // given
        float input = RandomUtils.randomFloat();
        Map<String, String> pathParams = Map.of("id", Float.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethod", float.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamFloat() throws Exception {
        // given
        Float input = RandomUtils.randomFloat();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Float.toString(input));
        Method method = TestController.class.getMethod("queryParamMethod", Float.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamFloatPrimitive() throws Exception {
        // given
        float input = RandomUtils.randomFloat();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Float.toString(input));
        Method method = TestController.class.getMethod("queryParamMethod", float.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    // boolean

    @Test
    void testExecuteWithPathParamBoolean() throws Exception {
        // given
        Boolean input = RandomUtils.randomBoolean();
        Map<String, String> pathParams = Map.of("id", Boolean.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethod", Boolean.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithPathParamBooleanPrimitive() throws Exception {
        // given
        boolean input = RandomUtils.randomBoolean();
        Map<String, String> pathParams = Map.of("id", Boolean.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethod", boolean.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamBoolean() throws Exception {
        // given
        Boolean input = RandomUtils.randomBoolean();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Boolean.toString(input));
        Method method = TestController.class.getMethod("queryParamMethod", Boolean.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamBooleanPrimitive() throws Exception {
        // given
        boolean input = RandomUtils.randomBoolean();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Boolean.toString(input));
        Method method = TestController.class.getMethod("queryParamMethod", boolean.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    // character

    @Test
    void testExecuteWithPathParamCharacter() throws Exception {
        // given
        Character input = RandomUtils.randomCharacter();
        Map<String, String> pathParams = Map.of("id", Character.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethod", Character.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithPathParamCharacterPrimitive() throws Exception {
        // given
        char input = RandomUtils.randomCharacter();
        Map<String, String> pathParams = Map.of("id", Character.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethod", char.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamCharacter() throws Exception {
        // given
        Character input = RandomUtils.randomCharacter();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Character.toString(input));
        Method method = TestController.class.getMethod("queryParamMethod", Character.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamCharacterPrimitive() throws Exception {
        // given
        char input = RandomUtils.randomCharacter();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Character.toString(input));
        Method method = TestController.class.getMethod("queryParamMethod", char.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    // string

    @Test
    void testExecuteWithPathParamString() throws Exception {
        // given
        String input = RandomUtils.randomAlphbetString(10);
        Map<String, String> pathParams = Map.of("id", input);
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethod", String.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamString() throws Exception {
        // given
        String input = RandomUtils.randomAlphbetString(10);
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", input);
        Method method = TestController.class.getMethod("queryParamMethod", String.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithPayloadParam() throws Exception {
        // given
        TestPayload input = new TestPayload(
                RandomUtils.randomInt(),
                RandomUtils.randomAlphbetString(10)
        );
        Method method = TestController.class.getMethod("queryPayloadMethod", TestPayload.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(Map.of(), Map.of(), method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, gson.toJson(input), req, resp, gson);

        // then
        assertEquals(input, result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithMultipleParams() throws Exception {
        // given
        TestPayload payloadParam = new TestPayload(
                RandomUtils.randomInt(),
                RandomUtils.randomAlphbetString(10)
        );
        Long pathParam = RandomUtils.randomLong();
        Boolean queryParam = RandomUtils.randomBoolean();
        Map<String, String> pathParams = Map.of("var1", Long.toString(pathParam));
        Map<String, String> queryParams = Map.of("var2", Boolean.toString(queryParam));
        Method method = TestController.class.getMethod("mixMethod", Long.class, Boolean.class, TestPayload.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, gson.toJson(payloadParam), req, resp, gson);

        // then
        assertEquals(payloadParam.id(), result);
        verifyNoInteractions(req, resp);
    }

    @Test
    void testExecuteWithPathParamIntegerWithHttpServlet() throws Exception {
        // given
        Integer input = RandomUtils.randomInt();
        Map<String, String> pathParams = Map.of("id", Integer.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethodWithHttpServlet", Integer.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithPathParamIntWithHttpServlet() throws Exception {
        // given
        int input = RandomUtils.randomInt();
        Map<String, String> pathParams = Map.of("id", Integer.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethodWithHttpServlet", int.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamIntegerWithHttpServlet() throws Exception {
        // given
        Integer input = RandomUtils.randomInt();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Integer.toString(input));
        Method method = TestController.class.getMethod("queryParamMethodWithHttpServlet", Integer.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamIntWithHttpServlet() throws Exception {
        // given
        int input = RandomUtils.randomInt();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Integer.toString(input));
        Method method = TestController.class.getMethod("queryParamMethodWithHttpServlet", int.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    // double

    @Test
    void testExecuteWithPathParamDoubleWithHttpServlet() throws Exception {
        // given
        Double input = RandomUtils.randomDouble();
        Map<String, String> pathParams = Map.of("id", Double.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethodWithHttpServlet", Double.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithPathParamDoublePrimitiveWithHttpServlet() throws Exception {
        // given
        double input = RandomUtils.randomDouble();
        Map<String, String> pathParams = Map.of("id", Double.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethodWithHttpServlet", double.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamDoubleWithHttpServlet() throws Exception {
        // given
        Double input = RandomUtils.randomDouble();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Double.toString(input));
        Method method = TestController.class.getMethod("queryParamMethodWithHttpServlet", Double.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamDoublePrimitiveWithHttpServlet() throws Exception {
        // given
        double input = RandomUtils.randomDouble();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Double.toString(input));
        Method method = TestController.class.getMethod("queryParamMethodWithHttpServlet", double.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    // long

    @Test
    void testExecuteWithPathParamLongWithHttpServlet() throws Exception {
        // given
        Long input = RandomUtils.randomLong();
        Map<String, String> pathParams = Map.of("id", Long.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethodWithHttpServlet", Long.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithPathParamLongPrimitiveWithHttpServlet() throws Exception {
        // given
        long input = RandomUtils.randomLong();
        Map<String, String> pathParams = Map.of("id", Long.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethodWithHttpServlet", long.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamLongWithHttpServlet() throws Exception {
        // given
        Long input = RandomUtils.randomLong();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Long.toString(input));
        Method method = TestController.class.getMethod("queryParamMethodWithHttpServlet", Long.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamLongPrimitiveWithHttpServlet() throws Exception {
        // given
        long input = RandomUtils.randomLong();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Long.toString(input));
        Method method = TestController.class.getMethod("queryParamMethodWithHttpServlet", long.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    // float

    @Test
    void testExecuteWithPathParamFloatWithHttpServlet() throws Exception {
        // given
        Float input = RandomUtils.randomFloat();
        Map<String, String> pathParams = Map.of("id", Float.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethodWithHttpServlet", Float.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithPathParamFloatPrimitiveWithHttpServlet() throws Exception {
        // given
        float input = RandomUtils.randomFloat();
        Map<String, String> pathParams = Map.of("id", Float.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethodWithHttpServlet", float.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamFloatWithHttpServlet() throws Exception {
        // given
        Float input = RandomUtils.randomFloat();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Float.toString(input));
        Method method = TestController.class.getMethod("queryParamMethodWithHttpServlet", Float.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamFloatPrimitiveWithHttpServlet() throws Exception {
        // given
        float input = RandomUtils.randomFloat();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Float.toString(input));
        Method method = TestController.class.getMethod("queryParamMethodWithHttpServlet", float.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    // boolean

    @Test
    void testExecuteWithPathParamBooleanWithHttpServlet() throws Exception {
        // given
        Boolean input = RandomUtils.randomBoolean();
        Map<String, String> pathParams = Map.of("id", Boolean.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethodWithHttpServlet", Boolean.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithPathParamBooleanPrimitiveWithHttpServlet() throws Exception {
        // given
        boolean input = RandomUtils.randomBoolean();
        Map<String, String> pathParams = Map.of("id", Boolean.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethodWithHttpServlet", boolean.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamBooleanWithHttpServlet() throws Exception {
        // given
        Boolean input = RandomUtils.randomBoolean();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Boolean.toString(input));
        Method method = TestController.class.getMethod("queryParamMethodWithHttpServlet", Boolean.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamBooleanPrimitiveWithHttpServlet() throws Exception {
        // given
        boolean input = RandomUtils.randomBoolean();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Boolean.toString(input));
        Method method = TestController.class.getMethod("queryParamMethodWithHttpServlet", boolean.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    // character

    @Test
    void testExecuteWithPathParamCharacterWithHttpServlet() throws Exception {
        // given
        Character input = RandomUtils.randomCharacter();
        Map<String, String> pathParams = Map.of("id", Character.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethodWithHttpServlet", Character.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithPathParamCharacterPrimitiveWithHttpServlet() throws Exception {
        // given
        char input = RandomUtils.randomCharacter();
        Map<String, String> pathParams = Map.of("id", Character.toString(input));
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethodWithHttpServlet", char.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamCharacterWithHttpServlet() throws Exception {
        // given
        Character input = RandomUtils.randomCharacter();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Character.toString(input));
        Method method = TestController.class.getMethod("queryParamMethodWithHttpServlet", Character.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamCharacterPrimitiveWithHttpServlet() throws Exception {
        // given
        char input = RandomUtils.randomCharacter();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Character.toString(input));
        Method method = TestController.class.getMethod("queryParamMethodWithHttpServlet", char.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    // string

    @Test
    void testExecuteWithPathParamStringWithHttpServlet() throws Exception {
        // given
        String input = RandomUtils.randomAlphbetString(10);
        Map<String, String> pathParams = Map.of("id", input);
        Map<String, String> queryParams = Map.of();
        Method method = TestController.class.getMethod("pathParamMethodWithHttpServlet", String.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, input);
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithQueryParamStringWithHttpServlet() throws Exception {
        // given
        String input = RandomUtils.randomAlphbetString(10);
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", input);
        Method method = TestController.class.getMethod("queryParamMethodWithHttpServlet", String.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, input);
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithPayloadParamWithHttpServlet() throws Exception {
        // given
        TestPayload input = new TestPayload(
                RandomUtils.randomInt(),
                RandomUtils.randomAlphbetString(10)
        );
        Method method = TestController.class.getMethod("queryPayloadMethodWithHttpServlet", TestPayload.class, HttpServletResponse.class, HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(Map.of(), Map.of(), method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, gson.toJson(input), req, resp, gson);

        // then
        assertEquals(input, result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(input));
        verifyNoMoreInteractions(req, resp);
    }

    @Test
    void testExecuteWithMultipleParamsWithHttpServlet() throws Exception {
        // given
        TestPayload payloadParam = new TestPayload(
                RandomUtils.randomInt(),
                RandomUtils.randomAlphbetString(10)
        );
        Long pathParam = RandomUtils.randomLong();
        Boolean queryParam = RandomUtils.randomBoolean();
        Map<String, String> pathParams = Map.of("var1", Long.toString(pathParam));
        Map<String, String> queryParams = Map.of("var2", Boolean.toString(queryParam));
        Method method = TestController.class.getMethod(
                "mixMethodWithHttpServlet",
                Long.class,
                Boolean.class,
                TestPayload.class,
                HttpServletResponse.class,
                HttpServletRequest.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, gson.toJson(payloadParam), req, resp, gson);

        // then
        assertEquals(payloadParam.id(), result);
        verify(req).getMethod();
        verify(resp).addHeader(TEMP_HEADER, String.valueOf(pathParam));
        verifyNoMoreInteractions(req, resp);
    }

    // Helper test controller class
    static class TestController {
        // int
        public Integer pathParamMethod(@PathParam("id") Integer param) {
            return param;
        }

        public int pathParamMethod(@PathParam("id") int param) {
            return param;
        }

        public Integer queryParamMethod(@QueryParam("id") Integer param) {
            return param;
        }

        public int queryParamMethod(@QueryParam("id") int param) {
            return param;
        }

        // double

        public Double pathParamMethod(@PathParam("id") Double param) {
            return param;
        }

        public double pathParamMethod(@PathParam("id") double param) {
            return param;
        }

        public Double queryParamMethod(@QueryParam("id") Double param) {
            return param;
        }

        public double queryParamMethod(@QueryParam("id") double param) {
            return param;
        }

        // long

        public Long pathParamMethod(@PathParam("id") Long param) {
            return param;
        }

        public long pathParamMethod(@PathParam("id") long param) {
            return param;
        }

        public Long queryParamMethod(@QueryParam("id") Long param) {
            return param;
        }

        public long queryParamMethod(@QueryParam("id") long param) {
            return param;
        }

        // float

        public Float pathParamMethod(@PathParam("id") Float param) {
            return param;
        }

        public float pathParamMethod(@PathParam("id") float param) {
            return param;
        }

        public Float queryParamMethod(@QueryParam("id") Float param) {
            return param;
        }

        public float queryParamMethod(@QueryParam("id") float param) {
            return param;
        }

        // boolean

        public Boolean pathParamMethod(@PathParam("id") Boolean param) {
            return param;
        }

        public boolean pathParamMethod(@PathParam("id") boolean param) {
            return param;
        }

        public Boolean queryParamMethod(@QueryParam("id") Boolean param) {
            return param;
        }

        public boolean queryParamMethod(@QueryParam("id") boolean param) {
            return param;
        }

        // character

        public Character pathParamMethod(@PathParam("id") Character param) {
            return param;
        }

        public char pathParamMethod(@PathParam("id") char param) {
            return param;
        }

        public Character queryParamMethod(@QueryParam("id") Character param) {
            return param;
        }

        public char queryParamMethod(@QueryParam("id") char param) {
            return param;
        }

        // string

        public String pathParamMethod(@PathParam("id") String param) {
            return param;
        }

        public String queryParamMethod(@QueryParam("id") String param) {
            return param;
        }

        public TestPayload queryPayloadMethod(@PayloadParam TestPayload param) {
            return param;
        }

        public int mixMethod(@PathParam("var1") Long param1,
                             @QueryParam("var2") Boolean param2,
                             @PayloadParam TestPayload param3) {
            return param3.id();
        }

        // int
        public Integer pathParamMethodWithHttpServlet(@PathParam("id") Integer param,
                                                      HttpServletResponse response,
                                                      HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public int pathParamMethodWithHttpServlet(@PathParam("id") int param,
                                                  HttpServletResponse response,
                                                  HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public Integer queryParamMethodWithHttpServlet(@QueryParam("id") Integer param,
                                                       HttpServletResponse response,
                                                       HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public int queryParamMethodWithHttpServlet(@QueryParam("id") int param,
                                                   HttpServletResponse response,
                                                   HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        // double

        public Double pathParamMethodWithHttpServlet(@PathParam("id") Double param,
                                                     HttpServletResponse response,
                                                     HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public double pathParamMethodWithHttpServlet(@PathParam("id") double param,
                                                     HttpServletResponse response,
                                                     HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public Double queryParamMethodWithHttpServlet(@QueryParam("id") Double param,
                                                      HttpServletResponse response,
                                                      HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public double queryParamMethodWithHttpServlet(@QueryParam("id") double param,
                                                      HttpServletResponse response,
                                                      HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        // long

        public Long pathParamMethodWithHttpServlet(@PathParam("id") Long param,
                                                   HttpServletResponse response,
                                                   HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public long pathParamMethodWithHttpServlet(@PathParam("id") long param,
                                                   HttpServletResponse response,
                                                   HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public Long queryParamMethodWithHttpServlet(@QueryParam("id") Long param,
                                                    HttpServletResponse response,
                                                    HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public long queryParamMethodWithHttpServlet(@QueryParam("id") long param,
                                                    HttpServletResponse response,
                                                    HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        // float

        public Float pathParamMethodWithHttpServlet(@PathParam("id") Float param,
                                                    HttpServletResponse response,
                                                    HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public float pathParamMethodWithHttpServlet(@PathParam("id") float param,
                                                    HttpServletResponse response,
                                                    HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public Float queryParamMethodWithHttpServlet(@QueryParam("id") Float param,
                                                     HttpServletResponse response,
                                                     HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public float queryParamMethodWithHttpServlet(@QueryParam("id") float param,
                                                     HttpServletResponse response,
                                                     HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        // boolean

        public Boolean pathParamMethodWithHttpServlet(@PathParam("id") Boolean param,
                                                      HttpServletResponse response,
                                                      HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public boolean pathParamMethodWithHttpServlet(@PathParam("id") boolean param,
                                                      HttpServletResponse response,
                                                      HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public Boolean queryParamMethodWithHttpServlet(@QueryParam("id") Boolean param,
                                                       HttpServletResponse response,
                                                       HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public boolean queryParamMethodWithHttpServlet(@QueryParam("id") boolean param,
                                                       HttpServletResponse response,
                                                       HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        // character

        public Character pathParamMethodWithHttpServlet(@PathParam("id") Character param,
                                                        HttpServletResponse response,
                                                        HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public char pathParamMethodWithHttpServlet(@PathParam("id") char param,
                                                   HttpServletResponse response,
                                                   HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public Character queryParamMethodWithHttpServlet(@QueryParam("id") Character param,
                                                         HttpServletResponse response,
                                                         HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public char queryParamMethodWithHttpServlet(@QueryParam("id") char param,
                                                    HttpServletResponse response,
                                                    HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        // string

        public String pathParamMethodWithHttpServlet(@PathParam("id") String param,
                                                     HttpServletResponse response,
                                                     HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public String queryParamMethodWithHttpServlet(@QueryParam("id") String param,
                                                      HttpServletResponse response,
                                                      HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public TestPayload queryPayloadMethodWithHttpServlet(@PayloadParam TestPayload param,
                                                             HttpServletResponse response,
                                                             HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param));
            return param;
        }

        public int mixMethodWithHttpServlet(@PathParam("var1") Long param1,
                                            @QueryParam("var2") Boolean param2,
                                            @PayloadParam TestPayload param3,
                                            HttpServletResponse response,
                                            HttpServletRequest request) {
            request.getMethod();
            response.addHeader(TEMP_HEADER, String.valueOf(param1));
            return param3.id();
        }

    }

    // Helper test payload class
    record TestPayload(int id, String name) {
    }
}