package cl.usm.tallerhdd.service;

import cl.usm.tallerhdd.model.CategoriaPeso;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class ValidadorRestriccionesTest {

    // dia 15 = impar, dia 16 = par
    private ValidadorRestricciones validador(int hora, int dia) {
        String instant = String.format("2024-01-%02dT%02d:00:00Z", dia, hora);
        Clock clock = Clock.fixed(Instant.parse(instant), ZoneId.of("UTC"));
        return new ValidadorRestricciones(clock);
    }

    // ─── esHorarioNocturnoProhibido ───────────────────────────────────────────

    @Test
    void horario_categoriaLiviano_ignoraHora() {
        assertThat(validador(21, 15).esHorarioNocturnoProhibido(CategoriaPeso.LIVIANO)).isFalse();
    }

    @Test
    void horario_categoriaMediano_ignoraHora() {
        assertThat(validador(21, 15).esHorarioNocturnoProhibido(CategoriaPeso.MEDIANO)).isFalse();
    }

    @Test
    void horario_pesadoHora20_esNocturno() {
        assertThat(validador(20, 15).esHorarioNocturnoProhibido(CategoriaPeso.PESADO)).isTrue();
    }

    @Test
    void horario_pesadoHora23_esNocturno() {
        assertThat(validador(23, 15).esHorarioNocturnoProhibido(CategoriaPeso.PESADO)).isTrue();
    }

    @Test
    void horario_pesadoHora0_esNocturno() {
        assertThat(validador(0, 15).esHorarioNocturnoProhibido(CategoriaPeso.PESADO)).isTrue();
    }

    @Test
    void horario_pesadoHora5_esNocturno() {
        assertThat(validador(5, 15).esHorarioNocturnoProhibido(CategoriaPeso.PESADO)).isTrue();
    }

    @Test
    void horario_pesadoHora6_esDiurno() {
        assertThat(validador(6, 15).esHorarioNocturnoProhibido(CategoriaPeso.PESADO)).isFalse();
    }

    @Test
    void horario_pesadoHora12_esDiurno() {
        assertThat(validador(12, 15).esHorarioNocturnoProhibido(CategoriaPeso.PESADO)).isFalse();
    }

    @Test
    void horario_pesadoHora19_esDiurno() {
        assertThat(validador(19, 15).esHorarioNocturnoProhibido(CategoriaPeso.PESADO)).isFalse();
    }

    // ─── esBalanzaPrimaBloqueada ──────────────────────────────────────────────

    @Test
    void balanza_categoriaLiviano_nuncaBloqueada() {
        assertThat(validador(10, 15).esBalanzaPrimaBloqueada(7, CategoriaPeso.LIVIANO)).isFalse();
    }

    @Test
    void balanza_categoriaMediano_nuncaBloqueada() {
        assertThat(validador(10, 15).esBalanzaPrimaBloqueada(7, CategoriaPeso.MEDIANO)).isFalse();
    }

    @Test
    void balanza_pesadoNoPrima_noBloqueada() {
        // balanza 4 = no primo
        assertThat(validador(10, 15).esBalanzaPrimaBloqueada(4, CategoriaPeso.PESADO)).isFalse();
    }

    @Test
    void balanza_pesadoPrimaDiaImpar_bloqueada() {
        // balanza 7 (primo), dia 15 (impar)
        assertThat(validador(10, 15).esBalanzaPrimaBloqueada(7, CategoriaPeso.PESADO)).isTrue();
    }

    @Test
    void balanza_pesadoPrimaDiaPar_noBloqueada() {
        // balanza 7 (primo), dia 16 (par)
        assertThat(validador(10, 16).esBalanzaPrimaBloqueada(7, CategoriaPeso.PESADO)).isFalse();
    }

    @Test
    void balanza_idUno_noPrimo() {
        assertThat(validador(10, 15).esBalanzaPrimaBloqueada(1, CategoriaPeso.PESADO)).isFalse();
    }

    @Test
    void balanza_idCero_noPrimo() {
        assertThat(validador(10, 15).esBalanzaPrimaBloqueada(0, CategoriaPeso.PESADO)).isFalse();
    }

    @Test
    void balanza_idNegativo_noPrimo() {
        assertThat(validador(10, 15).esBalanzaPrimaBloqueada(-3, CategoriaPeso.PESADO)).isFalse();
    }

    @Test
    void balanza_idDos_esPrimo() {
        // 2 es el primo más pequeño; dia 15 (impar) → bloqueada
        assertThat(validador(10, 15).esBalanzaPrimaBloqueada(2, CategoriaPeso.PESADO)).isTrue();
    }

    @Test
    void balanza_idNueve_noPrimo() {
        // 9 = 3×3
        assertThat(validador(10, 15).esBalanzaPrimaBloqueada(9, CategoriaPeso.PESADO)).isFalse();
    }

    @Test
    void balanza_idVeintitres_esPrimo() {
        // 23 es primo; dia 15 (impar) → bloqueada
        assertThat(validador(10, 15).esBalanzaPrimaBloqueada(23, CategoriaPeso.PESADO)).isTrue();
    }
}