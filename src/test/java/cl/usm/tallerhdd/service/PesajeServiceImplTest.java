package cl.usm.tallerhdd.service;

import cl.usm.tallerhdd.exception.IllegalWeighingStateException;
import cl.usm.tallerhdd.model.CategoriaPeso;
import cl.usm.tallerhdd.model.EstadoPesaje;
import cl.usm.tallerhdd.model.RegistroPesaje;
import cl.usm.tallerhdd.repository.RegistroPesajeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PesajeServiceImplTest {

    @Mock ConversorPeso conversorPeso;
    @Mock ClasificadorPeso clasificadorPeso;
    @Mock ValidadorRestricciones validadorRestricciones;
    @Mock MaquinaEstados maquinaEstados;
    @Mock RegistroPesajeRepository repository;

    PesajeServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PesajeServiceImpl(
                conversorPeso, clasificadorPeso, validadorRestricciones, maquinaEstados, repository);
    }

    // ─── registrarPesaje ──────────────────────────────────────────────────────

    @Test
    void registrar_exitoso_retornaRegistroGuardado() {
        when(conversorPeso.kgSansa(10.0)).thenReturn(7.48);
        when(clasificadorPeso.clasificar(7.48)).thenReturn(CategoriaPeso.LIVIANO);
        when(validadorRestricciones.esHorarioNocturnoProhibido(CategoriaPeso.LIVIANO)).thenReturn(false);
        when(validadorRestricciones.esBalanzaPrimaBloqueada(4, CategoriaPeso.LIVIANO)).thenReturn(false);
        RegistroPesaje guardado = new RegistroPesaje();
        guardado.setId("abc123");
        when(repository.save(any())).thenReturn(guardado);

        RegistroPesaje resultado = service.registrarPesaje("4", "PKG-1", 10.0);

        assertThat(resultado.getId()).isEqualTo("abc123");
        verify(repository).save(any(RegistroPesaje.class));
    }

    @Test
    void registrar_seteaEstadoIngresadoCategoriaPesoYFechas() {
        when(conversorPeso.kgSansa(5.0)).thenReturn(3.74);
        when(clasificadorPeso.clasificar(3.74)).thenReturn(CategoriaPeso.LIVIANO);
        when(validadorRestricciones.esHorarioNocturnoProhibido(CategoriaPeso.LIVIANO)).thenReturn(false);
        when(validadorRestricciones.esBalanzaPrimaBloqueada(4, CategoriaPeso.LIVIANO)).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RegistroPesaje resultado = service.registrarPesaje("4", "PKG-X", 5.0);

        assertThat(resultado.getEstado()).isEqualTo(EstadoPesaje.INGRESADO);
        assertThat(resultado.getCategoria()).isEqualTo(CategoriaPeso.LIVIANO);
        assertThat(resultado.getPesoSansas()).isEqualTo(3.74);
        assertThat(resultado.getCreatedAt()).isNotNull();
        assertThat(resultado.getUpdatedAt()).isNotNull();
        assertThat(resultado.getHistorialEstados()).hasSize(1);
        assertThat(resultado.getHistorialEstados().get(0)).startsWith("INGRESADO");
    }

    @Test
    void registrar_horarioNocturnoProhibido_lanzaExcepcion() {
        when(conversorPeso.kgSansa(anyDouble())).thenReturn(70.0);
        when(clasificadorPeso.clasificar(70.0)).thenReturn(CategoriaPeso.PESADO);
        when(validadorRestricciones.esHorarioNocturnoProhibido(CategoriaPeso.PESADO)).thenReturn(true);

        assertThatThrownBy(() -> service.registrarPesaje("3", "PKG-1", 94.0))
                .isInstanceOf(IllegalWeighingStateException.class)
                .hasMessageContaining("nocturno");

        verify(repository, never()).save(any());
    }

    @Test
    void registrar_idBalanzaNoNumerico_lanzaExcepcion() {
        when(conversorPeso.kgSansa(anyDouble())).thenReturn(7.0);
        when(clasificadorPeso.clasificar(7.0)).thenReturn(CategoriaPeso.LIVIANO);
        when(validadorRestricciones.esHorarioNocturnoProhibido(CategoriaPeso.LIVIANO)).thenReturn(false);

        assertThatThrownBy(() -> service.registrarPesaje("abc", "PKG-1", 10.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("numérico");

        verify(repository, never()).save(any());
    }

    @Test
    void registrar_balanzaPrimaBloqueada_lanzaExcepcion() {
        when(conversorPeso.kgSansa(anyDouble())).thenReturn(70.0);
        when(clasificadorPeso.clasificar(70.0)).thenReturn(CategoriaPeso.PESADO);
        when(validadorRestricciones.esHorarioNocturnoProhibido(CategoriaPeso.PESADO)).thenReturn(false);
        when(validadorRestricciones.esBalanzaPrimaBloqueada(7, CategoriaPeso.PESADO)).thenReturn(true);

        assertThatThrownBy(() -> service.registrarPesaje("7", "PKG-1", 94.0))
                .isInstanceOf(IllegalWeighingStateException.class)
                .hasMessageContaining("primo");

        verify(repository, never()).save(any());
    }

    // ─── actualizarEstado ─────────────────────────────────────────────────────

    @Test
    void actualizar_exitoso_cambiaEstadoYAgregaHistorial() {
        RegistroPesaje registro = new RegistroPesaje();
        registro.setId("id1");
        registro.setEstado(EstadoPesaje.INGRESADO);

        when(repository.findById("id1")).thenReturn(Optional.of(registro));
        when(maquinaEstados.transicionar(EstadoPesaje.INGRESADO, EstadoPesaje.PESADO)).thenReturn(EstadoPesaje.PESADO);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RegistroPesaje resultado = service.actualizarEstado("id1", EstadoPesaje.PESADO);

        assertThat(resultado.getEstado()).isEqualTo(EstadoPesaje.PESADO);
        assertThat(resultado.getUpdatedAt()).isNotNull();
        assertThat(resultado.getHistorialEstados()).hasSize(1);
        assertThat(resultado.getHistorialEstados().get(0)).startsWith("PESADO");
    }

    @Test
    void actualizar_registroNoEncontrado_lanzaExcepcion() {
        when(repository.findById("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizarEstado("inexistente", EstadoPesaje.PESADO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("inexistente");
    }

    @Test
    void actualizar_transicionInvalida_lanzaExcepcionDeMaquina() {
        RegistroPesaje registro = new RegistroPesaje();
        registro.setId("id2");
        registro.setEstado(EstadoPesaje.INGRESADO);

        when(repository.findById("id2")).thenReturn(Optional.of(registro));
        when(maquinaEstados.transicionar(EstadoPesaje.INGRESADO, EstadoPesaje.DESPACHADO))
                .thenThrow(new IllegalWeighingStateException("Transición no permitida"));

        assertThatThrownBy(() -> service.actualizarEstado("id2", EstadoPesaje.DESPACHADO))
                .isInstanceOf(IllegalWeighingStateException.class);

        verify(repository, never()).save(any());
    }

    // ─── obtenerPorFecha ──────────────────────────────────────────────────────

    @Test
    void obtenerPorFecha_delegaAlRepositorio() {
        LocalDateTime desde = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime hasta = LocalDateTime.of(2024, 1, 31, 23, 59);
        List<RegistroPesaje> lista = List.of(new RegistroPesaje(), new RegistroPesaje());
        when(repository.findByCreatedAtBetween(desde, hasta)).thenReturn(lista);

        List<RegistroPesaje> resultado = service.obtenerPorFecha(desde, hasta);

        assertThat(resultado).hasSize(2);
        verify(repository).findByCreatedAtBetween(desde, hasta);
    }

    @Test
    void obtenerPorFecha_sinResultados_retornaListaVacia() {
        LocalDateTime desde = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime hasta = LocalDateTime.of(2024, 6, 30, 23, 59);
        when(repository.findByCreatedAtBetween(desde, hasta)).thenReturn(List.of());

        assertThat(service.obtenerPorFecha(desde, hasta)).isEmpty();
    }
}