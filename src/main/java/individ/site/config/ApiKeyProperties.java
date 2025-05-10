package individ.site.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;

@Component
public class ApiKeyProperties {
    private static final Logger logger = LoggerFactory.getLogger(ApiKeyProperties.class);
    
    @Value("${api.key}")
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
    @PostConstruct
    public void init() {
        logger.info("API Key loaded: {}", key != null ? "YES" : "NO");
    }
}
