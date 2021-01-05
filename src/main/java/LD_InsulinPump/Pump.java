package LD_InsulinPump;

public interface Pump {
    void collectInsulin(Integer insulinToCollect) throws HardwareIssueException;
    boolean equals(Object o);
}
