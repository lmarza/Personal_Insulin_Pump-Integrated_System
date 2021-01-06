package LD_InsulinPump_Mock;

import LD_InsulinPump.HardwareIssueException;

public interface Sensor {
    Float runMeasurement() throws HardwareIssueException;
    boolean equals(Object o);
}
