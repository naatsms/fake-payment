package com.naatsms.payment.exception;

public class BusinessException extends RuntimeException
{
    public BusinessException(final String message)
    {
        super(message);
    }
}