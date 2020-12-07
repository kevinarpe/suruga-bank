package com.kevinarpe.suruga_bank.args;

import com.beust.jcommander.Parameter;
import com.googlecode.kevinarpe.papaya.argument.IntArgs;
import com.googlecode.kevinarpe.papaya.argument.StringArgs;
import com.googlecode.kevinarpe.papaya.java_mail.AlwaysTrustSSL;
import com.googlecode.kevinarpe.papaya.java_mail.EmailMessageAddress;
import com.googlecode.kevinarpe.papaya.web.chrome_dev_tools.IsHeadless;

import javax.annotation.Nullable;
import java.io.File;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public final class SaveDataMainArgs
extends AbstractArgs {

    private static final String CHROME_HEADLESS = "--chrome-headless";
    private static final String DATA_DIR = "--data-dir";
    private static final String BRANCH_NUMBER = "--branch-number";
    private static final String ACCOUNT_NUMBER = "--account-number";
    private static final String CASH_CARD_PASSWORD_NUMBER = "--cash-card-password-number";
    private static final String ACCOUNT_HOLDER_NAME = "--account-holder-name";
    private static final String SMTP_HOST = "--smtp-host";
    private static final String SMTP_PORT = "--smtp-port";
    private static final String EMAIL_ADDRESS = "--email-address";
    private static final String SMTP_USERNAME = "--smtp-username";
    private static final String SMTP_PASSWORD = "--smtp-password";

    public SaveDataMainArgs() {
        // Empty
    }

    @Parameter(
        names = CHROME_HEADLESS,
        required = false,
        description = "Display help"
    )
    private boolean isChromeHeadless = false;

    public IsHeadless isChromeHeadless() {
        return isChromeHeadless ? IsHeadless.YES : IsHeadless.NO;
    }

    @Parameter(names = DATA_DIR, required = true)
    private String dataDirPathname;

    public File dataDirPath() {

        final File x = new File(dataDirPathname);
        return x;
    }

    @Parameter(names = BRANCH_NUMBER, required = true)
    private String branchNumber;

    public String branchNumber() {
        return branchNumber;
    }

    @Parameter(names = ACCOUNT_NUMBER, required = true)
    private String accountNumber;

    public String accountNumber() {
        return accountNumber;
    }

    @Parameter(names = CASH_CARD_PASSWORD_NUMBER, required = true)
    private String cashCardPasswordNumber;

    public String cashCardPasswordNumber() {
        return cashCardPasswordNumber;
    }

    @Parameter(names = ACCOUNT_HOLDER_NAME, required = true)
    private String accountHolderName;

    public String accountHolderName() {
        return accountHolderName;
    }

    @Parameter(
        names = SMTP_HOST,
        required = true,
        description = "Example: smtp.gmail.com"
    )
    private String smtpHost;

    public String smtpHost() {
        return smtpHost;
    }

    @Parameter(
        names = SMTP_PORT,
        required = true,
        description = "Example: 587 (modern secure)"
    )
    private int smtpPort;

    public int smtpPort() {
        return smtpPort;
    }

    @Parameter(
        names = "--smtp-host-always-trust-ssl",
        required = false
    )
    private boolean smtpAlwaysTrustSsl = false;

    public AlwaysTrustSSL smtpAlwaysTrustSsl() {
        return smtpAlwaysTrustSsl ? AlwaysTrustSSL.YES : AlwaysTrustSSL.NO;
    }

    @Parameter(
        names = EMAIL_ADDRESS,
        required = true,
        description = "Example: kevinarpe@gmail.com"
    )
    private String emailAddress;

    public EmailMessageAddress emailAddress() {
        final EmailMessageAddress x = EmailMessageAddress.fromEmailAddressOnly(emailAddress);
        return x;
    }

    @Nullable
    @Parameter(
        names = SMTP_USERNAME,
        required = false,
        description = "Optional: SMTP username for authentication; Example: kevinarpe@gmail.com"
    )
    private String nullableSmtpUsername = null;

    @Nullable
    public String nullableSmtpUsername() {
        return nullableSmtpUsername;
    }

    @Nullable
    @Parameter(
        names = SMTP_PASSWORD,
        required = false,
        description = "Optional: SMTP password for authentication; Example: password123"
    )
    private String nullableSmtpPassword = null;

    @Nullable
    public String nullableSmtpPassword() {
        return nullableSmtpPassword;
    }

    @Override
    public void
    validate() {

        StringArgs.checkNotEmptyOrWhitespace(dataDirPathname, DATA_DIR);
        StringArgs.checkNotEmptyOrWhitespace(branchNumber, BRANCH_NUMBER);
        StringArgs.checkNotEmptyOrWhitespace(accountNumber, ACCOUNT_NUMBER);
        StringArgs.checkNotEmptyOrWhitespace(cashCardPasswordNumber, CASH_CARD_PASSWORD_NUMBER);
        StringArgs.checkNotEmptyOrWhitespace(accountHolderName, ACCOUNT_HOLDER_NAME);
        StringArgs.checkNotEmptyOrWhitespace(smtpHost, SMTP_HOST);
        IntArgs.checkMinValue(smtpPort, 1, SMTP_PORT);
        if ((null == nullableSmtpUsername) != (null == nullableSmtpPassword)) {

            throw new IllegalArgumentException(SMTP_USERNAME + " must be paired with " + SMTP_PASSWORD);
        }
        if (null != nullableSmtpUsername && null != nullableSmtpPassword) {

            StringArgs.checkNotEmptyOrWhitespace(nullableSmtpUsername, SMTP_USERNAME);
            StringArgs.checkNotEmptyOrWhitespace(nullableSmtpPassword, SMTP_PASSWORD);
        }
    }
}
