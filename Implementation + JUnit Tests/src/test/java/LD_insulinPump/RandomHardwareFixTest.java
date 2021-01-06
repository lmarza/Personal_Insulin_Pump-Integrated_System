package LD_insulinPump;

import java.util.Random;

public class RandomHardwareFixTest extends Random {

    private int randomNumTest;

    public RandomHardwareFixTest(int randomNumTest)
    {
        this.randomNumTest = randomNumTest;
    }

    @Override
    public int nextInt(int bound) {
        return randomNumTest;
    }
}
