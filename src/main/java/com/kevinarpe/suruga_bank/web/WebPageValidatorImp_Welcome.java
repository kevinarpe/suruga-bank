package com.kevinarpe.suruga_bank.web;

import com.google.common.collect.ImmutableList;
import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.exception.ExceptionThrower;
import com.googlecode.kevinarpe.papaya.function.count.AtLeastCountMatcher;
import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.HtmlElementTag;
import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.JerichoHtmlParserService;
import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.JerichoHtmlSource;
import net.htmlparser.jericho.Element;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
// Scope: Global singleton
public final class WebPageValidatorImp_Welcome
implements WebPageValidator {

    private final JerichoHtmlParserService jerichoHtmlParserService;
    private final ExceptionThrower exceptionThrower;

    public WebPageValidatorImp_Welcome(JerichoHtmlParserService jerichoHtmlParserService,
                                       ExceptionThrower exceptionThrower) {

        this.jerichoHtmlParserService =
            ObjectArgs.checkNotNull(jerichoHtmlParserService, "jerichoHtmlParserService");

        this.exceptionThrower = ObjectArgs.checkNotNull(exceptionThrower, "exceptionThrower");
    }

    @Override
    public WebPage
    getWebPage() {
        return WebPage.WELCOME;
    }

    @Override
    public void
    validate(String html)
    throws Exception {

        final JerichoHtmlSource source = new JerichoHtmlSource(getWebPage().name(), html);

        final Element tableElement =
            jerichoHtmlParserService.getElementByTag(source, source.source, HtmlElementTag.TABLE);

        final ImmutableList<Element> trElementList =
            jerichoHtmlParserService.getElementListByTag(
                source, tableElement, HtmlElementTag.TR, AtLeastCountMatcher.ONE);

        if (2 != trElementList.size()) {

            throw exceptionThrower.throwCheckedException(Exception.class,
                "Expected exactly two table rows, but found %d", trElementList.size());
        }
        final ImmutableList<Element> headerTdElementList =
            _getTdElementList(source, trElementList, HtmlElementTag.TH, 0);

        _assertTdRenderedText(headerTdElementList, 0, "支店名");
        _assertTdRenderedText(headerTdElementList, 1, "科目");
        _assertTdRenderedText(headerTdElementList, 2, "口座番号");
        _assertTdRenderedText(headerTdElementList, 3, "残高");
        _assertTdRenderedText(headerTdElementList, 4, "支払可能残高");

        final ImmutableList<Element> dataTdElementList =
            _getTdElementList(source, trElementList, HtmlElementTag.TD, 1);

        _assertTdRenderedText(dataTdElementList, 0, "渋谷支店");
        _assertTdRenderedText(dataTdElementList, 1, "普通預金");
        _assertTdRenderedText(dataTdElementList, 2, "2916970");
        // Intentional: Do not assert cells #4 & #5.  They are account balances.
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

        if (5 != tdElementList.size()) {

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
