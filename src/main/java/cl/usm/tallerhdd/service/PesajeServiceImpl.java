package cl.usm.tallerhdd.service;

import cl.usm.tallerhdd.exception.IllegalWeighingStateException;
import cl.usm.tallerhdd.model.CategoriaPeso;
import cl.usm.tallerhdd.model.EstadoPesaje;
import cl.usm.tallerhdd.model.RegistroPesaje;
import cl.usm.tallerhdd.repository.RegistroPesajeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PesajeServiceImpl implements PesajeService {

    private final ConversorPeso conversorPeso;
    private final ClasificadorPeso clasificadorPeso;
    private final ValidadorRestricciones validadorRestricciones;
    private final MaquinaEstados maquinaEstados;
    private final RegistroPesajeRepository repository;

    public PesajeServiceImpl(ConversorPeso conversorPeso,
                             ClasificadorPeso clasificadorPeso,
                             ValidadorRestricciones validadorRestricciones,
                             MaquinaEstados maquinaEstados,
                             RegistroPesajeRepository repository) {
        this.conversorPeso = conversorPeso;
        this.clasificadorPeso = clasificadorPeso;
        this.validadorRestricciones = validadorRestricciones;
        this.maquinaEstados = maquinaEstados;
        this.repository = repository;
    }

    @Override
    public RegistroPesaje registrarPesaje(String idBalanza, String idPaquete, double pesoKg) {
        double pesoSansas = conversorPeso.kgSansa(pesoKg);
        CategoriaPeso categoria = clasificadorPeso.clasificar(pesoSansas);

        if (validadorRestricciones.esHorarioNocturnoProhibido(categoria)) {
            throw new IllegalWeighingStateException(
                    "No se permite registrar paquetes PESADOS en horario nocturno (20:00 - 06:00)");
        }

        int idBalanzaInt;
        try {
            idBalanzaInt = Integer.parseInt(idBalanza);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El ID de balanza debe ser numérico: " + idBalanza);
        }

        if (validadorRestricciones.esBalanzaPrimaBloqueada(idBalanzaInt, categoria)) {
            throw new IllegalWeighingStateException(
                    "Balanza con ID primo no puede registrar paquetes PESADOS en días impares");
        }

        LocalDateTime ahora = LocalDateTime.now();

        RegistroPesaje registro = new RegistroPesaje();
        registro.setIdBalanza(idBalanza);
        registro.setIdPaquete(idPaquete);
        registro.setPesoSansas(pesoSansas);
        registro.setCategoria(categoria);
        registro.setEstado(EstadoPesaje.INGRESADO);
        registro.setCreatedAt(ahora);
        registro.setUpdatedAt(ahora);
        registro.getHistorialEstados().add("INGRESADO @ " + ahora);

        return repository.save(registro);
    }

    @Override
    public RegistroPesaje actualizarEstado(String id, EstadoPesaje nuevoEstado) {
        RegistroPesaje registro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado: " + id));

        EstadoPesaje estadoResultante = maquinaEstados.transicionar(registro.getEstado(), nuevoEstado);

        LocalDateTime ahora = LocalDateTime.now();
        registro.setEstado(estadoResultante);
        registro.setUpdatedAt(ahora);
        registro.getHistorialEstados().add(estadoResultante + " @ " + ahora);

        return repository.save(registro);
    }

    @Override
    public List<RegistroPesaje> obtenerPorFecha(LocalDateTime desde, LocalDateTime hasta) {
        return repository.findByCreatedAtBetween(desde, hasta);
    }
}