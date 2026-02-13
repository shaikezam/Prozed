package prozed.io.core.internal.web;

import prozed.io.core.api.web.PathParam;
import prozed.io.core.api.web.QueryParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public record NodeExecutorWrapper(
        Map<String, String> pathParams,
        Map<String, String> queryParams,
        Method method
) {
    public Object execute(Object controller) throws Exception {
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

            // Handle parameters without annotations (could be entire maps)
            if (parameter.getType() == Map.class) {
                // You might want to handle this case differently
                args[i] = pathParams; // or queryParams, depending on your design
            }
        }

        // Now invoke with the correctly ordered arguments
        return method.invoke(controller, args);
    }
}
