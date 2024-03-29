package LD_InsulinPump_Mock;

import LD_InsulinPump.HardwareIssueException;

import java.util.Random;

public class SensorRandomImpl implements Sensor, CheckHardware {
    private Random randomMeasurementGenerator;
    private Random randomCheckHWGenerator;
    private int bound = 1500;
    
    final static Float maxBloodSugarValue = 35f;

    /**
     *
     * @param randomMeasurementGenerator measurement random
     * @param randomCheckHWGenerator hardware random
     */
    public SensorRandomImpl(Random randomMeasurementGenerator, Random randomCheckHWGenerator) {
        this.randomMeasurementGenerator = randomMeasurementGenerator;
        this.randomCheckHWGenerator = randomCheckHWGenerator;
    }

    @Override
    public Float runMeasurement() throws HardwareIssueException
    {
        if(!isHardwareWorking())
            throw new HardwareIssueException("Sensor Hardware Issue");

        return 1 + randomMeasurementGenerator.nextFloat() * (maxBloodSugarValue - 1);
    }

    @Override
    public boolean isHardwareWorking()
    {
        int randomNum = randomCheckHWGenerator.nextInt(bound);

        if (randomNum == 0 || (randomNum > 876 && randomNum < 890))
            return false;

        return true;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorRandomImpl that = (SensorRandomImpl) o;
        return bound == that.bound;
    }
}
