package LD_insulinPump;

import LD_InsulinPump.*;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class TestNeedleAssemblyJU
{
    @Test(expected = Test.None.class /* no exception expected */)
    public void testSuccessInjectInsulin() throws HardwareIssueException
    {
        //using 1 as a 'random' number: the HW test will pass
        NeedleAssembly needle = new NeedleRandomImpl(new RandomHardwareFixTest(1));
        needle.injectInsulin(5);
    }

    @Test
    public void testFailInjectInsulin()
    {
        // using 0 as a 'random' number: the HW check will fail
        NeedleAssembly needle = new NeedleRandomImpl(new RandomHardwareFixTest(0));

        assertThrows(HardwareIssueException.class, () -> {
            needle.injectInsulin(5);
        });
    }

    @Test
    public void testIsHardwareWorking()
    {
        //using 1 as a 'random' number: the HW test will pass
        NeedleAssembly needle = new NeedleRandomImpl(new RandomHardwareFixTest(1));
        assertTrue(((NeedleRandomImpl) needle).isHardwareWorking());
    }

    @Test
    public void testIsHardwareNotWorking()
    {
        //using 0 as a 'random' number: the HW test will fail
        NeedleAssembly needle = new NeedleRandomImpl(new RandomHardwareFixTest(0));
        assertFalse(((NeedleRandomImpl) needle).isHardwareWorking());
    }
}
