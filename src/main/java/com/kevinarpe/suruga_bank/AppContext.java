package com.kevinarpe.suruga_bank;

import com.googlecode.kevinarpe.papaya.function.retry.RetryStrategyFactory;
import com.googlecode.kevinarpe.papaya.logging.slf4j.LoggerService;
import com.googlecode.kevinarpe.papaya.web.chrome_dev_tools.ChromeLauncherService;
import com.kevinarpe.suruga_bank.web.AccountsDbService;
import com.kevinarpe.suruga_bank.web.AccountsWebPageParserService;
import com.kevinarpe.suruga_bank.web.SurugaBankWebPageService;
import com.kevinarpe.suruga_bank.web.WebPageHtmlFileWriter;
import com.kevinarpe.suruga_bank.web.WebPageValidatorGroup;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public interface AppContext {

    ChromeLauncherService
    getChromeLauncherService();

    RetryStrategyFactory
    getRetryStrategyFactory();

    LoggerService
    getLoggerService();

    WebPageValidatorGroup
    getWebPageValidatorGroup();

    AccountsWebPageParserService
    getAccountsWebPageParserService();

    SurugaBankWebPageService
    getSurugaBankWebPageService();

    WebPageHtmlFileWriter
    getWebPageHtmlFileWriter();

    AccountsDbService
    getAccountsDbService();
}
