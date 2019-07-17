package org.edgexfoundry.controller.impl;

import java.util.List;

import org.edgexfoundry.controller.RuleController;
import org.edgexfoundry.exception.controller.ClientException;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.domain.Rule;
import org.edgexfoundry.service.RuleCreator;
import org.edgexfoundry.service.RuleEngine;
import org.edgexfoundry.service.RuleHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rule")
public class RuleControllerImpl implements RuleController {

    private static final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
            org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
                    .getEdgeXLogger(RuleControllerImpl.class);
    
    @Autowired
    @Qualifier("ruleHandler")
    private RuleHandler handler;

    @Autowired
    private RuleEngine engine;

    @Autowired
    private RuleCreator creator;

    @RequestMapping(method = RequestMethod.GET)
    @Override
    public List<Rule> listAll() {
        List<Rule> result;
        try {
            result = handler.listAll();
        } catch (Exception e) {
            logger.error("Problem getting rules");
            throw new ServiceException(e);
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Override
    public boolean addRule(@RequestBody Rule rule) {
        try {
            checkNameIntegrity(rule.getName());
            handler.addRule(rule);
            engine.addDroolRule(creator.createDroolRule(rule));
            return true;
        } catch (DataValidationException | ClientException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e);
        }
    }

    private void checkNameIntegrity(String name) {
        if (name == null || name.isEmpty()) {
            String errMsg = "name is null or empty.";
            logger.info(errMsg);
            throw new ClientException(errMsg);
        }

        Rule s = handler.findByName(name);
        if (s != null) {
            String errMsg = "duplicated rule name: " + name;
            logger.info(errMsg);
            throw new DataValidationException(errMsg);
        }
    }

    @RequestMapping(value = "/name/{name}", method = RequestMethod.DELETE)
    @Override
    public boolean removeRule(@PathVariable String name) {
        try {
            handler.removeRule(name);
            engine.removeDroolRule(name);
            return true;
        } catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e);
        }
    }

    @RequestMapping(value = "/name/{name}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Rule findByName(@PathVariable String name) {
        Rule result;
        try {
            result = handler.findByName(name);
        } catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e);
        }
        if (result == null) {
            throw new NotFoundException(Rule.class.toString(), name);
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public boolean updateRule(@RequestBody Rule rule) {
        try {
            handler.updateRule(rule);
            engine.removeDroolRule(rule.getName());
            engine.addDroolRule(creator.createDroolRule(rule));
            return true;
        } catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e);
        }
    }
}
