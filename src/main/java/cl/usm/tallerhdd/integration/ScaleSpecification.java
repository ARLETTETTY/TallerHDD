package cl.usm.tallerhdd.integration;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ScaleSpecification {

    private String id;
    private String name;
    private String brand;
    private double maxCapacity;
    private double precision;
    private double lastCalibrationOffset;
}