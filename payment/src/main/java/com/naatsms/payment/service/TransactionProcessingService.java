package com.naatsms.payment.service;

public interface TransactionProcessingService
{

    void processTopUpTransactions();

    void processPayoutTransactions();

}