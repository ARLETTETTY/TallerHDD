package cl.usm.tallerhdd.service;
import cl.usm.tallerhdd.model.CategoriaPeso;
import org.springframework.stereotype.Component;
@Component
public class ClasificadorPeso {

    public CategoriaPeso clasificar (double sansas){
        if (sansas<= 10){
            return CategoriaPeso.LIVIANO;
        } else if (sansas<= 50){
            return  CategoriaPeso.MEDIANO;
        } else {
            return CategoriaPeso.PESADO;
        }
    }
}

