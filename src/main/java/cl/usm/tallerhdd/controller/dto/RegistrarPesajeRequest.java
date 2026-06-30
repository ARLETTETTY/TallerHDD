package cl.usm.tallerhdd.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrarPesajeRequest {

    private String idBalanza;
    private String idPaquete;
    private double pesoKg;
}