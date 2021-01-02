package LD_InsulinPump;

import java.util.Random;

public class Sensor {
    Random randomNumber = new Random();
    final static Float maxBloodSugarValue = 35f;

    public Float runMeasurement(){
        return 1 + randomNumber.nextFloat() * (maxBloodSugarValue - 1) ;
    }

    public Float fixedMeasurement(Float fixedValue)
    {
        return fixedValue;
    }
}
