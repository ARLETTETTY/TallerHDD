package cl.usm.tallerhdd.controller;

import cl.usm.tallerhdd.model.EstadoPesaje;
import cl.usm.tallerhdd.model.RegistroPesaje;
import cl.usm.tallerhdd.service.PesajeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.usm.tallerhdd.controller.dto.RegistrarPesajeRequest;
import cl.usm.tallerhdd.controller.dto.ActualizarEstadoRequest;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pesajes")
public class PesajeController {

    private final PesajeService pesajeService;

    public PesajeController(PesajeService pesajeService) {
        this.pesajeService = pesajeService;
    }


    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody RegistrarPesajeRequest request) {
        RegistroPesaje registro = pesajeService.registrarPesaje(
                request.getIdBalanza(), request.getIdPaquete(), request.getPesoKg());
        return ResponseEntity.status(HttpStatus.CREATED).body(registro);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable String id,
                                              @RequestBody ActualizarEstadoRequest request) {
        EstadoPesaje nuevoEstado = EstadoPesaje.valueOf(request.getEstado());
        RegistroPesaje actualizado = pesajeService.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping
    public ResponseEntity<List<RegistroPesaje>> obtenerPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        List<RegistroPesaje> registros = pesajeService.obtenerPorFecha(desde, hasta);
        return ResponseEntity.ok(registros);
    }
}