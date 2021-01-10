package LD_InsulinPump_Mock;

import LD_InsulinPump.HardwareIssueException;

import java.util.Random;

public class PumpRandomImpl implements Pump, CheckHardware
{
    private Random randomCheckHWGenerator;
    private int bound = 1500;

    public PumpRandomImpl(Random randomCheckHWGenerator)
    {
        this.randomCheckHWGenerator = randomCheckHWGenerator;
    }

    @Override
    public void collectInsulin(Integer insulinToCollect) throws HardwareIssueException {
        if(!isHardwareWorking())
            throw new HardwareIssueException("Pump Hardware Issue");
    }

    @Override
    public boolean isHardwareWorking()
    {
        int randomNum = randomCheckHWGenerator.nextInt(bound);

        if (randomNum == 0 || (randomNum > 42 && randomNum < 53))
            return false;

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PumpRandomImpl that = (PumpRandomImpl) o;
        return bound == that.bound;
    }
}
