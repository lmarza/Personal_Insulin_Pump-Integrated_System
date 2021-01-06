package LD_InsulinPump_Mock;

import LD_InsulinPump.HardwareIssueException;

import java.util.Random;

public class NeedleRandomImpl implements NeedleAssembly, CheckHardware {
    private Random randomCheckHWGenerator;
    private int bound = 1000;

    public NeedleRandomImpl(Random randomCheckHWGenerator)
    {
        this.randomCheckHWGenerator = randomCheckHWGenerator;
    }

    @Override
    public void injectInsulin(Integer insulinToInject) throws HardwareIssueException {
        if(!isHardwareWorking())
            throw new HardwareIssueException("Needle Hardware Issue");
    }

    @Override
    public boolean isHardwareWorking() {
        int randomNum = randomCheckHWGenerator.nextInt(bound);

        if (randomNum == 0 || (randomNum > 101 && randomNum < 112))
            return false;

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NeedleRandomImpl that = (NeedleRandomImpl) o;
        return bound == that.bound;
    }
}
