package LD_insulinPump;

import LD_InsulinPump.*;
import LD_InsulinPump_Mock.NeedleRandomImpl;
import LD_InsulinPump_Mock.PumpRandomImpl;
import LD_InsulinPump_Mock.SensorRandomImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

public class TestConfig
{
    //Can't really test 'template' because it doesn't have an equals() method implemented

    @Test
    public void testControllerStateSet()
    {
        assertEquals(new Config().controllerState(), ControllerState.RUNNING);
    }

    @Test
    public void testSensorSet()
    {
        assertEquals(new Config().sensor(), new SensorRandomImpl(new Random(), new Random()));
    }

    @Test
    public void testPumpSet()
    {
        assertEquals(new Config().pump(), new PumpRandomImpl(new Random()));
    }

    @Test
    public void testNeedleSet()
    {
        assertEquals(new Config().needle(), new NeedleRandomImpl(new Random()));
    }

    @Test
    public void testMeasurementsList()
    {
        assertEquals(new Config().measurementsList(), new ArrayList<Measurement>());
    }
}
