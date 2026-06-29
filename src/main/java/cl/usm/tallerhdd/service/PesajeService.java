package cl.usm.tallerhdd.service;

import cl.usm.tallerhdd.model.EstadoPesaje;
import cl.usm.tallerhdd.model.RegistroPesaje;

import java.time.LocalDateTime;
import java.util.List;

public interface PesajeService {

    RegistroPesaje registrarPesaje(String idBalanza, String idPaquete, double pesoKg);

    RegistroPesaje actualizarEstado(String id, EstadoPesaje nuevoEstado);

    List<RegistroPesaje> obtenerPorFecha(LocalDateTime desde, LocalDateTime hasta);
}