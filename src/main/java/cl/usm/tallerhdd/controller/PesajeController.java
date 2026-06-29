package cl.usm.tallerhdd.controller;

import cl.usm.tallerhdd.model.EstadoPesaje;
import cl.usm.tallerhdd.model.RegistroPesaje;
import cl.usm.tallerhdd.service.PesajeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pesajes")
public class PesajeController {

    private final PesajeService pesajeService;

    public PesajeController(PesajeService pesajeService) {
        this.pesajeService = pesajeService;
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Map<String, String> body) {
        try {
            String idBalanza = body.get("idBalanza");
            String idPaquete = body.get("idPaquete");
            double pesoKg = Double.parseDouble(body.get("pesoKg"));

            RegistroPesaje registro = pesajeService.registrarPesaje(idBalanza, idPaquete, pesoKg);
            return ResponseEntity.status(HttpStatus.CREATED).body(registro);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable String id,
                                              @RequestBody Map<String, String> body) {
        try {
            EstadoPesaje nuevoEstado = EstadoPesaje.valueOf(body.get("estado"));
            RegistroPesaje actualizado = pesajeService.actualizarEstado(id, nuevoEstado);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "Estado inválido: " + body.get("estado")));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        try {
            List<RegistroPesaje> registros = pesajeService.obtenerPorFecha(desde, hasta);
            return ResponseEntity.ok(registros);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
        }
    }
}