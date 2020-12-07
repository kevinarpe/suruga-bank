package com.kevinarpe.suruga_bank.web;

import com.google.common.collect.ImmutableList;
import com.googlecode.kevinarpe.papaya.annotation.FullyTested;
import com.googlecode.kevinarpe.papaya.annotation.OutputParam;
import com.googlecode.kevinarpe.papaya.annotation.ReadOnlyContainer;
import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.container.ImmutableFullEnumMap;
import com.googlecode.kevinarpe.papaya.exception.ExceptionThrower;
import com.googlecode.kevinarpe.papaya.logging.slf4j.LoggerLevel;
import com.googlecode.kevinarpe.papaya.logging.slf4j.LoggerService;
import com.kevinarpe.suruga_bank.AccountName;
import com.kevinarpe.suruga_bank.AccountType;
import net.htmlparser.jericho.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.googlecode.kevinarpe.papaya.annotation.OutputParams.out;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
// Scope: Global singleton
@FullyTested
public final class AccountsWebPageParserServiceImp
implements AccountsWebPageParserService {

    private static final Logger logger = LoggerFactory.getLogger(AccountsWebPageParserServiceImp.class);

    private final JerichoHtmlParserService jerichoHtmlParserService;
    private final LoggerService loggerService;
    private final ExceptionThrower exceptionThrower;

    public AccountsWebPageParserServiceImp(JerichoHtmlParserService jerichoHtmlParserService,
                                           LoggerService loggerService,
                                           ExceptionThrower exceptionThrower) {
        this.jerichoHtmlParserService =
            ObjectArgs.checkNotNull(jerichoHtmlParserService, "jerichoHtmlParserService");

        this.loggerService = ObjectArgs.checkNotNull(loggerService, "loggerService");

        this.exceptionThrower = ObjectArgs.checkNotNull(exceptionThrower, "exceptionThrower");
    }

    @Override
    public Result
    parse(String html)
    throws Exception {

        final JerichoHtmlSource source = new JerichoHtmlSource(WebPage.ACCOUNTS.name(), html);

        @ReadOnlyContainer
        final List<Element> tableElementList =
            jerichoHtmlParserService.getNonEmptyElementListByTag(source, source.source, HtmlElementTag.TABLE);

        if (4 != tableElementList.size()) {

            throw exceptionThrower.throwCheckedException(Exception.class,
                "Expected exactly four tables, but found %d", tableElementList.size());
        }
        final LinkedHashMap<AccountName, Result.Account> b = new LinkedHashMap<>();

        _parseAccountTable(source, tableElementList, out(b), AccountType.SAVINGS,
            0, ImmutableList.of("科目", "口座数", "残高", "操作"), 0, 2);

        _parseAccountTable(source, tableElementList, out(b), AccountType.LOAN,
            1, ImmutableList.of("科目", "口数", "借入額", "操作"), 0, 2);

        final ImmutableFullEnumMap<AccountName, Result.Account> map = ImmutableFullEnumMap.copyOf(AccountName.class, b);
        final Result x = new Result(map);
        return x;
    }

    private void
    _parseAccountTable(JerichoHtmlSource source,
                       @ReadOnlyContainer
                       List<Element> tableElementList,
                       @OutputParam
                       LinkedHashMap<AccountName, Result.Account> b,
                       AccountType accountType,
                       final int tableIndex,
                       ImmutableList<String> headerList,
                       final int accountNameIndex,
                       final int balanceIndex)
    throws Exception {

        final Element tableElement = tableElementList.get(tableIndex);

        @ReadOnlyContainer
        final List<Element> trElementList =
            jerichoHtmlParserService.getNonEmptyElementListByTag(source, tableElement, HtmlElementTag.TR);

        final int trCount = trElementList.size();
        // Note: It is not safe to assert count: If account becomes negative, it will appear in loan account table!

        @ReadOnlyContainer
        final List<Element> headerTdElementList = _getTdElementList(source, trElementList, HtmlElementTag.TH, 0);
        final int headerCount = headerList.size();

        if (headerTdElementList.size() != headerCount) {

            throw exceptionThrower.throwCheckedException(Exception.class,
                "Expected %d headers, but found %d", headerCount, headerTdElementList.size());
        }
        for (int i = 0; i < headerCount; ++i) {

            final String header = headerList.get(i);
            _assertTdRenderedText(headerTdElementList, i, header);
        }

        for (int tri = 1; tri < trCount; ++tri) {

            @ReadOnlyContainer
            final List<Element> dataTdElementList = _getTdElementList(source, trElementList, HtmlElementTag.TD, tri);
            final AccountName accountName = _getAccountName(dataTdElementList, b, accountNameIndex);
            // Ex: "1,171,239円"
            final String balanceStr = _getTdRenderedText(dataTdElementList, balanceIndex);
            final long balance = _parseBalance(accountType, accountName, balanceStr);

            b.put(accountName, new Result.Account(accountName, balance));
        }
    }

    private AccountName
    _getAccountName(List<Element> dataTdElementList,
                    @ReadOnlyContainer
                    Map<AccountName, Result.Account> b,
                    final int index)
    throws Exception {

        // Ex: "普通預金"
        final String jpText = _getTdRenderedText(dataTdElementList, index);
        @Nullable
        final AccountName accountName = AccountName.TO_JAPANESE_TEXT_BIMAP.inverse().get(jpText);
        if (null == accountName) {

            throw exceptionThrower.throwCheckedException(Exception.class,
                "Unknown account name: [%s]", jpText);
        }
        if (b.containsKey(accountName)) {

            throw exceptionThrower.throwCheckedException(Exception.class,
                "Account [%s] appears twice", jpText);
        }
        return accountName;
    }

    private long
    _parseBalance(AccountType accountType,
                  AccountName accountName,
                  final String balanceStr)
    throws Exception {

        final long sign = accountType.equals(accountName.accountType) ? +1L : -1L;

        // Ex: "1,171,239円" -> "1171239"
        final String s = balanceStr.replace(",", "").replace("円", "");
        try {
            final long balance = Long.parseLong(s);
            if (sign > 0) {
                return balance;
            }
            else {
                final long x = -1L * balance;
                loggerService.formatThenLog(logger, LoggerLevel.INFO,
                    "Account [%s:%s] has inverted balance sign: [%s]->%d",
                    accountName.name(), accountName.japaneseText, balanceStr, x);
                return x;
            }
        }
        catch (Exception e) {
            throw exceptionThrower.throwChainedCheckedException(Exception.class,
                e,
                "Account [%s:%s]: Failed to parse balance: [%s]->[%s]",
                accountName.name(), accountName.japaneseText, balanceStr, s);
        }
    }

    @ReadOnlyContainer
    private List<Element>
    _getTdElementList(JerichoHtmlSource source,
                      List<Element> trElementList,
                      HtmlElementTag thOrTdTag,
                      final int index)
    throws Exception {

        final Element trElement = trElementList.get(index);
        @ReadOnlyContainer
        final List<Element> tdElementList =
            jerichoHtmlParserService.getNonEmptyElementListByTag(source, trElement, thOrTdTag);

        if (4 != tdElementList.size()) {

            throw exceptionThrower.throwCheckedException(Exception.class,
                "Expected exactly five table rows, but found %d", tdElementList.size());
        }
        return tdElementList;
    }

    private void
    _assertTdRenderedText(@ReadOnlyContainer
                          List<Element> tdElementList,
                          final int index,
                          String renderedText)
    throws Exception {

        final String s = _getTdRenderedText(tdElementList, index);
        if (false == s.equals(renderedText)) {

            throw exceptionThrower.throwCheckedException(Exception.class,
                "Table cell #%d: Expected text [%s], but found [%s]", (1 + index), renderedText, s);
        }
    }

    private String
    _getTdRenderedText(@ReadOnlyContainer
                       List<Element> tdElementList,
                       final int index) {

        final Element tdElement = tdElementList.get(index);
        final String x = tdElement.getRenderer().toString().stripLeading().stripTrailing();
        return x;
    }
}
