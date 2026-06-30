package cl.usm.tallerhdd.service;

import cl.usm.tallerhdd.exception.IllegalWeighingStateException;
import cl.usm.tallerhdd.model.EstadoPesaje;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MaquinaEstadosTest {

    private final MaquinaEstados maquina = new MaquinaEstados();

    // ─── Transiciones válidas ─────────────────────────────────────────────────

    @Test
    void ingresado_a_pesado_permitida() {
        assertThat(maquina.transicionar(EstadoPesaje.INGRESADO, EstadoPesaje.PESADO))
                .isEqualTo(EstadoPesaje.PESADO);
    }

    @Test
    void pesado_a_aprobado_permitida() {
        assertThat(maquina.transicionar(EstadoPesaje.PESADO, EstadoPesaje.APROBADO))
                .isEqualTo(EstadoPesaje.APROBADO);
    }

    @Test
    void pesado_a_rechazado_permitida() {
        assertThat(maquina.transicionar(EstadoPesaje.PESADO, EstadoPesaje.RECHAZADO))
                .isEqualTo(EstadoPesaje.RECHAZADO);
    }

    @Test
    void aprobado_a_despachado_permitida() {
        assertThat(maquina.transicionar(EstadoPesaje.APROBADO, EstadoPesaje.DESPACHADO))
                .isEqualTo(EstadoPesaje.DESPACHADO);
    }

    @Test
    void rechazado_a_despachado_permitida() {
        assertThat(maquina.transicionar(EstadoPesaje.RECHAZADO, EstadoPesaje.DESPACHADO))
                .isEqualTo(EstadoPesaje.DESPACHADO);
    }

    // ─── Transiciones inválidas desde INGRESADO ───────────────────────────────

    @Test
    void ingresado_a_aprobado_lanzaExcepcion() {
        assertThatThrownBy(() -> maquina.transicionar(EstadoPesaje.INGRESADO, EstadoPesaje.APROBADO))
                .isInstanceOf(IllegalWeighingStateException.class);
    }

    @Test
    void ingresado_a_rechazado_lanzaExcepcion() {
        assertThatThrownBy(() -> maquina.transicionar(EstadoPesaje.INGRESADO, EstadoPesaje.RECHAZADO))
                .isInstanceOf(IllegalWeighingStateException.class);
    }

    @Test
    void ingresado_a_despachado_lanzaExcepcion() {
        assertThatThrownBy(() -> maquina.transicionar(EstadoPesaje.INGRESADO, EstadoPesaje.DESPACHADO))
                .isInstanceOf(IllegalWeighingStateException.class);
    }

    // ─── Transiciones inválidas desde PESADO ─────────────────────────────────

    @Test
    void pesado_a_ingresado_lanzaExcepcion() {
        assertThatThrownBy(() -> maquina.transicionar(EstadoPesaje.PESADO, EstadoPesaje.INGRESADO))
                .isInstanceOf(IllegalWeighingStateException.class);
    }

    @Test
    void pesado_a_despachado_lanzaExcepcion() {
        assertThatThrownBy(() -> maquina.transicionar(EstadoPesaje.PESADO, EstadoPesaje.DESPACHADO))
                .isInstanceOf(IllegalWeighingStateException.class);
    }

    // ─── Transiciones inválidas desde APROBADO ───────────────────────────────

    @Test
    void aprobado_a_ingresado_lanzaExcepcion() {
        assertThatThrownBy(() -> maquina.transicionar(EstadoPesaje.APROBADO, EstadoPesaje.INGRESADO))
                .isInstanceOf(IllegalWeighingStateException.class);
    }

    @Test
    void aprobado_a_pesado_lanzaExcepcion() {
        assertThatThrownBy(() -> maquina.transicionar(EstadoPesaje.APROBADO, EstadoPesaje.PESADO))
                .isInstanceOf(IllegalWeighingStateException.class);
    }

    @Test
    void aprobado_a_rechazado_lanzaExcepcion() {
        assertThatThrownBy(() -> maquina.transicionar(EstadoPesaje.APROBADO, EstadoPesaje.RECHAZADO))
                .isInstanceOf(IllegalWeighingStateException.class);
    }

    // ─── Transiciones inválidas desde RECHAZADO ──────────────────────────────

    @Test
    void rechazado_a_ingresado_lanzaExcepcion() {
        assertThatThrownBy(() -> maquina.transicionar(EstadoPesaje.RECHAZADO, EstadoPesaje.INGRESADO))
                .isInstanceOf(IllegalWeighingStateException.class);
    }

    @Test
    void rechazado_a_pesado_lanzaExcepcion() {
        assertThatThrownBy(() -> maquina.transicionar(EstadoPesaje.RECHAZADO, EstadoPesaje.PESADO))
                .isInstanceOf(IllegalWeighingStateException.class);
    }

    @Test
    void rechazado_a_aprobado_lanzaExcepcion() {
        assertThatThrownBy(() -> maquina.transicionar(EstadoPesaje.RECHAZADO, EstadoPesaje.APROBADO))
                .isInstanceOf(IllegalWeighingStateException.class);
    }

    // ─── DESPACHADO es estado terminal ───────────────────────────────────────

    @Test
    void despachado_cualquierDestino_lanzaExcepcion() {
        for (EstadoPesaje destino : EstadoPesaje.values()) {
            assertThatThrownBy(() -> maquina.transicionar(EstadoPesaje.DESPACHADO, destino))
                    .isInstanceOf(IllegalWeighingStateException.class);
        }
    }

    @Test
    void excepcion_contieneEstadosImplicados() {
        assertThatThrownBy(() -> maquina.transicionar(EstadoPesaje.INGRESADO, EstadoPesaje.APROBADO))
                .isInstanceOf(IllegalWeighingStateException.class)
                .hasMessageContaining("INGRESADO")
                .hasMessageContaining("APROBADO");
    }
}