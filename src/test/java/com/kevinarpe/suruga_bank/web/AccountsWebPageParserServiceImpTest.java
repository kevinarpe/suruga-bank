package com.kevinarpe.suruga_bank.web;

import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.JerichoHtmlParserServiceImp;
import com.kevinarpe.suruga_bank.AccountName;
import com.kevinarpe.suruga_bank.TestGlobals;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public class AccountsWebPageParserServiceImpTest {

    private AccountsWebPageParserServiceImp classUnderTest;

    @BeforeMethod
    public void beforeEachTestMethod() {

        classUnderTest =
            new AccountsWebPageParserServiceImp(
                new JerichoHtmlParserServiceImp(TestGlobals.exceptionThrower),
                TestGlobals.loggerService,
                TestGlobals.exceptionThrower);
    }

    @Test
    public void parse_PassWhenSavingsAccountsAllPositive()
    throws Exception {

        final Path path =
            Path.of("test-data", "html-validation", "04-accounts.html");

        final String html = Files.readString(path);

        final AccountsWebPageParserService.Result result = classUnderTest.parse(html);

        for (final AccountName accountName : AccountName.values()) {

            final AccountsWebPageParserService.Result.Account account = result.accountMap.get(accountName);
            switch (accountName) {

                case REGULAR_SAVINGS:
                    // Negative balance
                    Assert.assertEquals(account.signedBalance, 2_817_082L);
                    break;

                case TIME_DEPOSIT_SAVINGS:
                    Assert.assertEquals(account.signedBalance, 0L);
                    break;

                case ACCUM_TIME_DEPOSIT_SAVINGS:
                    Assert.assertEquals(account.signedBalance, 1_171_239L);
                    break;

                case SAVINGS_SAVINGS:
                    Assert.assertEquals(account.signedBalance, 3_005_150L);
                    break;

                case MORTGAGE_LOAN:
                    Assert.assertEquals(account.signedBalance, 184_448_439L);
                    break;

                case CARD_LOAN:
                    Assert.assertEquals(account.signedBalance, 0L);
                    break;

                default: {
                    TestGlobals.exceptionThrower.throwCheckedException(Exception.class,
                        "Unknown %s: %s", accountName.getClass().getSimpleName(), accountName.name());
                }
            }
        }
    }

    @Test
    public void parse_PassWhenOneSavingsAccountsNegative()
    throws Exception {

        final Path path =
            Path.of("test-data", "html-validation", "negative-account-balance", "04-accounts.html");

        final String html = Files.readString(path);

        final AccountsWebPageParserService.Result result = classUnderTest.parse(html);

        for (final AccountName accountName : AccountName.values()) {

            final AccountsWebPageParserService.Result.Account account = result.accountMap.get(accountName);
            switch (accountName) {

                case REGULAR_SAVINGS:
                    // Negative balance
                    Assert.assertEquals(account.signedBalance, -349_918L);
                    break;

                case TIME_DEPOSIT_SAVINGS:
                    Assert.assertEquals(account.signedBalance, 0L);
                    break;

                case ACCUM_TIME_DEPOSIT_SAVINGS:
                    Assert.assertEquals(account.signedBalance, 1_171_239L);
                    break;

                case SAVINGS_SAVINGS:
                    Assert.assertEquals(account.signedBalance, 3_005_150L);
                    break;

                case MORTGAGE_LOAN:
                    Assert.assertEquals(account.signedBalance, 184_448_439L);
                    break;

                case CARD_LOAN:
                    Assert.assertEquals(account.signedBalance, 0L);
                    break;

                default: {
                    TestGlobals.exceptionThrower.throwCheckedException(Exception.class,
                        "Unknown %s: %s", accountName.getClass().getSimpleName(), accountName.name());
                }
            }
        }
    }
}
