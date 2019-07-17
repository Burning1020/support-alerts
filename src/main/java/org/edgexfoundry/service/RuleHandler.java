package org.edgexfoundry.service;

import org.edgexfoundry.domain.Rule;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface RuleHandler {
    List<Rule> listAll();

    Rule findByName(@PathVariable String name);

    void addRule(@RequestBody Rule rule);

    void updateRule(@RequestBody Rule rule);

    void removeRule(@PathVariable String name);
}
