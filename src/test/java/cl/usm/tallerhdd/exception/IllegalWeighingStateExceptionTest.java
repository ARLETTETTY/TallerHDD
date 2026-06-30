package cl.usm.tallerhdd.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IllegalWeighingStateExceptionTest {

    @Test
    void constructor_guardaMensaje() {
        String mensaje = "transición no permitida";
        IllegalWeighingStateException ex = new IllegalWeighingStateException(mensaje);
        assertThat(ex.getMessage()).isEqualTo(mensaje);
    }

    @Test
    void esSubclaseDeRuntimeException() {
        IllegalWeighingStateException ex = new IllegalWeighingStateException("error");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }
}