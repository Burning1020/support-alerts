package org.edgexfoundry.service;

import java.util.List;

public interface RuleEngine {

    public void init();

    public void execute(Object object);

    public void addRule(String newRule, String rulename);

    public boolean removeRule(String rulename);

    public List<String> getRulenames();

}
