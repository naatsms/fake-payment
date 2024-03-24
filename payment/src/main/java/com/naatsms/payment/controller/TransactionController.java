package com.naatsms.payment.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController
{
    private static final Logger LOG = LoggerFactory.getLogger(TransactionController.class);

    @PostMapping("/transaction")
    public HttpStatus createTransaction(String transactionId) {
        LOG.info("{} is created", transactionId);
        return HttpStatus.CREATED;
    }

}
