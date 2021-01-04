package LD_insulinPump;
import LD_InsulinPump.*;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;


public class TestSensorJU {

    @Test
    public void testSuccessRunMeasurement() throws HardwareIssueException
    {
        //using 1 as a 'random' number: the HW test will pass
        Sensor sensor = new SensorRandomImpl(new Random(), new RandomHardwareFixTest(1));
        Float bloodSugar = sensor.runMeasurement();

        assertTrue(bloodSugar >= 1f && bloodSugar <=35f);
    }

    @Test
    public void testFailRunMeasurement()
    {
        // using 0 as a 'random' number: the HW check will fail
        Sensor sensor = new SensorRandomImpl(new Random(), new RandomHardwareFixTest(0));
        assertThrows(HardwareIssueException.class, () -> {
            sensor.runMeasurement();
        });
    }

    @Test
    public void testIsHardwareWorking()
    {
        //using 1 as a 'random' number: the HW test will pass
        Sensor sensor = new SensorRandomImpl(new Random(), new RandomHardwareFixTest(1));
        assertTrue(((SensorRandomImpl) sensor).isHardwareWorking());
    }

    @Test
    public void testIsHardwareNotWorking()
    {
        //using 877 as a 'random' number: the HW test will fail
        Sensor sensor = new SensorRandomImpl(new Random(), new RandomHardwareFixTest(0));
        assertFalse(((SensorRandomImpl) sensor).isHardwareWorking());
    }
}
