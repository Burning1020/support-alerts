package org.edgexfoundry.domain.rule;

import java.util.ArrayList;
import java.util.List;

public class Condition {

    private String device;
    private List<ValueCheck> checks;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public List<ValueCheck> getChecks() {
        return checks;
    }

    public void setChecks(List<ValueCheck> checks) {
        this.checks = checks;
    }

    public void addCheck(ValueCheck check) {
        if (checks == null)
            checks = new ArrayList<>();
        checks.add(check);
    }

}
