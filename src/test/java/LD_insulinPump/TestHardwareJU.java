package LD_insulinPump;
import LD_InsulinPump.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestHardwareJU {


    @Test
    public void TestAllSystemJU() throws HardwareIssueException {
        TestHardware testHardware = new TestHardware();
        testHardware.setTestAlwaysFails(true);
        assertThrows(HardwareIssueException.class, () -> {
            testHardware.testAllSystem();
        });
    }

    @Test
    public void TestPumpJU() throws HardwareIssueException {
        TestHardware testHardware = new TestHardware();
        testHardware.setTestAlwaysFails(true);
        assertThrows(HardwareIssueException.class, () -> {
            testHardware.testPump(new Pump());
        });
    }

    @Test
    public void TestNeedleJU() throws HardwareIssueException {
        TestHardware testHardware = new TestHardware();
        testHardware.setTestAlwaysFails(true);
        assertThrows(HardwareIssueException.class, () -> {
            testHardware.testNeedle(new NeedleAssembly());
        });
    }

    @Test
    public void TestSensorJU() throws HardwareIssueException {
        TestHardware testHardware = new TestHardware();
        testHardware.setTestAlwaysFails(true);
        assertThrows(HardwareIssueException.class, () -> {
            testHardware.testSensor(new Sensor());
        });
    }
}
