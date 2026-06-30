package cl.usm.tallerhdd.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class ConversorPesoTest {

    private final ConversorPeso conversor = new ConversorPeso();

    @Test
    void kgSansa_convierteCorrectamente() {
        double resultado = conversor.kgSansa(1.337);
        assertThat(resultado).isCloseTo(1.0, within(0.0001));
    }

    @Test
    void kgSansa_ceroRetornaCero() {
        assertThat(conversor.kgSansa(0)).isEqualTo(0.0);
    }

    @Test
    void kgSansa_valorPositivo_retornaConversion() {
        double resultado = conversor.kgSansa(13.37);
        assertThat(resultado).isCloseTo(10.0, within(0.0001));
    }

    @Test
    void sansaAKg_convierteCorrectamente() {
        double resultado = conversor.sansaAKg(1.0);
        assertThat(resultado).isCloseTo(1.337, within(0.0001));
    }

    @Test
    void sansaAKg_ceroRetornaCero() {
        assertThat(conversor.sansaAKg(0)).isEqualTo(0.0);
    }

    @Test
    void conversion_esInversaExacta() {
        double pesoOriginal = 50.0;
        double sansas = conversor.kgSansa(pesoOriginal);
        double kgDeVuelta = conversor.sansaAKg(sansas);
        assertThat(kgDeVuelta).isCloseTo(pesoOriginal, within(0.0001));
    }
}
