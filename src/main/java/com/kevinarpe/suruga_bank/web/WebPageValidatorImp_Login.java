package com.kevinarpe.suruga_bank.web;

import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.HtmlElementTag;
import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.JerichoHtmlAttributesMatcherImp;
import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.JerichoHtmlParserService;
import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.JerichoHtmlSource;
import net.htmlparser.jericho.Element;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
// Scope: Global singleton
public final class WebPageValidatorImp_Login
implements WebPageValidator {

    private final JerichoHtmlParserService jerichoHtmlParserService;

    public WebPageValidatorImp_Login(JerichoHtmlParserService jerichoHtmlParserService) {

        this.jerichoHtmlParserService =
            ObjectArgs.checkNotNull(jerichoHtmlParserService, "jerichoHtmlParserService");
    }

    @Override
    public WebPage
    getWebPage() {
        return WebPage.LOGIN;
    }

    @Override
    public void
    validate(String html)
    throws Exception {

        final JerichoHtmlSource source = new JerichoHtmlSource(getWebPage().name(), html);

        final Element formElement =
            jerichoHtmlParserService.getElementByTagAndAttributes(source, source.source, HtmlElementTag.FORM,
                JerichoHtmlAttributesMatcherImp.withNonEmptyValue("method", "post")
                    .and(JerichoHtmlAttributesMatcherImp.withNonEmptyValue("action", "/cb/IBGate"))
                    .and(JerichoHtmlAttributesMatcherImp.withNonEmptyValue("name", "LoginForm")));

        final Element branchNumTextInputElement =
            jerichoHtmlParserService.getElementByTagAndAttributes(source, formElement, HtmlElementTag.INPUT,
                JerichoHtmlAttributesMatcherImp.withNonEmptyValue("type", "text")
                    .and(JerichoHtmlAttributesMatcherImp.withNonEmptyValue("name", "BRA_NUM")));

        final Element accountTypeRadioInputElement =
            jerichoHtmlParserService.getElementByTagAndAttributes(source, formElement, HtmlElementTag.INPUT,
                JerichoHtmlAttributesMatcherImp.withNonEmptyValue("type", "radio")
                    .and(JerichoHtmlAttributesMatcherImp.withNonEmptyValue("name", "ACCT_TYPE_CHECKED"))
                    .and(JerichoHtmlAttributesMatcherImp.withNonEmptyValue("id", "ACCT_TYPE_CHECKED0"))
                    .and(JerichoHtmlAttributesMatcherImp.withNonEmptyValue("value", "100000JPY"))
                    .and(JerichoHtmlAttributesMatcherImp.withNonEmptyValue("checked", "checked")));

        final Element accountNumTextInputElement =
            jerichoHtmlParserService.getElementByTagAndAttributes(source, formElement, HtmlElementTag.INPUT,
                JerichoHtmlAttributesMatcherImp.withNonEmptyValue("type", "text")
                    .and(JerichoHtmlAttributesMatcherImp.withNonEmptyValue("name", "ACCT_NUM")));

        final Element cashCardCodePasswordInputElement =
            jerichoHtmlParserService.getElementByTagAndAttributes(source, formElement, HtmlElementTag.INPUT,
                JerichoHtmlAttributesMatcherImp.withNonEmptyValue("type", "password")
                    .and(JerichoHtmlAttributesMatcherImp.withNonEmptyValue("name", "MASK_CASH_CARD_PWD"))
                    .and(JerichoHtmlAttributesMatcherImp.withNonEmptyValue("id", "password1")));

        final Element accountHolderNameTextInputElement =
            jerichoHtmlParserService.getElementByTagAndAttributes(source, formElement, HtmlElementTag.INPUT,
                JerichoHtmlAttributesMatcherImp.withNonEmptyValue("type", "text")
                    .and(JerichoHtmlAttributesMatcherImp.withNonEmptyValue("name", "ACCT_HLDR_FW")));
    }
}
