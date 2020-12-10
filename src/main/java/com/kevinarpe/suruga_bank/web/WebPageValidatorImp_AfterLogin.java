package com.kevinarpe.suruga_bank.web;

import com.googlecode.kevinarpe.papaya.annotation.EmptyStringAllowed;
import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.exception.ExceptionThrower;
import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.HtmlElementTag;
import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.JerichoHtmlAttributesMatcherImp;
import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.JerichoHtmlParserService;
import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.JerichoHtmlSource;
import net.htmlparser.jericho.Element;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
// Scope: Global singleton
public final class WebPageValidatorImp_AfterLogin
implements WebPageValidator {

    private final JerichoHtmlParserService jerichoHtmlParserService;
    private final ExceptionThrower exceptionThrower;

    public WebPageValidatorImp_AfterLogin(JerichoHtmlParserService jerichoHtmlParserService,
                                          ExceptionThrower exceptionThrower) {

        this.jerichoHtmlParserService =
            ObjectArgs.checkNotNull(jerichoHtmlParserService, "jerichoHtmlParserService");

        this.exceptionThrower = ObjectArgs.checkNotNull(exceptionThrower, "exceptionThrower");
    }

    @Override
    public WebPage
    getWebPage() {
        return WebPage.AFTER_LOGIN;
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
                    .and(JerichoHtmlAttributesMatcherImp.withNonEmptyValue("name", "formDI20120290_001")));

        @EmptyStringAllowed
        final String renderedText = source.source.getRenderer().toString().stripLeading().stripTrailing();
        if (false == renderedText.isEmpty()) {

            throw exceptionThrower.throwCheckedException(Exception.class,
                "Expected empty rendered text but found: >>>%s<<<", renderedText);
        }
    }
}
