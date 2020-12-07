package com.kevinarpe.suruga_bank.web;

import com.github.kklisura.cdt.protocol.events.page.LoadEventFired;
import com.github.kklisura.cdt.protocol.support.types.EventHandler;
import com.github.kklisura.cdt.protocol.types.page.Navigate;
import com.googlecode.kevinarpe.papaya.annotation.Blocking;
import com.googlecode.kevinarpe.papaya.annotation.OutputParam;
import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.container.ImmutableFullEnumMap;
import com.googlecode.kevinarpe.papaya.exception.ExceptionThrower;
import com.googlecode.kevinarpe.papaya.function.retry.RetryService;
import com.googlecode.kevinarpe.papaya.function.retry.RetryStrategyFactory;
import com.googlecode.kevinarpe.papaya.logging.slf4j.LoggerLevel;
import com.googlecode.kevinarpe.papaya.logging.slf4j.LoggerService;
import com.googlecode.kevinarpe.papaya.web.chrome_dev_tools.Chrome;
import com.googlecode.kevinarpe.papaya.web.chrome_dev_tools.ChromeDevToolsDomNode;
import com.googlecode.kevinarpe.papaya.web.chrome_dev_tools.ChromeDevToolsDomQuerySelectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.googlecode.kevinarpe.papaya.annotation.OutputParams.out;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
// Scope: Global singleton
public final class SurugaBankWebPageServiceImp
implements SurugaBankWebPageService {

    private static final Logger logger = LoggerFactory.getLogger(SurugaBankWebPageServiceImp.class);

    private final ChromeDevToolsDomQuerySelectorFactory domQuerySelectorFactory;
    private final RetryService retryService;
    private final RetryStrategyFactory retryStrategyFactory;
    private final LoggerService loggerService;
    private final ExceptionThrower exceptionThrower;

    public SurugaBankWebPageServiceImp(ChromeDevToolsDomQuerySelectorFactory domQuerySelectorFactory,
                                       RetryService retryService,
                                       RetryStrategyFactory retryStrategyFactory,
                                       LoggerService loggerService,
                                       ExceptionThrower exceptionThrower) {

        this.domQuerySelectorFactory =
            ObjectArgs.checkNotNull(domQuerySelectorFactory, "domQuerySelectorFactory");

        this.retryService = ObjectArgs.checkNotNull(retryService, "retryService");
        this.retryStrategyFactory = ObjectArgs.checkNotNull(retryStrategyFactory, "retryStrategyFactory");
        this.loggerService = ObjectArgs.checkNotNull(loggerService, "loggerService");
        this.exceptionThrower = exceptionThrower;
    }

    @Blocking
    @Override
    public Result
    getHtmlMap(Chrome chrome, SurugaBankWebCredentials credentials)
    throws Exception {

        final boolean isFairLock = false;
        final Lock lock = new ReentrantLock(isFairLock);
        final Condition lockCondition = lock.newCondition();
        // @GuardedBy("lock")
        final boolean[] isDoneRef = {false};

        final ImmutableFullEnumMap.Builder<WebPage, String> b = ImmutableFullEnumMap.builder(WebPage.class);
        final Exception[] exceptionRef = {null};

        chrome.chromeTab0.getPage().onLoadEventFired(new EventHandler<LoadEventFired>() {

            private int count = 0;

            @Override
            public void onEvent(LoadEventFired event) {

                ++count;
                loggerService.formatThenLog(logger, LoggerLevel.INFO,
                    "%s #%d", event.getClass().getSimpleName(), count);
                try {
                    switch (count) {

                        case 1: {
                            final String html = _doLogin(chrome, credentials, retryStrategyFactory);
                            b.put(WebPage.LOGIN, html);
                            break;
                        }
                        case 2: {
                            final String html = chrome.chromeTab0.getDocumentOuterHTML();
                            b.put(WebPage.AFTER_LOGIN, html);
                            break;
                        }
                        case 3: {
                            final String html = chrome.chromeTab0.getDocumentOuterHTML();
                            b.put(WebPage.WELCOME, html);
                            // Show accounts from top left menu.
                            chrome.chromeTab0.getPage().navigate("https://ib.surugabank.co.jp/cb/IBGate/i202301CT");
                            break;
                        }
                        case 4: {
                            final String html = chrome.chromeTab0.getDocumentOuterHTML();
                            b.put(WebPage.ACCOUNTS, html);
                            // Click logout button is actually <a><img ...></a>!
                            chrome.chromeTab0.getPage().navigate("https://ib.surugabank.co.jp/cb/IBGate/i201301CT");
                            break;
                        }
                        case 5: {
                            final String html = chrome.chromeTab0.getDocumentOuterHTML();
                            b.put(WebPage.LOGOUT, html);
                            _setDone(lock, out(isDoneRef), lockCondition);
                            break;
                        }
                        default: {
                            throw exceptionThrower.throwCheckedException(Exception.class,
                                "Internal error: Missing switch case %d", count);
                        }
                    }
                }
                catch (Exception e) {

                    exceptionRef[0] = e;
                    _setDone(lock, out(isDoneRef), lockCondition);
                }
            }
        });
        chrome.chromeTab0.getPage().enable();

        final Navigate navigate = retryService.call(retryStrategyFactory,
            () -> {
                final String url = "https://ib.surugabank.co.jp/cb/IBGate";
                final Navigate n = chrome.chromeTab0.getPage().navigate(url);
                @Nullable
                final String errorText = n.getErrorText();
                if (null != errorText) {
                    throw exceptionThrower.throwCheckedException(Exception.class,
                        "Failed to navigate to URL [%s]: [%s]", url, errorText);
                }
                return n;
            });

        _awaitDone(lock, lockCondition, isDoneRef);

        chrome.chromeTab0.getPage().disable();
        chrome.chromeTab0.awaitClose(retryStrategyFactory);
        chrome.close();

        if (null != exceptionRef[0]) {
            throw exceptionRef[0];
        }

        final ImmutableFullEnumMap<WebPage, String> map = b.build();
        final Result x = new Result(map);
        return x;
    }

    private String
    _doLogin(Chrome chrome,
             SurugaBankWebCredentials credentials,
             RetryStrategyFactory retryStrategyFactory)
    throws Exception {

        domQuerySelectorFactory.newInstance(chrome.chromeTab0)
            .parentNodeIsDocument()
            .awaitQuerySelectorExactlyOneThenRun(
                "input[type=text][name=BRA_NUM]", retryStrategyFactory,
                (ChromeDevToolsDomNode n) -> n.focus())
            .sendKeys(credentials.branchNumber);

        domQuerySelectorFactory.newInstance(chrome.chromeTab0)
            .parentNodeIsDocument()
            .awaitQuerySelectorExactlyOneThenRun(
                "input[type=text][name=ACCT_NUM]", retryStrategyFactory,
                (ChromeDevToolsDomNode n) -> n.focus())
            .sendKeys(credentials.accountNumber);

        domQuerySelectorFactory.newInstance(chrome.chromeTab0)
            .parentNodeIsDocument()
            .awaitQuerySelectorExactlyOneThenRun(
                "input[type=password][name=MASK_CASH_CARD_PWD]", retryStrategyFactory,
                (ChromeDevToolsDomNode n) -> n.focus())
            .sendKeys(credentials.cashCardPasswordNumber);

        domQuerySelectorFactory.newInstance(chrome.chromeTab0)
            .parentNodeIsDocument()
            .awaitQuerySelectorExactlyOneThenRun(
                "input[type=text][name=ACCT_HLDR_FW]", retryStrategyFactory,
                (ChromeDevToolsDomNode n) -> n.focus())
            // Note: Method sendKeys() will not work for Japanese text.
            .runJavaScriptExpression("$0.value = '" + credentials.accountHolderName + "'");

        final String html = chrome.chromeTab0.getDocumentOuterHTML();

        domQuerySelectorFactory.newInstance(chrome.chromeTab0)
            .parentNodeIsDocument()
            .awaitQuerySelectorExactlyOne("input[type=submit][name=ACT_doLogin]", retryStrategyFactory)
            .click();

        return html;
    }

    private void
    _setDone(Lock lock,
             @OutputParam boolean[] isDoneRef,
             Condition lockCondition) {

        lock.lock();
        isDoneRef[0] = true;
        try {
            lockCondition.signal();
        }
        finally {
            lock.unlock();
        }
    }

    private void
    _awaitDone(Lock lock,
               Condition lockCondition,
               boolean[] isDoneRef)
    throws Exception {

        lock.lock();
        try {
            while (false == isDoneRef[0]) {
                lockCondition.await();
            }
        }
        finally {
            lock.unlock();
        }
    }
}
