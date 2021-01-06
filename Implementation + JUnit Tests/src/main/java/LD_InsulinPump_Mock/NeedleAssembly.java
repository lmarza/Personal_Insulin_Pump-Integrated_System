package LD_InsulinPump_Mock;

import LD_InsulinPump.HardwareIssueException;

public interface NeedleAssembly {
    void injectInsulin(Integer insulinToInject) throws HardwareIssueException;
    boolean equals(Object o);
}
