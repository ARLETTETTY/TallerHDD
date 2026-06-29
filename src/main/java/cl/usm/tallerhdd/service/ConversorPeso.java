package cl.usm.tallerhdd.service;
import org.springframework.stereotype.Component;

@Component
public class ConversorPeso {

    private static final double KG_POR_SANSA = 1.337;

    public double kgSansa(double kg) {
        return kg * KG_POR_SANSA;
    }

    public double sansaAKg(double sansas) {
        return sansas * KG_POR_SANSA;
    }
}

