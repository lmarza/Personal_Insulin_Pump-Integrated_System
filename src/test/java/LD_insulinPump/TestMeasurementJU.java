package LD_insulinPump;

import LD_InsulinPump.AppController;
import LD_InsulinPump.Measurement;
import LD_InsulinPump.Sensor;
import LD_InsulinPump.SensorRandomImpl;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class TestMeasurementJU {

    /*
    @Test
    public void TestUpdateMeasurementBloodSugarLevel()
    {
        Measurement measurement1;
        Measurement measurement2;
        Measurement measurement3;
        Sensor sensor = new SensorRandomImpl();

        measurement1 = new Measurement(sensor.fixedMeasurement(2.5f));
        assertNull(measurement1.getR1());
        assertNull(measurement1.getR0());
        assertEquals(Float.valueOf(2.5f), measurement1.getR2());

        measurement2 = new Measurement( measurement1.getR2(), sensor.fixedMeasurement(3.7f));
        assertNull(measurement2.getR0());
        assertEquals(Float.valueOf(3.7f), measurement2.getR2());
        assertEquals(Float.valueOf(2.5f), measurement2.getR1());

        measurement3 = new Measurement(measurement2.getR1(), measurement2.getR2(), sensor.fixedMeasurement(18.9f));
        assertEquals(Float.valueOf(18.9f), measurement3.getR2());
        assertEquals(Float.valueOf(3.7f), measurement3.getR1());
        assertEquals(Float.valueOf(2.5f), measurement3.getR0());

    }*/

    /**
     * This method tests the computation of insulin to inject in the case of blood sugar level falling (r2 < r1)
     */

    @Test
    public void TestComputeInsulinToInjectC1()
    {
        Measurement measurement = new Measurement(15.84f, 10.35f);
        assertTrue(measurement.getR2() < measurement.getR1());
        assertEquals(measurement.getCompDose(), Integer.valueOf(0));
        measurement.setCompDose(new AppController().computeInsulinToInject(measurement));
        assertEquals(measurement.getCompDose(), Integer.valueOf(0));
    }

    /**
     * This method tests the computation of insulin to inject in the case of blood sugar level stable (r2 = r1)
     */

    @Test
    public void TestComputeInsulinToInjectC2()
    {
        Measurement measurement = new Measurement(15.84f, 15.84f);
        assertEquals(measurement.getR2(), measurement.getR1());
        assertEquals(measurement.getCompDose(), Integer.valueOf(0));
        measurement.setCompDose(new AppController().computeInsulinToInject(measurement));
        assertEquals(measurement.getCompDose(), Integer.valueOf(0));
    }

    /**
     * This method tests the computation of insulin to inject in the case of blood sugar level increasing (r2 > r1)
     * and the rate of increase decreasing ((r2 - r1) < (r1 - r0))
     */

    @Test
    public void TestComputeInsulinToInjectC3()
    {
        Measurement measurement = new Measurement(15.34f, 20.84f, 21.84f);
        assertTrue(measurement.getR2() > measurement.getR1());
        assertTrue(measurement.getR2() - measurement.getR1() < measurement.getR1()- measurement.getR0());
        assertEquals(measurement.getCompDose(), Integer.valueOf(0));
        measurement.setCompDose(new AppController().computeInsulinToInject(measurement));
        assertEquals(measurement.getCompDose(), Integer.valueOf(0));
    }

    /**
     * This method tests the computation of insulin to inject in the case of blood sugar level increasing (r2 > r1)
     * and the rate of increase stable or increasing ((r2 - r1) >= (r1 - r0))
     */

    @Test
    public void TestComputeInsulinToInjectC4()
    {
        Measurement measurement = new Measurement(15.20f, 20.80f, 34.90f);
        assertTrue(measurement.getR2() > measurement.getR1());
        assertTrue(measurement.getR2() - measurement.getR1() >= measurement.getR1()- measurement.getR0());
        assertEquals(measurement.getCompDose(), Integer.valueOf(0));
        measurement.setCompDose(new AppController().computeInsulinToInject(measurement));
        assertEquals(measurement.getCompDose(), Integer.valueOf(4));
    }

    /**
     * This method tests the computation of insulin to inject in the case of blood sugar level increasing (r2 > r1)
     * and the rate of increase stable or increasing ((r2 - r1) >= (r1 - r0)) and result of rounded division not equal to zero
     * In this case we also test the minDose of insulin injection
     */

    @Test
    public void TestComputeInsulinToInjectC5()
    {
        Measurement measurement = new Measurement(33.0f, 34.0f, 35.0f);
        assertTrue(measurement.getR2() > measurement.getR1());
        assertTrue(measurement.getR2() - measurement.getR1() >= measurement.getR1()- measurement.getR0());
        assertEquals(measurement.getCompDose(), Integer.valueOf(0));
        measurement.setCompDose(new AppController().computeInsulinToInject(measurement));
        assertEquals(measurement.getCompDose(), Integer.valueOf(1));
    }
}
