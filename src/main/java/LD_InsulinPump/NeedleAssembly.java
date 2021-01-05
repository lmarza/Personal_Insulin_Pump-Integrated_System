package LD_InsulinPump;

public interface NeedleAssembly {
    void injectInsulin(Integer insulinToInject) throws HardwareIssueException;
    boolean equals(Object o);
}
