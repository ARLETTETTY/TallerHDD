package cl.usm.tallerhdd.controller;

import cl.usm.tallerhdd.exception.IllegalWeighingStateException;
import cl.usm.tallerhdd.model.CategoriaPeso;
import cl.usm.tallerhdd.model.EstadoPesaje;
import cl.usm.tallerhdd.model.RegistroPesaje;
import cl.usm.tallerhdd.service.PesajeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PesajeController.class)
class PesajeControllerTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean PesajeService pesajeService;

    private RegistroPesaje registroEjemplo() {
        RegistroPesaje r = new RegistroPesaje();
        r.setId("reg-1");
        r.setIdBalanza("4");
        r.setIdPaquete("PKG-1");
        r.setPesoSansas(7.48);
        r.setCategoria(CategoriaPeso.LIVIANO);
        r.setEstado(EstadoPesaje.INGRESADO);
        r.setCreatedAt(LocalDateTime.of(2024, 6, 15, 10, 0));
        r.setUpdatedAt(LocalDateTime.of(2024, 6, 15, 10, 0));
        return r;
    }

    // ─── POST /api/pesajes ────────────────────────────────────────────────────

    @Test
    void registrar_exitoso_retorna201ConBody() throws Exception {
        when(pesajeService.registrarPesaje(any(), any(), any(Double.class)))
                .thenReturn(registroEjemplo());

        mockMvc.perform(post("/api/pesajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("idBalanza", "4", "idPaquete", "PKG-1", "pesoKg", "10.0"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("reg-1"))
                .andExpect(jsonPath("$.estado").value("INGRESADO"));
    }

    @Test
    void registrar_pesoKgNoNumerico_retorna400() throws Exception {
        mockMvc.perform(post("/api/pesajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("idBalanza", "4", "idPaquete", "PKG-1", "pesoKg", "no-es-numero"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrar_horarioNocturnoProhibido_retorna409() throws Exception {
        when(pesajeService.registrarPesaje(any(), any(), any(Double.class)))
                .thenThrow(new IllegalWeighingStateException("No se permite en horario nocturno"));

        mockMvc.perform(post("/api/pesajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("idBalanza", "3", "idPaquete", "PKG-1", "pesoKg", "94.0"))))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("nocturno")));
    }

    @Test
    void registrar_idBalanzaInvalido_retorna400() throws Exception {
        when(pesajeService.registrarPesaje(any(), any(), any(Double.class)))
                .thenThrow(new IllegalArgumentException("El ID de balanza debe ser numérico"));

        mockMvc.perform(post("/api/pesajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("idBalanza", "abc", "idPaquete", "PKG-1", "pesoKg", "10.0"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrar_balanzaPrimaBloqueada_retorna409() throws Exception {
        when(pesajeService.registrarPesaje(any(), any(), any(Double.class)))
                .thenThrow(new IllegalWeighingStateException("Balanza con ID primo no puede registrar"));

        mockMvc.perform(post("/api/pesajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("idBalanza", "7", "idPaquete", "PKG-1", "pesoKg", "94.0"))))
                .andExpect(status().isConflict());
    }

    // ─── PUT /api/pesajes/{id}/estado ─────────────────────────────────────────

    @Test
    void actualizarEstado_exitoso_retorna200() throws Exception {
        RegistroPesaje r = registroEjemplo();
        r.setEstado(EstadoPesaje.PESADO);
        when(pesajeService.actualizarEstado(eq("reg-1"), eq(EstadoPesaje.PESADO))).thenReturn(r);

        mockMvc.perform(put("/api/pesajes/reg-1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("estado", "PESADO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PESADO"));
    }

    @Test
    void actualizarEstado_transicionInvalida_retorna409() throws Exception {
        when(pesajeService.actualizarEstado(any(), any()))
                .thenThrow(new IllegalWeighingStateException("Transición no permitida"));

        mockMvc.perform(put("/api/pesajes/reg-1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("estado", "DESPACHADO"))))
                .andExpect(status().isConflict());
    }

    @Test
    void actualizarEstado_estadoInexistente_retorna400() throws Exception {
        mockMvc.perform(put("/api/pesajes/reg-1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("estado", "ESTADO_INVALIDO"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarEstado_registroNoEncontrado_retorna500() throws Exception {
        when(pesajeService.actualizarEstado(eq("nope"), any()))
                .thenThrow(new RuntimeException("Registro no encontrado: nope"));

        mockMvc.perform(put("/api/pesajes/nope/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("estado", "PESADO"))))
                .andExpect(status().isInternalServerError());
    }

    // ─── GET /api/pesajes ─────────────────────────────────────────────────────

    @Test
    void obtenerPorFecha_exitoso_retornaLista() throws Exception {
        when(pesajeService.obtenerPorFecha(any(), any())).thenReturn(List.of(registroEjemplo()));

        mockMvc.perform(get("/api/pesajes")
                        .param("desde", "2024-01-01T00:00:00")
                        .param("hasta", "2024-01-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("reg-1"));
    }

    @Test
    void obtenerPorFecha_sinResultados_retornaListaVacia() throws Exception {
        when(pesajeService.obtenerPorFecha(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/pesajes")
                        .param("desde", "2024-01-01T00:00:00")
                        .param("hasta", "2024-01-02T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}