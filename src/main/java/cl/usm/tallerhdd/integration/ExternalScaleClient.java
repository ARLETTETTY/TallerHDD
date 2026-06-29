package cl.usm.tallerhdd.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

@Component
public class ExternalScaleClient {

    private static final String CACHE_PREFIX = "scale:spec:";
    private static final String DEFAULT_KEY = "scale:spec:-1";

    private final RestClient restClient;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${scale.api.base-url}")
    private String baseUrl;

    public ExternalScaleClient(RestClient restClient, RedisTemplate<String, Object> redisTemplate) {
        this.restClient = restClient;
        this.redisTemplate = redisTemplate;
    }

    @Retryable(
            retryFor = RestClientException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2)
    )
    public ScaleSpecification getScaleSpecifications(String scaleId) {
        ScaleSpecification spec = restClient.get()
                .uri("/scales/{id}", scaleId)
                .retrieve()
                .body(ScaleSpecification.class);

        String cacheKey = CACHE_PREFIX + scaleId;
        redisTemplate.opsForValue().set(cacheKey, spec, Duration.ofSeconds(120));

        return spec;
    }

    @Recover
    public ScaleSpecification recover(RestClientException ex, String scaleId) {
        String cacheKey = CACHE_PREFIX + scaleId;

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return convertToSpec(cached);
        }

        Object defaultSpec = redisTemplate.opsForValue().get(DEFAULT_KEY);
        if (defaultSpec != null) {
            return convertToSpec(defaultSpec);
        }

        throw new RuntimeException("API externa no disponible y sin especificación en caché para balanza: " + scaleId);
    }

    private ScaleSpecification convertToSpec(Object obj) {
        if (obj instanceof ScaleSpecification spec) {
            return spec;
        }
        throw new RuntimeException("Error al deserializar especificación desde caché");
    }
}