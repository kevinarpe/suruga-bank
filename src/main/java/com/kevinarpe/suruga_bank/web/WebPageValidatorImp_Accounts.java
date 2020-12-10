package com.kevinarpe.suruga_bank.web;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.googlecode.kevinarpe.papaya.annotation.OutputParam;
import com.googlecode.kevinarpe.papaya.annotation.ReadOnlyContainer;
import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.exception.ExceptionThrower;
import com.googlecode.kevinarpe.papaya.function.count.AtLeastCountMatcher;
import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.HtmlElementTag;
import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.JerichoHtmlParserService;
import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.JerichoHtmlSource;
import com.kevinarpe.suruga_bank.AccountName;
import net.htmlparser.jericho.Element;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import static com.googlecode.kevinarpe.papaya.annotation.OutputParams.out;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
// Scope: Global singleton
public final class WebPageValidatorImp_Accounts
implements WebPageValidator {

    private final JerichoHtmlParserService jerichoHtmlParserService;
    private final ExceptionThrower exceptionThrower;

    public WebPageValidatorImp_Accounts(JerichoHtmlParserService jerichoHtmlParserService,
                                        ExceptionThrower exceptionThrower) {
        this.jerichoHtmlParserService =
            ObjectArgs.checkNotNull(jerichoHtmlParserService, "jerichoHtmlParserService");

        this.exceptionThrower = ObjectArgs.checkNotNull(exceptionThrower, "exceptionThrower");
    }

    @Override
    public WebPage
    getWebPage() {
        return WebPage.ACCOUNTS;
    }

    // Should be map of jp text to enum
    private static final ImmutableSet<String> EXPECTED_ALL_ACCOUNT_NAME_SET = AccountName.TO_JAPANESE_TEXT_BIMAP.values();

    @Override
    public void
    validate(String html)
    throws Exception {

        final JerichoHtmlSource source = new JerichoHtmlSource(getWebPage().name(), html);

        final ImmutableList<Element> tableElementList =
            jerichoHtmlParserService.getElementListByTag(
                source, source.source, HtmlElementTag.TABLE, AtLeastCountMatcher.ONE);

        if (4 != tableElementList.size()) {

            throw exceptionThrower.throwCheckedException(Exception.class,
                "Expected exactly four tables, but found %d", tableElementList.size());
        }

        final LinkedHashSet<String> remainingExpectedAccountNameSet = new LinkedHashSet<>(EXPECTED_ALL_ACCOUNT_NAME_SET);

        _assertAccountTable(source, tableElementList, out(remainingExpectedAccountNameSet));
        _assertLoanTable(source, tableElementList, out(remainingExpectedAccountNameSet));

        if (false == remainingExpectedAccountNameSet.isEmpty()) {

            throw exceptionThrower.throwCheckedException(Exception.class,
                "Failed to find all expected savings and loan accounts names.  Remaining: %s",
                remainingExpectedAccountNameSet);
        }
    }

    private void
    _assertAccountTable(JerichoHtmlSource source,
                        @ReadOnlyContainer
                        List<Element> tableElementList,
                        @OutputParam
                        HashSet<String> remainingExpectedAccountNameSet)
    throws Exception {

        final Element tableElement = tableElementList.get(0);

        final ImmutableList<Element> trElementList =
            jerichoHtmlParserService.getElementListByTag(
                source, tableElement, HtmlElementTag.TR, AtLeastCountMatcher.ONE);

        final int trCount = trElementList.size();
        if (5 != trCount) {

            throw exceptionThrower.throwCheckedException(Exception.class,
                "Expected exactly five account table rows, but found %d", trCount);
        }
        final ImmutableList<Element> headerTdElementList =
            _getTdElementList(source, trElementList, HtmlElementTag.TH, 0);

        _assertTdRenderedText(headerTdElementList, 0, "科目");
        _assertTdRenderedText(headerTdElementList, 1, "口座数");
        _assertTdRenderedText(headerTdElementList, 2, "残高");
        _assertTdRenderedText(headerTdElementList, 3, "操作");

        for (int tri = 1; tri < trCount; ++tri) {

            final ImmutableList<Element> dataTdElementList =
                _getTdElementList(source, trElementList, HtmlElementTag.TD, tri);

            final Element dataTdElement = dataTdElementList.get(0);
            final String accountName = dataTdElement.getRenderer().toString().stripLeading().stripTrailing();

            if (false == remainingExpectedAccountNameSet.remove(accountName)) {

                throw exceptionThrower.throwCheckedException(Exception.class,
                    "Unexpected savings account name [%s]: Remaining expected: %s",
                    accountName, remainingExpectedAccountNameSet);
            }
        }
    }

    private void
    _assertLoanTable(JerichoHtmlSource source,
                     @ReadOnlyContainer
                     List<Element> tableElementList,
                     @OutputParam
                     HashSet<String> remainingExpectedAccountNameSet)
    throws Exception {

        final Element tableElement = tableElementList.get(1);

        final ImmutableList<Element> trElementList =
            jerichoHtmlParserService.getElementListByTag(
                source, tableElement, HtmlElementTag.TR, AtLeastCountMatcher.ONE);

        final int trCount = trElementList.size();
        if (3 != trCount) {

            throw exceptionThrower.throwCheckedException(Exception.class,
                "Expected exactly three loan table rows, but found %d", trCount);
        }
        final ImmutableList<Element> headerTdElementList =
            _getTdElementList(source, trElementList, HtmlElementTag.TH, 0);

        _assertTdRenderedText(headerTdElementList, 0, "科目");
        _assertTdRenderedText(headerTdElementList, 1, "口数");
        _assertTdRenderedText(headerTdElementList, 2, "借入額");
        _assertTdRenderedText(headerTdElementList, 3, "操作");

        for (int tri = 1; tri < trCount; ++tri) {

            final ImmutableList<Element> dataTdElementList =
                _getTdElementList(source, trElementList, HtmlElementTag.TD, tri);

            final Element dataTdElement = dataTdElementList.get(0);
            final String accountName = dataTdElement.getRenderer().toString().stripLeading().stripTrailing();

            if (false == remainingExpectedAccountNameSet.remove(accountName)) {

                throw exceptionThrower.throwCheckedException(Exception.class,
                    "Unexpected loan account name [%s]: Remaining expected: %s",
                    accountName, remainingExpectedAccountNameSet);
            }
        }
    }

    private ImmutableList<Element>
    _getTdElementList(JerichoHtmlSource source,
                      ImmutableList<Element> trElementList,
                      HtmlElementTag thOrTdTag,
                      final int index)
    throws Exception {

        final Element trElement = trElementList.get(index);

        final ImmutableList<Element> tdElementList =
            jerichoHtmlParserService.getElementListByTag(source, trElement, thOrTdTag, AtLeastCountMatcher.ONE);

        if (4 != tdElementList.size()) {

            throw exceptionThrower.throwCheckedException(Exception.class,
                "Expected exactly five table rows, but found %d", tdElementList.size());
        }
        return tdElementList;
    }

    private void
    _assertTdRenderedText(ImmutableList<Element> tdElementList,
                          final int index,
                          String renderedText)
    throws Exception {

        final Element tdElement = tdElementList.get(index);
        final String s = tdElement.getRenderer().toString().stripLeading().stripTrailing();
        if (false == s.equals(renderedText)) {

            throw exceptionThrower.throwCheckedException(Exception.class,
                "Table cell #%d: Expected text [%s], but found [%s]", (1 + index), renderedText, s);
        }
    }
}
