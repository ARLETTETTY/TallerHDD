package cl.usm.tallerhdd.service;

import cl.usm.tallerhdd.model.CategoriaPeso;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class ClasificadorPesoTest {

    private final ClasificadorPeso clasificador = new ClasificadorPeso();

    @Test
    void clasificar_cero_esLiviano() {
        assertThat(clasificador.clasificar(0)).isEqualTo(CategoriaPeso.LIVIANO);
    }

    @Test
    void clasificar_exactamenteDiez_esLiviano() {
        assertThat(clasificador.clasificar(10)).isEqualTo(CategoriaPeso.LIVIANO);
    }

    @Test
    void clasificar_once_esMediano() {
        assertThat(clasificador.clasificar(11)).isEqualTo(CategoriaPeso.MEDIANO);
    }

    @Test
    void clasificar_exactamenteCincuenta_esMediano() {
        assertThat(clasificador.clasificar(50)).isEqualTo(CategoriaPeso.MEDIANO);
    }

    @Test
    void clasificar_cincuentaYUno_esPesado() {
        assertThat(clasificador.clasificar(51)).isEqualTo(CategoriaPeso.PESADO);
    }

    @Test
    void clasificar_valorGrande_esPesado() {
        assertThat(clasificador.clasificar(999)).isEqualTo(CategoriaPeso.PESADO);
    }

    @ParameterizedTest
    @CsvSource({
            "0,      LIVIANO",
            "5,      LIVIANO",
            "10,     LIVIANO",
            "10.001, MEDIANO",
            "30,     MEDIANO",
            "50,     MEDIANO",
            "50.001, PESADO",
            "100,    PESADO"
    })
    void clasificar_limitesDeBanda(double sansas, String categoriaEsperada) {
        assertThat(clasificador.clasificar(sansas).name()).isEqualTo(categoriaEsperada);
    }
}
