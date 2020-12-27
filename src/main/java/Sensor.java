import java.util.Random;

public class Sensor {
    Random randomNumber;
    final static Float maxBloodSugarValue = 35f;

    public Float runMeasurement(){
        //TODO: check if it is correct
        return 1 + randomNumber.nextFloat() * (maxBloodSugarValue - 1) ;
    }
}
