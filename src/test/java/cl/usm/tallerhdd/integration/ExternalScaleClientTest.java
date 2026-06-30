package cl.usm.tallerhdd.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.lang.reflect.Field;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class ExternalScaleClientTest {

    @Mock RestClient restClient;
    @Mock RedisTemplate<String, Object> redisTemplate;
    @Mock ValueOperations<String, Object> valueOps;
    @Mock RestClient.RequestHeadersUriSpec uriSpec;
    @Mock RestClient.RequestHeadersSpec headersSpec;
    @Mock RestClient.ResponseSpec responseSpec;

    ExternalScaleClient client;

    @BeforeEach
    void setUp() throws Exception {
        client = new ExternalScaleClient(restClient, redisTemplate);
        Field field = ExternalScaleClient.class.getDeclaredField("baseUrl");
        field.setAccessible(true);
        field.set(client, "http://test-api");

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    // ─── getScaleSpecifications ───────────────────────────────────────────────

    @Test
    void getSpec_exitoso_retornaEspecificacion() {
        ScaleSpecification esperado = new ScaleSpecification("1", "TestScale", "Brand", 100.0, 0.1, 0.0);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(ScaleSpecification.class)).thenReturn(esperado);

        ScaleSpecification resultado = client.getScaleSpecifications("1");

        assertThat(resultado).isEqualTo(esperado);
    }

    @Test
    void getSpec_exitoso_guardaEnCacheConTTL() {
        ScaleSpecification spec = new ScaleSpecification("1", "Scale", "B", 50.0, 0.1, 0.0);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(ScaleSpecification.class)).thenReturn(spec);

        client.getScaleSpecifications("1");

        verify(valueOps).set(eq("scale:spec:1"), eq(spec), any(Duration.class));
    }

    // ─── recover ─────────────────────────────────────────────────────────────

    @Test
    void recover_conCacheEspecifica_retornaCacheado() {
        ScaleSpecification cacheado = new ScaleSpecification("1", "Cached", "B", 50.0, 0.1, 0.0);
        when(valueOps.get("scale:spec:1")).thenReturn(cacheado);

        ScaleSpecification resultado = client.recover(new RestClientException("error"), "1");

        assertThat(resultado).isEqualTo(cacheado);
        verify(valueOps, never()).get("scale:spec:-1");
    }

    @Test
    void recover_sinCacheEspecifica_usaEspecificacionDefault() {
        ScaleSpecification defaultSpec = new ScaleSpecification("-1", "Default", "B", 30.0, 0.1, 0.0);
        when(valueOps.get("scale:spec:1")).thenReturn(null);
        when(valueOps.get("scale:spec:-1")).thenReturn(defaultSpec);

        ScaleSpecification resultado = client.recover(new RestClientException("error"), "1");

        assertThat(resultado).isEqualTo(defaultSpec);
    }

    @Test
    void recover_sinNingunCache_lanzaExcepcion() {
        when(valueOps.get(anyString())).thenReturn(null);

        assertThatThrownBy(() -> client.recover(new RestClientException("fallo"), "1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("balanza");
    }

    @Test
    void recover_cacheConTipoInvalido_lanzaExcepcion() {
        when(valueOps.get("scale:spec:1")).thenReturn("string-invalido");

        assertThatThrownBy(() -> client.recover(new RestClientException("fallo"), "1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("deserializar");
    }
}