package cl.usm.tallerhdd.repository;

import cl.usm.tallerhdd.model.RegistroPesaje;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroPesajeRepository extends MongoRepository<RegistroPesaje, String> {

    List<RegistroPesaje> findByCreatedAtBetween(LocalDateTime desde, LocalDateTime hasta);
}