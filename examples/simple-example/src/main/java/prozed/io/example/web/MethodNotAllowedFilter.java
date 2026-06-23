package prozed.io.example.web;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MethodNotAllowedFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodNotAllowedFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // Check if the sessionId cookie is present
        Cookie[] cookies = request.getCookies();
        if (request.getMethod().equals("GET")) {
            LOGGER.warn("GET method not allowed");
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method not allowed");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
