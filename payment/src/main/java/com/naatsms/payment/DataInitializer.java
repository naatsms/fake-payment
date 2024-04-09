package com.naatsms.payment;

import com.naatsms.payment.entity.AccountBalance;
import com.naatsms.payment.entity.Merchant;
import com.naatsms.payment.repository.AccountRepository;
import com.naatsms.payment.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
public class DataInitializer implements ApplicationRunner
{

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void run(final ApplicationArguments args)
    {
            merchantRepository.findByName("name")
                    .switchIfEmpty(getMerchant())
                    .flatMap(merchant -> accountRepository.findByMerchantIdAndCurrencyIso(merchant.id(), "BRL")
                            .switchIfEmpty(accountRepository.save(getAccount(merchant))))
                    .subscribe();
    }

    private AccountBalance getAccount(final Merchant merchant)
    {
        return new AccountBalance(null, merchant.id(), "BRL", BigDecimal.valueOf(1000));
    }

    private Mono<Merchant> getMerchant()
    {
        Merchant merchant = new Merchant(null, "name", passwordEncoder.encode("secret"));
        return merchantRepository.save(merchant);
    }
}
