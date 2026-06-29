package cl.usm.tallerhdd.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "registros_pesaje")
public class RegistroPesaje {

    @Id
    private String id;

    private String idBalanza;
    private String idPaquete;
    private double pesoSansas;
    private CategoriaPeso categoria;
    private EstadoPesaje estado;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<String> historialEstados = new ArrayList<>();
}