package org.edgexfoundry.controller.impl;

import java.util.List;

import org.edgexfoundry.controller.RuleEngineController;
import org.edgexfoundry.service.RuleCreator;
import org.edgexfoundry.service.RuleEngine;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.domain.rule.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rule")
public class RuleEngineControllerImpl implements RuleEngineController {

    private static final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
            org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
                    .getEdgeXLogger(RuleEngineControllerImpl.class);

    @Autowired
    @Qualifier("ruleEngine")
    private RuleEngine engine;

    @Autowired
    @Qualifier("ruleCreator")
    private RuleCreator creator;

    @RequestMapping(method = RequestMethod.GET)
    @Override
    public List<String> ruleNames() {
        try {
            return engine.getRulenames();
        } catch (Exception e) {
            logger.error("Problem getting rule names");
            throw new ServiceException(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    @Override
    public boolean addRule(@RequestBody Rule rule) {
        try {
            engine.addRule(creator.createDroolRule(rule), rule.getName());
            logger.info("Rule named:  " + rule.getName() + " added.");
            return true;
        } catch (Exception e) {
            logger.error("Problem adding a new rule called: " + rule.getName());
            throw new ServiceException(e);
        }
    }

    @RequestMapping(value = "/name/{rulename}", method = RequestMethod.DELETE)
    @Override
    public boolean removeRule(@PathVariable String rulename) {
        try {
            engine.removeRule(rulename);
            logger.info("Rule named:  " + rulename + " removed");
            return true;
        } catch (Exception e) {
            logger.error("Problem removing the rule called: " + rulename);
            throw new ServiceException(e);
        }
    }
}
