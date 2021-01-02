package LD_insulinPump;
import LD_InsulinPump.Measurement;
import LD_InsulinPump.Sensor;
import org.junit.Test;
import static org.junit.Assert.*;


public class TestMeasurementJU {

    @Test
    public void TestLastMeasurementBloodSugarLevel()
    {
        Measurement measurement = new Measurement();
        assertNull(measurement.getR2());

        Sensor sensor = new Sensor();
        Float fixedValue = 2.5f;
        measurement.setR2(sensor.fixedMeasurement(fixedValue));
        assertEquals(fixedValue, measurement.getR2());
    }

    @Test
    public void TestUpdateMeasurementBloodSugarLevel()
    {
        Measurement measurement1;
        Measurement measurement2;
        Measurement measurement3;
        Sensor sensor = new Sensor();

        measurement1 = new Measurement(sensor.fixedMeasurement(2.5f));
        assertNull(measurement1.getR1());
        assertNull(measurement1.getR0());
        assertEquals(new Float(2.5f), measurement1.getR2());

        measurement2 = new Measurement( measurement1.getR2(), sensor.fixedMeasurement(3.7f));
        assertNull(measurement2.getR0());
        assertEquals(new Float(3.7f), measurement2.getR2());
        assertEquals(new Float(2.5f), measurement2.getR1());

        measurement3 = new Measurement(measurement2.getR1(), measurement2.getR2(), sensor.fixedMeasurement(18.9f));
        assertEquals(new Float(18.9f), measurement3.getR2());
        assertEquals(new Float(3.7f), measurement3.getR1());
        assertEquals(new Float(2.5f), measurement3.getR0());

    }

}
