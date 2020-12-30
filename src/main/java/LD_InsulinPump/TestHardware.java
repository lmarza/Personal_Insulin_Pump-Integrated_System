package LD_InsulinPump;

import java.util.Random;

public class TestHardware{
    Random randomNumber = new Random();
    final static int bound = 2000;
    int randomNum = 0;
    boolean test = false;

    public void setRandomNum(int randomNum) {
        this.randomNum = randomNum;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public void testAllSystem() throws HardwareIssueException {
        if (!test)
          randomNum = randomNumber.nextInt(bound);

        if (randomNum > 732 && randomNum < 743)
            throw new HardwareIssueException("Hardware Issue");
    }

    public void testNeedle(NeedleAssembly needleAssembly) throws HardwareIssueException {
        if (!test)
            randomNum = randomNumber.nextInt(bound);

        if (randomNum > 101 && randomNum < 112)
            throw new HardwareIssueException("Needle Hardware Issue");
    }

    public void testPump(Pump pump) throws HardwareIssueException {
        if (!test)
            randomNum = randomNumber.nextInt(bound);
        if (randomNum > 42 && randomNum < 53)
            throw new HardwareIssueException("Pump Hardware Issue");
    }

    public void testSensor(Sensor sensor) throws HardwareIssueException {
        if (!test)
            randomNum = randomNumber.nextInt(bound);
        if (randomNum > 876 && randomNum < 890)
            throw new HardwareIssueException("Sensor Hardware Issue");
    }
}
