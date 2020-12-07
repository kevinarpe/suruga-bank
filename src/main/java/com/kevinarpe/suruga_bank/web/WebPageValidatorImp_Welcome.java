package com.kevinarpe.suruga_bank.web;

import com.google.common.collect.ImmutableList;
import com.googlecode.kevinarpe.papaya.annotation.ReadOnlyContainer;
import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.exception.ExceptionThrower;
import net.htmlparser.jericho.Element;

import java.util.List;

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
            jerichoHtmlParserService.getElementByTagAndAttributes(source, source.source, HtmlElementTag.TABLE,
                ImmutableList.of());

        @ReadOnlyContainer
        final List<Element> trElementList =
            jerichoHtmlParserService.getNonEmptyElementListByTag(source, tableElement, HtmlElementTag.TR);

        if (2 != trElementList.size()) {

            throw exceptionThrower.throwCheckedException(Exception.class,
                "Expected exactly two table rows, but found %d", trElementList.size());
        }
        @ReadOnlyContainer
        final List<Element> headerTdElementList = _getTdElementList(source, trElementList, HtmlElementTag.TH, 0);
        _assertTdRenderedText(headerTdElementList, 0, "支店名");
        _assertTdRenderedText(headerTdElementList, 1, "科目");
        _assertTdRenderedText(headerTdElementList, 2, "口座番号");
        _assertTdRenderedText(headerTdElementList, 3, "残高");
        _assertTdRenderedText(headerTdElementList, 4, "支払可能残高");

        @ReadOnlyContainer
        final List<Element> dataTdElementList = _getTdElementList(source, trElementList, HtmlElementTag.TD, 1);
        _assertTdRenderedText(dataTdElementList, 0, "渋谷支店");
        _assertTdRenderedText(dataTdElementList, 1, "普通預金");
        _assertTdRenderedText(dataTdElementList, 2, "2916970");
        // Intentional: Do not assert cells #4 & #5.  They are account balances.
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

        if (5 != tdElementList.size()) {

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

        final Element tdElement = tdElementList.get(index);
        final String s = tdElement.getRenderer().toString().stripLeading().stripTrailing();
        if (false == s.equals(renderedText)) {

            throw exceptionThrower.throwCheckedException(Exception.class,
                "Table cell #%d: Expected text [%s], but found [%s]", (1 + index), renderedText, s);
        }
    }
}
