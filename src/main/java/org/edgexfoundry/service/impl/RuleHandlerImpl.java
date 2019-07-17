package org.edgexfoundry.service.impl;

import com.mongodb.MongoException;
import org.edgexfoundry.dao.RuleDAO;
import org.edgexfoundry.domain.Rule;
import org.edgexfoundry.exception.controller.ClientException;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.service.RuleCreator;
import org.edgexfoundry.service.RuleEngine;
import org.edgexfoundry.service.RuleHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ruleHandler")
public class RuleHandlerImpl implements RuleHandler {
    private static final String RECD_NULL = "RulenHandler received a null object";
    private final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
            org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());

    @Autowired
    private RuleDAO ruleDAO;

    @Override
    public List<Rule> listAll() {
        logger.debug("RuleHandler is listing all rules");
        return ruleDAO.findAll();
    }

    @Override
    public Rule findByName(String name) {
        logger.debug("RuleHandler is finding rule by name=" + name);
        return ruleDAO.findByNameIgnoreCase(name);
    }

    @Override
    public void addRule(Rule rule) {
        if (rule == null) {
            logger.error(RECD_NULL);
            throw new ClientException("Rule is null");
        }

        logger.debug("RuleHandler is creating a new rule: " + rule.toString());

        try {
            ruleDAO.insert(rule);
        } catch (DuplicateKeyException e) {
            logger.info(e.getMessage(), e);
            throw new DataValidationException("duplicated rule name: " + rule.getName());
        }

        logger.debug("new rule is created: name=" + rule.getName());
    }

    @Override
    public void updateRule(Rule rule) {
        if (rule == null) {
            logger.error(RECD_NULL);
            throw new ClientException("Rule is null");
        }

        Rule oldRule = ruleDAO.findByNameIgnoreCase(rule.getName());
        if (oldRule == null) {
            logger.info("the rule doesn't exist: name=" + rule.getName());
            throw new NotFoundException(Rule.class.toString(), rule.getName());
        }

        logger.debug(
                "RuleHandler is updating an existing rule: " + rule.toString());

        rule.setId(oldRule.getId());
        try {
            ruleDAO.save(rule);
        } catch (MongoException e) {
            logger.info("rule update operation was failed: name=" + rule.getName());
            logger.info(e.getMessage(), e);
            throw new ClientException(e.getMessage());
        }

        logger.debug("the rule is updated: name=" + rule.getName());
    }

    @Override
    public void removeRule(String name) {
        if (name == null) {
            logger.error(RECD_NULL);
            throw new ClientException("name is null");
        }

        Rule rule = ruleDAO.findByNameIgnoreCase(name);
        if (rule == null) {
            logger.info("the rule doesn't exist: name=" + name);
            throw new NotFoundException(Rule.class.toString(), name);
        }

        logger.debug(
                "RuleHandler is deleting an existing rule: " + rule.toString());

        try {
            ruleDAO.delete(rule);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e);
        }

        logger.debug("the rule is deleted: name=" + name);
    }
}
