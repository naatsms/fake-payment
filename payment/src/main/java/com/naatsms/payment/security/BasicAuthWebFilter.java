package com.naatsms.payment.security;

import com.naatsms.payment.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.naatsms.payment.constants.SessionKeys.MERCHANT_ID;

@Component
@Qualifier("basicAuthWebFilter")
public class BasicAuthWebFilter implements WebFilter
{
    public static final String BASIC = "Basic ";
    public static final String DELIMITER = ":";
    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;

    public BasicAuthWebFilter(final MerchantRepository merchantRepository, final PasswordEncoder passwordEncoder)
    {
        this.merchantRepository = merchantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain)
    {
        String[] credentials = getCredentials(exchange);
        return merchantRepository.findByName(credentials[0])
                .filter(merchant -> passwordEncoder.matches(credentials[1], merchant.secret()))
                .switchIfEmpty(Mono.error(new BadCredentialsException("Bad credentials")))
                .doOnNext(merchant -> exchange.getAttributes().put(MERCHANT_ID, merchant.id()))
                .flatMap(f -> chain.filter(exchange));
    }

    private String[] getCredentials(final ServerWebExchange exchange)
    {
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            throw new BadCredentialsException("No authentication provided");
        }
        header = header.trim();
        if (!StringUtils.startsWithIgnoreCase(header, BASIC)) {
            throw new BadCredentialsException("Invalid authentication format");
        }
        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded = Base64.getDecoder().decode(base64Token);
        String token = new String(decoded);
        String[] credentials = token.split(DELIMITER);
        if (credentials.length != 2) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return credentials;
    }

}
