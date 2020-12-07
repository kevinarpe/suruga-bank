package com.kevinarpe.suruga_bank.web;

import com.google.common.collect.ImmutableList;
import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
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
                ImmutableList.of(
                    JerichoHtmlAttributeMatcherImp.withValue("method", "post"),
                    JerichoHtmlAttributeMatcherImp.withValue("action", "/cb/IBGate"),
                    JerichoHtmlAttributeMatcherImp.withValue("name", "LoginForm")));

        final Element branchNumTextInputElement =
            jerichoHtmlParserService.getElementByTagAndAttributes(source, formElement, HtmlElementTag.INPUT,
                ImmutableList.of(
                    JerichoHtmlAttributeMatcherImp.withValue("type", "text"),
                    JerichoHtmlAttributeMatcherImp.withValue("name", "BRA_NUM")));

        final Element accountTypeRadioInputElement =
            jerichoHtmlParserService.getElementByTagAndAttributes(source, formElement, HtmlElementTag.INPUT,
                ImmutableList.of(
                    JerichoHtmlAttributeMatcherImp.withValue("type", "radio"),
                    JerichoHtmlAttributeMatcherImp.withValue("name", "ACCT_TYPE_CHECKED"),
                    JerichoHtmlAttributeMatcherImp.withValue("id", "ACCT_TYPE_CHECKED0"),
                    JerichoHtmlAttributeMatcherImp.withValue("value", "100000JPY"),
                    JerichoHtmlAttributeMatcherImp.withValue("checked", "checked")));

        final Element accountNumTextInputElement =
            jerichoHtmlParserService.getElementByTagAndAttributes(source, formElement, HtmlElementTag.INPUT,
                ImmutableList.of(
                    JerichoHtmlAttributeMatcherImp.withValue("type", "text"),
                    JerichoHtmlAttributeMatcherImp.withValue("name", "ACCT_NUM")));

        final Element cashCardCodePasswordInputElement =
            jerichoHtmlParserService.getElementByTagAndAttributes(source, formElement, HtmlElementTag.INPUT,
                ImmutableList.of(
                    JerichoHtmlAttributeMatcherImp.withValue("type", "password"),
                    JerichoHtmlAttributeMatcherImp.withValue("name", "MASK_CASH_CARD_PWD"),
                    JerichoHtmlAttributeMatcherImp.withValue("id", "password1")));

        final Element accountHolderNameTextInputElement =
            jerichoHtmlParserService.getElementByTagAndAttributes(source, formElement, HtmlElementTag.INPUT,
                ImmutableList.of(
                    JerichoHtmlAttributeMatcherImp.withValue("type", "text"),
                    JerichoHtmlAttributeMatcherImp.withValue("name", "ACCT_HLDR_FW")));
    }
}
