package LD_InsulinPump;

import java.util.Objects;

public class Measurement {
    private Integer compDose;
    private Float r0,r1,r2;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Measurement)) return false;
        Measurement that = (Measurement) o;
        return Objects.equals(compDose, that.compDose) &&
                Objects.equals(r0, that.r0) &&
                Objects.equals(r1, that.r1) &&
                Objects.equals(r2, that.r2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compDose, r0, r1, r2);
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
}
