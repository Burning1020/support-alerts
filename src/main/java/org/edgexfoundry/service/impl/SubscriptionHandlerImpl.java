/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * @microservice: support-notifications
 * @author: Cloud Tsai, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.service.impl;

import com.mongodb.MongoException;

import org.edgexfoundry.dao.SubscriptionDAO;
import org.edgexfoundry.domain.notification.Subscription;
import org.edgexfoundry.exception.controller.ClientException;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.service.SubscriptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionHandlerImpl implements SubscriptionHandler {

    private static final String RECD_NULL = "SubscriptionHandler received a null object";
    private final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
            org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());

    @Autowired
    private SubscriptionDAO subscriptionDAO;

    @Override
    public void createSubscription(Subscription subscription) {
        if (subscription == null) {
            logger.error(RECD_NULL);
            throw new ClientException("Subscription is null");
        }

        logger.debug("SubscriptionHandler is creating a new subscription: " + subscription.toString());

        try {
            subscription = subscriptionDAO.insert(subscription);
        } catch (DuplicateKeyException e) {
            logger.info(e.getMessage(), e);
            throw new DataValidationException("duplicated subscription receiver: " + subscription.getReceiver());
        }

        logger.debug("new subscription is created: receiver=" + subscription.getReceiver());
    }

    @Override
    public List<Subscription> listAll() {
        logger.debug("SubscriptionHandler is listing all subscriptions");
        return subscriptionDAO.findAll();
    }

    @Override
    public void updateSubscription(Subscription subscription) {
        if (subscription == null) {
            logger.error(RECD_NULL);
            throw new ClientException("Subscription is null");
        }

        Subscription oldSubscription = subscriptionDAO.findByReceiverIgnoreCase(subscription.getReceiver());
        if (oldSubscription == null) {
            logger.info("the subscription doesn't exist: receiver=" + subscription.getReceiver());
            throw new NotFoundException(Subscription.class.toString(), subscription.getReceiver());
        }

        logger.debug(
                "SubscriptionHandler is updating an existing subscription: " + subscription.toString());

        subscription.setId(oldSubscription.getId());
        try {
            subscriptionDAO.save(subscription);
        } catch (MongoException e) {
            logger.info("subscription update operation was failed: receiver=" + subscription.getReceiver());
            logger.info(e.getMessage(), e);
            throw new ClientException(e.getMessage());
        }

        logger.debug("the subscription is updated: receiver=" + subscription.getReceiver());
    }

    @Override
    public Subscription findByReceiver(String receiver) {
        logger.debug("SubscriptionHandler is finding subscription by receiver=" + receiver);
        return subscriptionDAO.findByReceiverIgnoreCase(receiver);
    }

    @Override
    public void deleteByReceiver(String receiver) {
        if (receiver == null) {
            logger.error(RECD_NULL);
            throw new ClientException("receiver is null");
        }

        Subscription subscription = subscriptionDAO.findByReceiverIgnoreCase(receiver);
        if (subscription == null) {
            logger.info("the subscription doesn't exist: receiver=" + receiver);
            throw new NotFoundException(Subscription.class.toString(), receiver);
        }

        logger.debug(
                "SubscriptionHandler is deleting an existing subscription: " + subscription.toString());

        try {
            subscriptionDAO.delete(subscription);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e);
        }
        logger.debug("the subscription is deleted: reveiver=" + receiver);
    }

}
