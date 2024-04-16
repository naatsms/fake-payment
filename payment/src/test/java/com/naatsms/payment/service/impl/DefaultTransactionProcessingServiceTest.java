package com.naatsms.payment.service.impl;

import com.naatsms.payment.TestPaymentApplication;
import com.naatsms.payment.entity.Account;
import com.naatsms.payment.entity.Card;
import com.naatsms.payment.entity.PaymentTransaction;
import com.naatsms.payment.enums.PaymentMethod;
import com.naatsms.payment.enums.TransactionStatus;
import com.naatsms.payment.enums.TransactionType;
import com.naatsms.payment.repository.AccountRepository;
import com.naatsms.payment.repository.CardRepository;
import com.naatsms.payment.repository.TransactionRepository;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.awaitility.Awaitility.await;

/** @noinspection SpringJavaInjectionPointsAutowiringInspection*/
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
@Testcontainers
@Import(TestPaymentApplication.class)
@ActiveProfiles("test")
class DefaultTransactionProcessingServiceTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    public static final Random RANDOM = new Random(1L);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://localhost:" + postgres.getFirstMappedPort() + "/test");
        registry.add("spring.flyway.url", () -> "jdbc:postgresql://localhost:" + postgres.getFirstMappedPort() + "/test");
    }

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void setUp() {
        flyway.migrate();
    }

    @AfterEach
    void tearDown() {
        flyway.clean();
    }

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private AccountRepository accountRepository;

    private final int transactionBatchSize = 20;

    @Test
    void test() {
        var a1 = getPaymentTransactions(1L, 1L, TransactionType.TRANSACTION ,"USD");
        var a2 = getPaymentTransactions(2L, 1L, TransactionType.TRANSACTION ,"EUR");
        var a3 = getPaymentTransactions(3L, 1L, TransactionType.TRANSACTION ,"GBP");
        var a4 = getPaymentTransactions(4L, 1L, TransactionType.TRANSACTION ,"USD");
        var a5 = getPaymentTransactions(5L, 1L, TransactionType.TRANSACTION ,"EUR");
        var a6 = getPaymentTransactions(6L, 1L, TransactionType.TRANSACTION ,"GBP");
        var a7 = getPaymentTransactions(7L, 1L, TransactionType.TRANSACTION ,"USD");
        var a8 = getPaymentTransactions(8L, 1L, TransactionType.TRANSACTION ,"EUR");
        var a9 = getPaymentTransactions(9L, 1L, TransactionType.TRANSACTION ,"GBP");
        var c1usd = getPaymentTransactions(1L, 1L, TransactionType.PAYOUT ,"USD");
        var c2usd = getPaymentTransactions(2L, 2L, TransactionType.PAYOUT ,"USD");
        var c3usd = getPaymentTransactions(3L, 3L, TransactionType.PAYOUT ,"USD");
        var c1eur = getPaymentTransactions(1L, 1L, TransactionType.PAYOUT ,"EUR");
        var c2eur = getPaymentTransactions(2L, 2L, TransactionType.PAYOUT ,"EUR");
        var c3eur = getPaymentTransactions(3L, 3L, TransactionType.PAYOUT ,"EUR");
        var c1gbp = getPaymentTransactions(1L, 1L, TransactionType.PAYOUT ,"GBP");
        var c2gbp = getPaymentTransactions(2L, 2L, TransactionType.PAYOUT ,"GBP");
        var c3gbp = getPaymentTransactions(3L, 3L, TransactionType.PAYOUT ,"GBP");
        var allTransactions = Stream.of(a1,a2,a3,a4,a5,a6,a7,a8,a9,c1usd,c1eur,c1gbp,c2gbp,c2usd,c2eur,c3gbp,c3eur,c3usd).flatMap(List::stream).toList();

        var expectedAccount_1_balance = getSum(a1) + 10000;
        var expectedAccount_2_balance = getSum(a2) + 8000;
        var expectedAccount_3_balance = getSum(a3) + 6000;
        var expectedAccount_4_balance = getSum(a4) + 5000;
        var expectedAccount_5_balance = getSum(a5) + 3000;
        var expectedAccount_6_balance = getSum(a6) + 3000;
        var expectedAccount_7_balance = getSum(a7) + 2000;
        var expectedAccount_8_balance = getSum(a8) + 4000;
        var expectedAccount_9_balance = getSum(a9) + 6000;
        var expectedCard_1_balance = getSum(c1usd, c1eur, c1gbp) + 1500;
        var expectedCard_2_balance = getSum(c2usd, c2eur, c2gbp) + 800;
        var expectedCard_3_balance = getSum(c3usd, c3eur, c3gbp) + 700;

        transactionRepository.saveAll(allTransactions).subscribe();

        verifyCardBalanceAsync(expectedCard_1_balance, "1234567890123456");
        verifyCardBalanceAsync(expectedCard_2_balance, "2345678901234567");
        verifyCardBalanceAsync(expectedCard_3_balance, "3456789012345678");

        verifyAccountBalanceAsync(expectedAccount_1_balance, 1L);
        verifyAccountBalanceAsync(expectedAccount_2_balance, 2L);
        verifyAccountBalanceAsync(expectedAccount_3_balance, 3L);
        verifyAccountBalanceAsync(expectedAccount_4_balance, 4L);
        verifyAccountBalanceAsync(expectedAccount_5_balance, 5L);
        verifyAccountBalanceAsync(expectedAccount_6_balance, 6L);
        verifyAccountBalanceAsync(expectedAccount_7_balance, 7L);
        verifyAccountBalanceAsync(expectedAccount_8_balance, 8L);
        verifyAccountBalanceAsync(expectedAccount_9_balance, 9L);
    }

    private int getSum(List<PaymentTransaction> paymentTransactions) {
        return paymentTransactions.stream().map(PaymentTransaction::getAmount).mapToInt(BigDecimal::intValue).sum();
    }

    private int getSum(List<PaymentTransaction>... list) {
        return Arrays.stream(list).flatMap(Collection::stream).map(PaymentTransaction::getAmount).mapToInt(BigDecimal::intValue).sum();
    }

    private void verifyAccountBalanceAsync(int expectedAccountBalance, long account) {
        await().atMost(Duration.ofSeconds(20))
                .until(() -> accountRepository.findById(account)
                            .map(Account::getAmount)
                            .block(),
                        value -> value.compareTo(BigDecimal.valueOf(expectedAccountBalance)) == 0);
    }

    private void verifyCardBalanceAsync(int expectedCardBalance, String cardNumber) {
        await().atMost(Duration.ofSeconds(20))
                .until(() -> cardRepository.findByCardNumber(cardNumber)
                                .map(Card::getAmount)
                                .block(),
                             value -> value.compareTo(BigDecimal.valueOf(expectedCardBalance)) == 0);
    }

    private List<PaymentTransaction> getPaymentTransactions(Long account, Long customer, TransactionType type, String currency) {
        return IntStream.rangeClosed(1, transactionBatchSize)
                .mapToObj(in -> this.getPaymentTransaction(account, customer, currency, type, RANDOM.nextInt(10))).collect(Collectors.toList());
    }

    private PaymentTransaction getPaymentTransaction(Long account, Long customer, String currency, TransactionType type, int amount) {
        return PaymentTransaction.builder()
                .accountId(account)
                .customerId(customer)
                .amount(BigDecimal.valueOf(amount))
                .currencyIso(currency)
                .status(TransactionStatus.IN_PROGRESS)
                .type(type)
                .languageIso("en")
                .notificationUrl("example.com")
                .paymentMethod(PaymentMethod.CARD)
                .build();
    }

}