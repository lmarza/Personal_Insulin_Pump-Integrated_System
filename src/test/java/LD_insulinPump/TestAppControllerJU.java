package LD_insulinPump;

import LD_InsulinPump.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestAppControllerJU
{
    //TODO: da sistemare testupdatemeasurement

    /*
    * public void updateMeasurement(Float lastMeasurement, List<Measurement> measurementList){
        if(measurementList.isEmpty())
            measurementList.add(new Measurement(lastMeasurement));
        else if (measurementList.size() == 1)
            measurementList.add(new Measurement(measurementList.get(0).getR2(), lastMeasurement));
        else
            measurementList.add(new Measurement(measurementList.get(measurementList.size()-1).getR1(), measurementList.get(measurementList.size()-1).getR2(),lastMeasurement));
    }*/

    @Test
    public void TestUpdateMeasurementBloodSugarLevel() {
        AppController appController = new AppController();
        List<Measurement> measurementList = new ArrayList<>();

        //empty list
        appController.updateMeasurement(21.53f, measurementList);
        assertEquals(1, measurementList.size());
        assertEquals(measurementList.get(0).getR2(), Float.valueOf(21.53f));

        //1 element in list
        appController.updateMeasurement(16.74f, measurementList);
        assertEquals(2, measurementList.size());
        assertEquals(measurementList.get(1).getR2(), Float.valueOf(16.74f));
        assertEquals(measurementList.get(1).getR1(), Float.valueOf(21.53f));

        //2 elements in list
        appController.updateMeasurement(3.79f, measurementList);
        assertEquals(3, measurementList.size());
        assertEquals(measurementList.get(2).getR2(), Float.valueOf(3.79f));
        assertEquals(measurementList.get(2).getR1(), Float.valueOf(16.74f));
        assertEquals(measurementList.get(2).getR0(), Float.valueOf(21.53f));

        //3 or more elements in list
        appController.updateMeasurement(5.47f, measurementList);
        assertTrue(measurementList.size() > 3);
        assertEquals(measurementList.get(measurementList.size()-1).getR2(), Float.valueOf(5.47f));
        assertEquals(measurementList.get(measurementList.size()-1).getR1(), Float.valueOf(3.79f));
        assertEquals(measurementList.get(measurementList.size()-1).getR0(), Float.valueOf(16.74f));
    }

    /**
     * This method tests the computation of insulin to inject in the case of blood sugar level falling (r2 < r1)
     */
    @Test
    public void testComputeInsulinToInjectC1()
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
    public void testComputeInsulinToInjectC2()
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
    public void testComputeInsulinToInjectC3()
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
    public void testComputeInsulinToInjectC4()
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
    public void testComputeInsulinToInjectC5()
    {
        Measurement measurement = new Measurement(33.0f, 34.0f, 35.0f);
        assertTrue(measurement.getR2() > measurement.getR1());
        assertTrue(measurement.getR2() - measurement.getR1() >= measurement.getR1()- measurement.getR0());
        assertEquals(measurement.getCompDose(), Integer.valueOf(0));
        measurement.setCompDose(new AppController().computeInsulinToInject(measurement));
        assertEquals(measurement.getCompDose(), Integer.valueOf(1));
    }
}
