package com.kevinarpe.suruga_bank.main;

import com.googlecode.kevinarpe.papaya.exception.ThrowableUtils;
import com.googlecode.kevinarpe.papaya.java_mail.EmailMessageAddressListType;
import com.googlecode.kevinarpe.papaya.java_mail.EmailMessageAddressType;
import com.googlecode.kevinarpe.papaya.java_mail.JavaMailSession;
import com.googlecode.kevinarpe.papaya.java_mail.JavaMailSessionBuilder;
import com.googlecode.kevinarpe.papaya.java_mail.JavaMailSessionBuilderFactory;
import com.googlecode.kevinarpe.papaya.java_mail.TextMimeSubType;
import com.googlecode.kevinarpe.papaya.logging.slf4j.LoggerLevel;
import com.googlecode.kevinarpe.papaya.web.chrome_dev_tools.Chrome;
import com.kevinarpe.suruga_bank.AppContext;
import com.kevinarpe.suruga_bank.AppContextImp;
import com.kevinarpe.suruga_bank.args.CommandLineArguments;
import com.kevinarpe.suruga_bank.args.SaveDataMainArgs;
import com.kevinarpe.suruga_bank.web.AccountsWebPageParserService;
import com.kevinarpe.suruga_bank.web.SurugaBankWebCredentials;
import com.kevinarpe.suruga_bank.web.SurugaBankWebPageService;
import com.kevinarpe.suruga_bank.web.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.MimeMessage;
import java.time.ZonedDateTime;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public final class SaveDataMain {

    private static final Logger logger = LoggerFactory.getLogger(SaveDataMain.class);

    public static void main(String[] argArr)
    throws Exception {

        final SaveDataMainArgs args = new SaveDataMainArgs();
        CommandLineArguments.parseArgsOrExitOnFailure("suruga-bank-save-data", argArr, args);

        final JavaMailSession javaMailSession = _createJavaMailSession(args);

        try {
            _main(args);
        }
        catch (Exception e) {
            logger.error("Unexpected error", e);

            final MimeMessage m =
                javaMailSession.emailMessageBuilder()
                    .address(EmailMessageAddressType.FROM, args.emailAddress())
                    .addToAddressSet(EmailMessageAddressListType.TO, args.emailAddress())
                    .subject("Suruga Bank Save Data: Unexpected error")
                    .body(TextMimeSubType.PLAIN, ThrowableUtils.toStringWithStackTrace(e))
                    .build();

            javaMailSession.sendMessage(m);

            System.exit(1);  // non-zero / failure
        }
    }

    private static JavaMailSession
    _createJavaMailSession(SaveDataMainArgs args) {

        final JavaMailSessionBuilder b =
            JavaMailSessionBuilderFactory.INSTANCE.newInstance()
                .host(args.smtpHost(), args.smtpAlwaysTrustSsl())
                .customPort(args.smtpPort());

        if (null != args.nullableSmtpUsername()) {

            b.usernameAndPassword(args.nullableSmtpUsername(), args.nullableSmtpPassword());
        }
        final JavaMailSession x = b.build();
        return x;
    }

    private static void
    _main(SaveDataMainArgs args)
    throws Exception {

        final AppContext appContext = new AppContextImp(args.dataDirPath());

        final SurugaBankWebCredentials surugaBankWebCredentials =
            new SurugaBankWebCredentials(
                args.branchNumber(), args.accountNumber(), args.cashCardPasswordNumber(), args.accountHolderName());

        final ZonedDateTime asOf = ZonedDateTime.now();

        final String accountsHtml = _getAccountsHtml(args, appContext, surugaBankWebCredentials, asOf);

        final AccountsWebPageParserService.Result result =
            appContext.getAccountsWebPageParserService().parse(accountsHtml);

        appContext.getAccountsDbService().insert(asOf, result.accountMap);
        appContext.getLoggerService().log(logger, LoggerLevel.INFO, "Done");
    }

    private static String
    _getAccountsHtml(SaveDataMainArgs args,
                     AppContext appContext,
                     SurugaBankWebCredentials surugaBankWebCredentials,
                     ZonedDateTime asOf)
    throws Exception {

        final Chrome chrome =
            appContext.getChromeLauncherService().launchChrome(
                args.isChromeHeadless(),
                appContext.getRetryStrategyFactory());

        final SurugaBankWebPageService.Result result =
            appContext.getSurugaBankWebPageService().getHtmlMap(chrome, surugaBankWebCredentials);

        // Intentional: Write before validate.  Why?  If validate fails, we want a copy of the HTML to debug.
        appContext.getWebPageHtmlFileWriter().writeAllToFiles(asOf, result.webPageToHtmlMap);

        // Note: If you are debugging, this might fail if you are too slow to grab the document HTML *immediately*
        // after login.  Why?  That page immediately redirects to another page.
        appContext.getWebPageValidatorGroup().validateAll(result.webPageToHtmlMap);

        final String x = result.webPageToHtmlMap.get(WebPage.ACCOUNTS);
        return x;
    }
}
