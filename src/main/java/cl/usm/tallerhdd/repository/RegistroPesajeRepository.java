package cl.usm.tallerhdd.repository;

import cl.usm.tallerhdd.model.RegistroPesaje;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroPesajeRepository extends MongoRepository<RegistroPesaje, String> {

    @Query("{ 'createdAt' : { $gte: ?0, $lte: ?1 } }")
    List<RegistroPesaje> findByCreatedAtBetween(LocalDateTime desde, LocalDateTime hasta);
}