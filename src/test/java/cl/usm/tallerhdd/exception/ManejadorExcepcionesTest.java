package cl.usm.tallerhdd.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ManejadorExcepcionesTest {

    private final ManejadorExcepciones manejador = new ManejadorExcepciones();

    @Test
    void manejarTransicionInvalida_retorna409ConMensaje() {
        IllegalWeighingStateException ex = new IllegalWeighingStateException("transición inválida");
        ResponseEntity<String> respuesta = manejador.manejarTransicionInvalida(ex);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(respuesta.getBody()).isEqualTo("transición inválida");
    }

    @Test
    void manejarArgumentoInvalido_retorna400ConMensaje() {
        IllegalArgumentException ex = new IllegalArgumentException("argumento inválido");
        ResponseEntity<String> respuesta = manejador.manejarArgumentoInvalido(ex);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(respuesta.getBody()).isEqualTo("argumento inválido");
    }

    @Test
    void manejarArgumentoInvalido_aceptaNumberFormatException() {
        // NumberFormatException extiende IllegalArgumentException
        NumberFormatException ex = new NumberFormatException("valor no numérico");
        ResponseEntity<String> respuesta = manejador.manejarArgumentoInvalido(ex);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(respuesta.getBody()).contains("numérico");
    }

    @Test
    void manejarErrorInesperado_retorna500ConMensaje() {
        RuntimeException ex = new RuntimeException("Registro no encontrado: nope");
        ResponseEntity<String> respuesta = manejador.manejarErrorInesperado(ex);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(respuesta.getBody()).isEqualTo("Registro no encontrado: nope");
    }
}