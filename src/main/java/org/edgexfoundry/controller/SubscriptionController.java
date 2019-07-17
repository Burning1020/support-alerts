package org.edgexfoundry.controller;

import org.edgexfoundry.domain.notification.Subscription;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface SubscriptionController {

    ResponseEntity<String> createSubscription(@RequestBody Subscription subscription);

    boolean updateSubscription(@RequestBody Subscription subscription);

    List<Subscription> listAll();

    Subscription findByReceiver(@PathVariable String receiver);

    boolean deleteByReceiver(@PathVariable String receiver);

}
