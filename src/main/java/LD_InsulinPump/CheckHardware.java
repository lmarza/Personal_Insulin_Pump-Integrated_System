package LD_InsulinPump;

import java.util.Random;

// TODO: Replace with methods inside sensor, pump, etc. Maybe can be an interface
public interface CheckHardware {
    Random randomNumber = new Random();
    final static int bound = 1000;
    int randomNum = 0;

    boolean isHardwareWorking();
/*
    @Deprecated
    public void testAllSystem() throws HardwareIssueException {
        if(testAlwaysFails)
            throw new HardwareIssueException("Hardware Issue");

        randomNum = randomNumber.nextInt(bound);

        if (randomNum > 732 && randomNum < 743)
            throw new HardwareIssueException("Hardware Issue");
    }

    @Deprecated
    public void testNeedle(NeedleAssembly needleAssembly) throws HardwareIssueException {
        if(testAlwaysFails)
            throw new HardwareIssueException("Needle Hardware Issue");

        randomNum = randomNumber.nextInt(bound);

        if (randomNum > 101 && randomNum < 112)
            throw new HardwareIssueException("Needle Hardware Issue");
    }

    @Deprecated
    public void testPump(Pump pump) throws HardwareIssueException {
        if(testAlwaysFails)
            throw new HardwareIssueException("Pump Hardware Issue");

        randomNum = randomNumber.nextInt(bound);

        if (randomNum > 42 && randomNum < 53)
            throw new HardwareIssueException("Pump Hardware Issue");
    }
*/

}
