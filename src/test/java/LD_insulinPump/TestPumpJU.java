package LD_insulinPump;

import LD_InsulinPump.*;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class TestPumpJU
{
    @Test(expected = Test.None.class /* no exception expected */)
    public void testSuccessCollectInsulin() throws HardwareIssueException
    {
        //using 1 as a 'random' number: the HW test will pass
        Pump pump = new PumpRandomImpl(new RandomHardwareFixTest(1));
        pump.collectInsulin(5);
    }

    @Test
    public void testFailCollectInsulin()
    {
        // using 0 as a 'random' number: the HW check will fail
        Pump pump = new PumpRandomImpl(new RandomHardwareFixTest(0));

        assertThrows(HardwareIssueException.class, () -> {
            pump.collectInsulin(5);
        });
    }

    @Test
    public void testIsHardwareWorking()
    {
        //using 1 as a 'random' number: the HW test will pass
        Pump pump = new PumpRandomImpl(new RandomHardwareFixTest(1));
        assertTrue(((PumpRandomImpl) pump).isHardwareWorking());
    }

    @Test
    public void testIsHardwareNotWorking()
    {
        //using 0 as a 'random' number: the HW test will fail
        Pump pump = new PumpRandomImpl(new RandomHardwareFixTest(0));
        assertFalse(((PumpRandomImpl) pump).isHardwareWorking());
    }

    @Test
    public void testEquals()
    {
        assertTrue(new PumpRandomImpl(new Random()).equals(new PumpRandomImpl(new Random())));
    }
}
