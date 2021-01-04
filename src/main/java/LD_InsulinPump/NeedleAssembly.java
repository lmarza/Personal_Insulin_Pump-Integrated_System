package LD_InsulinPump;

public interface NeedleAssembly {
    public void injectInsulin(Integer insulinToInject) throws HardwareIssueException;
}
