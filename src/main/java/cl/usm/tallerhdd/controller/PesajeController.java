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
    public ResponseEntity<RegistroPesaje> registrar(@RequestBody Map<String, String> body) {
        String idBalanza = body.get("idBalanza");
        String idPaquete = body.get("idPaquete");
        double pesoKg = Double.parseDouble(body.get("pesoKg")); // si no es número -> NumberFormatException -> 400

        RegistroPesaje registro = pesajeService.registrarPesaje(idBalanza, idPaquete, pesoKg);
        return ResponseEntity.status(HttpStatus.CREATED).body(registro);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<RegistroPesaje> actualizarEstado(@PathVariable String id,
                                                           @RequestBody Map<String, String> body) {
        EstadoPesaje nuevoEstado = EstadoPesaje.valueOf(body.get("estado")); // estado inexistente -> 400
        RegistroPesaje actualizado = pesajeService.actualizarEstado(id, nuevoEstado); // transición inválida -> 409
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