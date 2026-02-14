package prozed.io.core.internal.web;

import com.google.gson.Gson;
import prozed.io.core.api.web.HttpCode;
import prozed.io.core.api.web.PathParam;
import prozed.io.core.api.web.PayloadParam;
import prozed.io.core.api.web.QueryParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
                String paramName = pathParam.name();
                String value = pathParams.get(paramName);
                args[i] = value;
                continue;
            }

            // Handle @QueryParam
            QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
            if (queryParam != null) {
                String paramName = queryParam.name();
                String value = queryParams.get(paramName);
                args[i] = value;
                continue;
            }

            PayloadParam payloadParam = parameter.getAnnotation(PayloadParam.class);
            if (payloadParam != null) {
                try {
                    Class<?> payloadType = parameter.getType();
                    args[i] = gson.fromJson(payload, payloadType);
                } catch (Exception e) {
                    throw new HttpException("Payload parameter %s could not be parsed".formatted(payloadParam.value()), HttpCode.BAD_REQUEST, e);
                }
            }
        }

        return method.invoke(method.getDeclaringClass(), args);
    }
}
