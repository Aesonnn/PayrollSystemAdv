package individ.site.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import individ.site.config.ApiKeyProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyInterceptor.class);
    
    @Autowired
    private ApiKeyProperties apiKeyProperties;

    private static final String API_KEY_HEADER = "X-API-KEY";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Only apply to specific API paths
        String requestPath = request.getRequestURI();
        if (!requestPath.startsWith("/api/")) {
            return true;
        }
        
        String receivedApiKey = request.getHeader(API_KEY_HEADER);
        String configuredApiKey = apiKeyProperties.getKey();
        
        logger.info("Validating API key for path: {}", requestPath);
        
        // Check if key is configured
        if (configuredApiKey == null || configuredApiKey.isEmpty()) {
            logger.error("API key is not configured");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write("API not configured properly");
            return false;
        }
        
        // Check if key is provided
        if (receivedApiKey == null || receivedApiKey.isEmpty()) {
            logger.warn("API key header is missing");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("API key required");
            return false;
        }
        
        // Check if key matches
        if (!configuredApiKey.equals(receivedApiKey)) {
            logger.warn("Invalid API key provided");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Invalid API key");
            return false;
        }
        
        return true;
    }
}
