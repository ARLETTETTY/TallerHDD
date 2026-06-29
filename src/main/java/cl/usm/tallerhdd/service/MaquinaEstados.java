package cl.usm.tallerhdd.service;
import cl.usm.tallerhdd.model.EstadoPesaje;
import cl.usm.tallerhdd.exception.IllegalWeighingStateException;
import org.springframework.stereotype.Component;

@Component
public class MaquinaEstados {

    public EstadoPesaje transicionar( EstadoPesaje actual, EstadoPesaje destino){
        boolean permitida = switch (actual){
            case INGRESADO -> destino == EstadoPesaje.PESADO;
            case PESADO -> destino == EstadoPesaje.APROBADO || destino == EstadoPesaje.RECHAZADO;
            case APROBADO  -> destino == EstadoPesaje.DESPACHADO;
            case RECHAZADO -> destino == EstadoPesaje.DESPACHADO;
            case DESPACHADO -> false;
        };
    if (!permitida){
        throw new IllegalWeighingStateException(
                "Transición no permitida" + actual + "->" + destino);
            }
    return destino;
    }
}
