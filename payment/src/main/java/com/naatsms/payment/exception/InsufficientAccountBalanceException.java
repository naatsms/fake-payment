package com.naatsms.payment.exception;

public class InsufficientAccountBalanceException extends BusinessException
{
    public InsufficientAccountBalanceException(final String message)
    {
        super(message);
    }
}
