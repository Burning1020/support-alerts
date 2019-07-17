package org.edgexfoundry.controller;

import java.util.List;

import org.edgexfoundry.domain.Rule;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface RuleController {

    List<Rule> listAll();

    Rule findByName(@PathVariable String name);

    boolean addRule(@RequestBody Rule rule);

    boolean updateRule(@RequestBody Rule rule);

    boolean removeRule(@PathVariable String name);
}
