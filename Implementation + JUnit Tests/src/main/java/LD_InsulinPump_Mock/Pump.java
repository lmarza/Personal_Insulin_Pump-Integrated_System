package LD_InsulinPump_Mock;

import LD_InsulinPump.HardwareIssueException;

public interface Pump {
    void collectInsulin(Integer insulinToCollect) throws HardwareIssueException;
    boolean equals(Object o);
}
