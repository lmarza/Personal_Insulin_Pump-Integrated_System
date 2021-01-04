package LD_InsulinPump;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Measurement {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Integer compDose;
    private Float r0,r1,r2;

    public Measurement(){}

    public Measurement(Float r2) {
        this(null, null, r2);
    }

    public Measurement(Float r1, Float r2) {
        this(null,r1,r2);
    }

    public Measurement(Float r0, Float r1, Float r2) {
        this.compDose = 0;
        this.r0 = r0;
        this.r1 = r1;
        this.r2 = r2;
    }

    public Integer getCompDose() {
        return compDose;
    }

    public void setCompDose(Integer compDose) {
        this.compDose = compDose;
    }

    public Float getR0() {
        return r0;
    }

    public void setR0(Float r0) {
        this.r0 = r0;
    }

    public Float getR1() {
        return r1;
    }

    public void setR1(Float r1) {
        this.r1 = r1;
    }

    public Float getR2() {
        return r2;
    }

    public void setR2(Float r2) {
        this.r2 = r2;
    }

    @Override
    public String toString() {
        return "Measurement[" +
                "compDose=" + compDose +
                ", r0=" + String.format("%.2f", r0) +
                ", r1=" + String.format("%.2f", r1) +
                ", r2=" + String.format("%.2f", r2) +
                ']';
    }

    public boolean hasExactlyOneMeasurement() {
        return this.r2 != null && this.r1 == null && this.r0 == null;
    }

    public boolean hasThreeMeasurements() {
        return this.r2 != null && this.r1 != null && this.r0 != null;
    }
}
