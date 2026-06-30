package cl.usm.tallerhdd.repository;

import cl.usm.tallerhdd.model.CategoriaPeso;
import cl.usm.tallerhdd.model.EstadoPesaje;
import cl.usm.tallerhdd.model.RegistroPesaje;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.Start;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class RegistroPesajeRepositoryTest {

    private static final TransitionWalker.ReachedState<RunningMongodProcess> mongod =
            Mongod.builder()
                    .net(Start.to(Net.class).initializedWith(Net.builder().port(27017).isIpv6(false).build()))
                    .build()
                    .start(Version.Main.V7_0);

    @AfterAll
    static void detenerMongoEmbebido() {
        mongod.close();
    }

    @Autowired
    RegistroPesajeRepository repository;

    @BeforeEach
    void limpiar() {
        repository.deleteAll();
    }

    private RegistroPesaje registro(String idPaquete, LocalDateTime createdAt) {
        RegistroPesaje r = new RegistroPesaje();
        r.setIdBalanza("4");
        r.setIdPaquete(idPaquete);
        r.setPesoSansas(7.48);
        r.setCategoria(CategoriaPeso.LIVIANO);
        r.setEstado(EstadoPesaje.INGRESADO);
        r.setCreatedAt(createdAt);
        r.setUpdatedAt(createdAt);
        return r;
    }

    // ─── save / findById ─────────────────────────────────────────────────────

    @Test
    void guardar_asignaIdAutomatico() {
        RegistroPesaje r = registro("PKG-1", LocalDateTime.now());
        RegistroPesaje guardado = repository.save(r);

        assertThat(guardado.getId()).isNotNull().isNotBlank();
    }

    @Test
    void findById_retornaRegistroGuardado() {
        RegistroPesaje guardado = repository.save(registro("PKG-2", LocalDateTime.now()));

        Optional<RegistroPesaje> encontrado = repository.findById(guardado.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getIdPaquete()).isEqualTo("PKG-2");
    }

    @Test
    void findById_idInexistente_retornaVacio() {
        assertThat(repository.findById("id-inexistente")).isEmpty();
    }

    @Test
    void guardar_historialEstadosSeConserva() {
        RegistroPesaje r = registro("PKG-3", LocalDateTime.now());
        r.getHistorialEstados().add("INGRESADO @ 2024-01-01T10:00");
        RegistroPesaje guardado = repository.save(r);

        RegistroPesaje encontrado = repository.findById(guardado.getId()).orElseThrow();
        assertThat(encontrado.getHistorialEstados()).hasSize(1)
                .first().asString().startsWith("INGRESADO");
    }

    // ─── findByCreatedAtBetween ───────────────────────────────────────────────

    @Test
    void findByCreatedAtBetween_retornaRegistrosDentroDelRango() {
        LocalDateTime base = LocalDateTime.of(2024, 6, 15, 10, 0);
        repository.save(registro("PKG-ANTES", base.minusDays(1)));   // fuera
        repository.save(registro("PKG-A",     base));                 // dentro
        repository.save(registro("PKG-B",     base.plusHours(2)));    // dentro
        repository.save(registro("PKG-DESPUES", base.plusDays(2)));   // fuera

        List<RegistroPesaje> resultado = repository.findByCreatedAtBetween(
                base.minusMinutes(1), base.plusDays(1));

        assertThat(resultado).hasSize(2)
                .extracting(RegistroPesaje::getIdPaquete)
                .containsExactlyInAnyOrder("PKG-A", "PKG-B");
    }

    @Test
    void findByCreatedAtBetween_sinResultados_retornaListaVacia() {
        LocalDateTime base = LocalDateTime.of(2024, 6, 15, 10, 0);
        repository.save(registro("PKG-1", base));

        List<RegistroPesaje> resultado = repository.findByCreatedAtBetween(
                base.plusDays(1), base.plusDays(2));

        assertThat(resultado).isEmpty();
    }

    @Test
    void findByCreatedAtBetween_incluyeExtremosSiCoinciden() {
        LocalDateTime exacto = LocalDateTime.of(2024, 6, 15, 10, 0);
        repository.save(registro("PKG-EXACTO", exacto));

        List<RegistroPesaje> resultado = repository.findByCreatedAtBetween(exacto, exacto);

        assertThat(resultado).hasSize(1);
    }

    // ─── findAll / deleteAll ──────────────────────────────────────────────────

    @Test
    void findAll_retornaTodosLosRegistros() {
        repository.save(registro("PKG-X", LocalDateTime.now()));
        repository.save(registro("PKG-Y", LocalDateTime.now()));

        assertThat(repository.findAll()).hasSize(2);
    }

    @Test
    void deleteAll_eliminaTodos() {
        repository.save(registro("PKG-Z", LocalDateTime.now()));
        repository.deleteAll();

        assertThat(repository.findAll()).isEmpty();
    }
}