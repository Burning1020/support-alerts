package org.edgexfoundry.controller;

import java.util.List;

import org.edgexfoundry.domain.rule.Rule;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface RuleEngineController {

    List<String> ruleNames();

    boolean addRule(@RequestBody Rule rule);

    boolean removeRule(@PathVariable String rulename);
}
