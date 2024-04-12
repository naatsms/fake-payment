package com.naatsms.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)
@Slf4j
@Profile("test")
public class DataInitializer implements ApplicationRunner
{

    @Override
    public void run(final ApplicationArguments args)
    {
        //For future use
    }

}