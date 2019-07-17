package org.edgexfoundry.service;

import org.edgexfoundry.domain.Rule;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface RuleEngine {

    void init();

    void execute(Object object);

    List<String> getRuleNames();

    void addDroolRule(String newRule);

    void removeDroolRule(String rulename);

}
