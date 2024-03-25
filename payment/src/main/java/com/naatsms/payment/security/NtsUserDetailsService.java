package com.naatsms.payment.security;

import com.naatsms.payment.entity.Merchant;
import com.naatsms.payment.repository.MerchantRepository;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class NtsUserDetailsService implements ReactiveUserDetailsService
{
    private final MerchantRepository merchantRepository;

    public NtsUserDetailsService(final MerchantRepository merchantRepository)
    {
        this.merchantRepository = merchantRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(final String username)
    {
        return merchantRepository.findByName(username)
                .map(this::toUserDetails);
    }

    private UserDetails toUserDetails(final Merchant merchant)
    {
        return new User(merchant.getName(), merchant.getSecretKey(), AuthorityUtils.createAuthorityList("USER"));
    }
}
