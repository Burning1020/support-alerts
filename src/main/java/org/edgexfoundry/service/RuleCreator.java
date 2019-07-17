package org.edgexfoundry.service;

import org.edgexfoundry.domain.Rule;

public interface RuleCreator {

    public void init();

    public String createDroolRule(Rule rule);

}
