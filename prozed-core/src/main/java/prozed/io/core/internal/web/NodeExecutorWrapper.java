package prozed.io.core.internal.web;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import prozed.io.core.api.web.PathParam;
import prozed.io.core.api.web.PayloadParam;
import prozed.io.core.api.web.QueryParam;
import prozed.io.core.internal.reflection.TypeConvertor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Map;

public record NodeExecutorWrapper(
        Map<String, String> pathParams,
        Map<String, String> queryParams,
        Method method
) {
    public Object execute(Object controller, String payload, Gson gson) throws Exception {
        // Build arguments array in the correct order
        Object[] args = new Object[method.getParameterCount()];

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            // Handle @PathParam
            PathParam pathParam = parameter.getAnnotation(PathParam.class);
            if (pathParam != null) {
                Object value = TypeConvertor.convert(pathParams.get(pathParam.value()), parameter.getType());
                args[i] = value;
                continue;
            }

            // Handle @QueryParam
            QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
            if (queryParam != null) {
                Object value = TypeConvertor.convert(queryParams.get(queryParam.value()), parameter.getType());
                args[i] = value;
                continue;
            }

            PayloadParam payloadParam = parameter.getAnnotation(PayloadParam.class);
            if (payloadParam != null) {
                try {
                    Type payloadType = parameter.getParameterizedType();
                    args[i] = gson.fromJson(payload, payloadType);
                } catch (Exception e) {
                    throw new HttpException("Payload parameter %s could not be parsed".formatted(parameter.getName()), HttpServletResponse.SC_BAD_REQUEST, e);
                }
            }
        }

        return method.invoke(controller, args);
    }
}
