package com.kevinarpe.suruga_bank;

import com.google.common.collect.ImmutableList;
import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.exception.ExceptionThrower;
import com.googlecode.kevinarpe.papaya.exception.ExceptionThrowerImpl;
import com.googlecode.kevinarpe.papaya.exception.ThrowableToStringServiceFactory;
import com.googlecode.kevinarpe.papaya.function.retry.BasicRetryStrategyImp;
import com.googlecode.kevinarpe.papaya.function.retry.CollectionIndexMatcher;
import com.googlecode.kevinarpe.papaya.function.retry.RetryService;
import com.googlecode.kevinarpe.papaya.function.retry.RetryServiceImp;
import com.googlecode.kevinarpe.papaya.function.retry.RetryStrategyFactory;
import com.googlecode.kevinarpe.papaya.logging.slf4j.LoggerLevel;
import com.googlecode.kevinarpe.papaya.logging.slf4j.LoggerService;
import com.googlecode.kevinarpe.papaya.logging.slf4j.LoggerServiceImpl;
import com.googlecode.kevinarpe.papaya.string.MessageFormatter;
import com.googlecode.kevinarpe.papaya.string.MessageFormatterImpl;
import com.googlecode.kevinarpe.papaya.web.chrome_dev_tools.ChromeDevToolsAppContext;
import com.googlecode.kevinarpe.papaya.web.chrome_dev_tools.ChromeLauncherService;
import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.JerichoHtmlParserService;
import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.JerichoHtmlParserServiceImp;
import com.kevinarpe.suruga_bank.db.DSLConnection;
import com.kevinarpe.suruga_bank.db.DSLContextServiceImp;
import com.kevinarpe.suruga_bank.db.DbTables;
import com.kevinarpe.suruga_bank.db.SqliteConnectionService;
import com.kevinarpe.suruga_bank.db.SqliteConnectionServiceImp;
import com.kevinarpe.suruga_bank.db.SqliteDslConnectionService;
import com.kevinarpe.suruga_bank.db.SqliteDslConnectionServiceImp;
import com.kevinarpe.suruga_bank.web.AccountsDbService;
import com.kevinarpe.suruga_bank.web.AccountsDbServiceImp;
import com.kevinarpe.suruga_bank.web.AccountsWebPageParserService;
import com.kevinarpe.suruga_bank.web.AccountsWebPageParserServiceImp;
import com.kevinarpe.suruga_bank.web.SurugaBankWebPageService;
import com.kevinarpe.suruga_bank.web.SurugaBankWebPageServiceImp;
import com.kevinarpe.suruga_bank.web.WebPageHtmlFileWriter;
import com.kevinarpe.suruga_bank.web.WebPageHtmlFileWriterImp;
import com.kevinarpe.suruga_bank.web.WebPageValidatorGroup;
import com.kevinarpe.suruga_bank.web.WebPageValidatorGroupImp;
import com.kevinarpe.suruga_bank.web.WebPageValidatorImp_Accounts;
import com.kevinarpe.suruga_bank.web.WebPageValidatorImp_AfterLogin;
import com.kevinarpe.suruga_bank.web.WebPageValidatorImp_Login;
import com.kevinarpe.suruga_bank.web.WebPageValidatorImp_Logout;
import com.kevinarpe.suruga_bank.web.WebPageValidatorImp_Welcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
// Scope: Global singleton
public final class AppContextImp
implements AppContext {

    public static final String DATABASE_FILE_NAME = "suruga-bank.sqlite";
    private static final Logger logger = LoggerFactory.getLogger(AppContextImp.class);

    public final File dataDirPath;
    public final File databaseFilePath;
    private final ChromeLauncherService chromeLauncherService;
    private final RetryStrategyFactory retryStrategyFactory;
    private final LoggerService loggerService;
    private final WebPageValidatorGroup webPageValidatorGroup;
    private final AccountsWebPageParserService accountsWebPageParserService;
    private final SurugaBankWebPageService surugaBankWebPageService;
    private final WebPageHtmlFileWriter webPageHtmlFileWriter;
    private final AccountsDbService accountsDbService;

    @Override
    public ChromeLauncherService
    getChromeLauncherService() {
        return chromeLauncherService;
    }

    @Override
    public RetryStrategyFactory
    getRetryStrategyFactory() {
        return retryStrategyFactory;
    }

    @Override
    public LoggerService
    getLoggerService() {
        return loggerService;
    }

    @Override
    public WebPageValidatorGroup
    getWebPageValidatorGroup() {
        return webPageValidatorGroup;
    }

    @Override
    public AccountsWebPageParserService
    getAccountsWebPageParserService() {
        return accountsWebPageParserService;
    }

    @Override
    public SurugaBankWebPageService
    getSurugaBankWebPageService() {
        return surugaBankWebPageService;
    }

    @Override
    public WebPageHtmlFileWriter
    getWebPageHtmlFileWriter() {
        return webPageHtmlFileWriter;
    }

    @Override
    public AccountsDbService
    getAccountsDbService() {
        return accountsDbService;
    }

    public AppContextImp(File dataDirPath)
    throws Exception {

        this.dataDirPath = ObjectArgs.checkNotNull(dataDirPath, "dataDirPath");
        this.databaseFilePath = new File(dataDirPath, DATABASE_FILE_NAME);

        final MessageFormatter messageFormatter = MessageFormatterImpl.INSTANCE;

        final ExceptionThrower exceptionThrower = new ExceptionThrowerImpl(messageFormatter);

        final ThrowableToStringServiceFactory throwableToStringServiceFactory =
            ThrowableToStringServiceFactory.DEFAULT_IMPL;

        this.loggerService =
            new LoggerServiceImpl(
                throwableToStringServiceFactory,
                messageFormatter);

        loggerService.formatThenLog(logger, LoggerLevel.INFO,
            "Data directory: [%s]", dataDirPath.getAbsolutePath());

        final ChromeDevToolsAppContext chromeDevToolsAppContext = new ChromeDevToolsAppContext(messageFormatter);

        this.chromeLauncherService = chromeDevToolsAppContext.chromeLauncherService;

        this.retryStrategyFactory =
            BasicRetryStrategyImp.factoryBuilder()
                .maxRetryCount(9)
                .beforeRetrySleepDuration(Duration.ofMillis(100))
                .build();

        final JerichoHtmlParserService jerichoHtmlParserService = new JerichoHtmlParserServiceImp(exceptionThrower);

        this.webPageValidatorGroup =
            new WebPageValidatorGroupImp(
                ImmutableList.of(
                    new WebPageValidatorImp_Login(jerichoHtmlParserService),
                    new WebPageValidatorImp_AfterLogin(jerichoHtmlParserService, exceptionThrower),
                    new WebPageValidatorImp_Welcome(jerichoHtmlParserService, exceptionThrower),
                    new WebPageValidatorImp_Accounts(jerichoHtmlParserService, exceptionThrower),
                    new WebPageValidatorImp_Logout(jerichoHtmlParserService, exceptionThrower)
                ));

        final SqliteConnectionService sqliteConnectionService =
            new SqliteConnectionServiceImp(
                loggerService,
                exceptionThrower);

        final SqliteDslConnectionService dslConnectionService =
            new SqliteDslConnectionServiceImp(sqliteConnectionService);

        final DSLConnection dslConn = dslConnectionService.getConnectionFromFile(databaseFilePath);

        DbTables.init(dslConn);

        this.accountsWebPageParserService =
            new AccountsWebPageParserServiceImp(
                jerichoHtmlParserService,
                loggerService,
                exceptionThrower);

        final RetryService retryService = new RetryServiceImp(CollectionIndexMatcher.FIRST_AND_LAST_ONLY);

        this.surugaBankWebPageService =
            new SurugaBankWebPageServiceImp(
                chromeDevToolsAppContext.chromeDevToolsDomQuerySelectorFactory,
                retryService,
                retryStrategyFactory,
                loggerService,
                exceptionThrower);

        this.webPageHtmlFileWriter =
            new WebPageHtmlFileWriterImp(
                dataDirPath,
                loggerService);

        final DSLContextServiceImp dslContextService = new DSLContextServiceImp(exceptionThrower);

        this.accountsDbService =
            new AccountsDbServiceImp(
                dslConn,
                dslContextService);
    }
}
