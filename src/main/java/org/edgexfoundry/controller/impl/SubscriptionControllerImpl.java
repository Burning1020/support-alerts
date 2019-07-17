package org.edgexfoundry.controller.impl;

import org.edgexfoundry.controller.SubscriptionController;
import org.edgexfoundry.exception.controller.ClientException;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.domain.notification.Subscription;
import org.edgexfoundry.service.SubscriptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscription")
public class SubscriptionControllerImpl implements SubscriptionController {

    private final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
            org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());

    @Autowired
    private SubscriptionHandler subscriptionHandler;

    @Override
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createSubscription(@RequestBody Subscription subscription) {
        try {
            checkReceiverIntegrity(subscription.getReceiver());
            subscriptionHandler.createSubscription(subscription);
            return new ResponseEntity<>(subscription.getReceiver(), HttpStatus.CREATED);
        } catch (DataValidationException | ClientException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e);
        }
    }

    private void checkReceiverIntegrity(String receiver) {
        if (receiver == null || receiver.isEmpty()) {
            String errMsg = "receiver is null or empty.";
            logger.info(errMsg);
            throw new ClientException(errMsg);
        }

        Subscription s = subscriptionHandler.findByReceiver(receiver);
        if (s != null) {
            String errMsg = "duplicated subscription receiver: " + receiver;
            logger.info(errMsg);
            throw new DataValidationException(errMsg);
        }
    }

    @Override
    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean updateSubscription(@RequestBody Subscription subscription) {
        try {
            subscriptionHandler.updateSubscription(subscription);
            return true;
        } catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e);
        }
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Subscription> listAll() {
        List<Subscription> result;
        try {
            result = subscriptionHandler.listAll();
        } catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e);
        }
        return result;
    }

    @Override
    @RequestMapping(value = "/receiver/{receiver:.+}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Subscription findByReceiver(@PathVariable String receiver) {
        Subscription result;
        try {
            result = subscriptionHandler.findByReceiver(receiver);
        } catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e);
        }
        if (result == null) {
            throw new NotFoundException(Subscription.class.toString(), receiver);
        }
        return result;
    }

    @Override
    @RequestMapping(value = "/receiver/{receiver:.+}", method = RequestMethod.DELETE)
    public boolean deleteByReceiver(@PathVariable String receiver) {
        try {
            subscriptionHandler.deleteByReceiver(receiver);
            return true;
        } catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e);
        }
    }
}
