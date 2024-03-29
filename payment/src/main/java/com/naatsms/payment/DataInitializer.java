package com.naatsms.payment;

import com.naatsms.payment.entity.Merchant;
import com.naatsms.payment.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner
{

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MerchantRepository merchantRepository;

    @Override
    public void run(final ApplicationArguments args)
    {
        try
        {
            Merchant merchant = new Merchant(null, "name", passwordEncoder.encode("secret"));
            merchantRepository.save(merchant).subscribe();
        } catch (Exception e) {
            // ignore
        }
    }
}
