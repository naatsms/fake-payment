package com.naatsms.payment.exception;

public class InsufficientCardBalanceException extends BusinessException
{
    public InsufficientCardBalanceException(final String message)
    {
        super(message);
    }
}