package org.edgexfoundry.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import com.mongodb.MongoException;
import org.edgexfoundry.dao.RuleDAO;
import org.edgexfoundry.domain.Rule;
import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.exception.controller.ClientException;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.service.DistributionCoordinator;
import org.edgexfoundry.service.RuleCreator;
import org.edgexfoundry.service.RuleEngine;
import org.edgexfoundry.service.RuleHandler;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
//import org.kie.api.definition.rule.Rule;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service("ruleEngine")
public class RuleEngineImpl implements RuleEngine {

    private static final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
            org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(RuleEngineImpl.class);

    @Value("${rules.packagename}")
    private String packageName;

    @Autowired
    private RuleCreator creator;

    @Autowired
    private RuleHandler handler;

    @Autowired
    private DistributionCoordinator coordinator;

    private KieHelper kieHelper;
    private KieBase kbase;



    @Override
    @PostConstruct
    public void init() {
        logger.debug("initializing Drools Kies");
        kieHelper = new KieHelper();
        // upload rule from database
        List<Rule> rules = handler.listAll();
        for (Rule rule : rules) {
            kieHelper.addContent(creator.createDroolRule(rule), ResourceType.DRL);
        }
    }

    @Override
    public void execute(Object object) {
        try {
            kbase = kieHelper.build();
            KieSession ksession = kbase.newKieSession();
            if (!getRuleNames().isEmpty()) {
                ksession.setGlobal("logger", logger);
                ksession.setGlobal("coordinator", coordinator);
                ksession.insert(object);
                int rulesFired = ksession.fireAllRules();
                logger.debug("Number of rules fired on event: " + rulesFired);
                if (rulesFired > 0) {
                    logger.info("Event triggered " + rulesFired + "rules: " + (Event) object);
                }

            } else {
                logger.debug("No rules in the system - skipping firing rules");
            }
            ksession.dispose();
        } catch (Exception e) {
            logger.error("Error during rules enging processing:  " + e.getMessage());
        }
    }

    @Override
    public void addDroolRule(String newRule) {
        try {
            kieHelper.addContent(newRule, ResourceType.DRL);
            kbase = kieHelper.build();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<String> getRuleNames() {
        List<String> result = new ArrayList<>();
        try {
            KiePackage kpkg = kbase.getKiePackage(packageName);
            if (kpkg != null) {
                Collection<org.kie.api.definition.rule.Rule> rules = kpkg.getRules();
                if (rules != null) {
                    for (org.kie.api.definition.rule.Rule rule : rules) {
                        result.add(rule.getName());
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ServiceException(ex);
        }
    }

    @Override
    public void removeDroolRule(String rulename) {
        if (kbase.getRule(packageName, rulename) != null)
            kbase.removeRule(packageName, rulename);
        logger.info("rule: " + rulename + " removed.");
    }

    public String getPackageName() {
        return packageName;
    }

}
