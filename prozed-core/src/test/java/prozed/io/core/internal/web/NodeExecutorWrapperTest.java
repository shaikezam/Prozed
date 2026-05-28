package prozed.io.core.internal.web;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import prozed.io.core.api.web.PathParam;
import prozed.io.core.api.web.PayloadParam;
import prozed.io.core.api.web.QueryParam;
import prozed.io.test.utils.RandomUtils;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NodeExecutorWrapperTest {

    private final Gson gson = new Gson();

    // int

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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
    }

    @Test
    void testExecuteWithQueryParamFloatPrimitive() throws Exception {
        // given
        float input = RandomUtils.randomLong();
        Map<String, String> pathParams = Map.of();
        Map<String, String> queryParams = Map.of("id", Float.toString(input));
        Method method = TestController.class.getMethod("queryParamMethod", float.class);
        NodeExecutorWrapper wrapper = new NodeExecutorWrapper(pathParams, queryParams, method);
        TestController controller = new TestController();

        // when
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
    }

    // character

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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, null, gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, gson.toJson(input), gson);

        // then
        assertEquals(input, result);
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
        Object result = wrapper.execute(controller, gson.toJson(payloadParam), gson);

        // then
        assertEquals(payloadParam.id(), result);
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

    }

    // Helper test payload class
    record TestPayload(int id, String name) {
    }
}