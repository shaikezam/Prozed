package prozed.io.example.web;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class ProtectPathFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ProtectPathFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // Check if the sessionId cookie is present
        Cookie[] cookies = request.getCookies();
        boolean sessionIdCookiePresent = false;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sessionId".equals(cookie.getName())) {
                    sessionIdCookiePresent = true;
                    break;
                }
            }
        }
        if (!sessionIdCookiePresent) {
            logger.warn("sessionId cookie not present");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set 401 Unauthorized status code
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized will redirect to login page...");
            return;
        }
        // Log the request details
        logger.info("Request from {} authorized", request.getRemoteAddr());

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
