package com.naatsms.payment.exception;

public class AccountNotFoundException extends BusinessException
{
    public AccountNotFoundException(final String message)
    {
        super(message);
    }
}