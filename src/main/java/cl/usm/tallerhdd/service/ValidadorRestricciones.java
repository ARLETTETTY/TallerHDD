package cl.usm.tallerhdd.service;
import cl.usm.tallerhdd.model.CategoriaPeso;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class ValidadorRestricciones {

    private final Clock clock;

    public ValidadorRestricciones(Clock clock) {
        this.clock = clock;
    }

    public boolean esHorarioNocturnoProhibido (CategoriaPeso categoria) {
        if (categoria != CategoriaPeso.PESADO) {
            return false;
        }
        int hora = LocalDateTime.now(clock).getHour();
        return hora >= 20 || hora < 6;
    }

    public boolean esBalanzaPrimaBloqueada (int idBalanza, CategoriaPeso categoria) {
        if (categoria != CategoriaPeso.PESADO) {
            return false;
        }
        if (!esPrimo(idBalanza)){
            return false;
        }
        int diaDelMes = LocalDateTime.now(clock).getDayOfMonth();
        boolean diaImpar = (diaDelMes % 2) != 0;
        return diaImpar;
    }

    private boolean esPrimo(int numero) {
        if (numero < 2) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(numero); i++) {
            if (numero % i == 0) {
                return false;
            }
        }
        return true;
    }
}

