package LD_InsulinPump;

public interface Pump {
    public void collectInsulin(Integer insulinToCollect) throws HardwareIssueException;
}
