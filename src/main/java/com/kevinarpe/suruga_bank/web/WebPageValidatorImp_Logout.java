package com.kevinarpe.suruga_bank.web;

import com.google.common.collect.ImmutableList;
import com.googlecode.kevinarpe.papaya.annotation.EmptyStringAllowed;
import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.exception.ExceptionThrower;
import net.htmlparser.jericho.Element;

import java.util.regex.Pattern;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
// Scope: Global singleton
public final class WebPageValidatorImp_Logout
implements WebPageValidator {

    private final JerichoHtmlParserService jerichoHtmlParserService;
    private final ExceptionThrower exceptionThrower;

    public WebPageValidatorImp_Logout(JerichoHtmlParserService jerichoHtmlParserService,
                                      ExceptionThrower exceptionThrower) {

        this.jerichoHtmlParserService =
            ObjectArgs.checkNotNull(jerichoHtmlParserService, "jerichoHtmlParserService");

        this.exceptionThrower = ObjectArgs.checkNotNull(exceptionThrower, "exceptionThrower");
    }

    @Override
    public WebPage
    getWebPage() {
        return WebPage.LOGOUT;
    }

    private static final Pattern PATTERN =
        Pattern.compile(
            "ログアウト\\s*\n" +
                "\\s*ログアウトしました。ご利用ありがとうございました。\\s*\n" +
                "\\s*ウィンドウを閉じてください。",
            Pattern.DOTALL);

    @Override
    public void
    validate(String html)
    throws Exception {

        final JerichoHtmlSource source = new JerichoHtmlSource(getWebPage().name(), html);

        final Element submitElement =
            jerichoHtmlParserService.getElementByTagAndAttributes(source, source.source, HtmlElementTag.INPUT,
                ImmutableList.of(
                    JerichoHtmlAttributeMatcherImp.withValue("type", "submit"),
                    JerichoHtmlAttributeMatcherImp.withValue("name", "ACT_doClose"),
                    JerichoHtmlAttributeMatcherImp.withValue("value", "閉じる")));
/*
スルガ銀行


ログアウト

ログアウトしました。ご利用ありがとうございました。

ウィンドウを閉じてください。


【関連リンク】

    * 再度ログインされる場合はこちら <https://ib.surugabank.co.jp/cb/IBGate/>
DI20130100
このページの先頭へ戻る
------------------------------------------------------------------------
Copyright © 2012 SURUGA bank Ltd. All Rights Reserved.
 */
        @EmptyStringAllowed
        final String renderedText = source.source.getRenderer().toString().stripLeading().stripTrailing();
        if (false == PATTERN.matcher(renderedText).find()) {

            throw exceptionThrower.throwCheckedException(Exception.class,
                "Failed to match pattern >>>%s<<<:"
                    + "%n%s",
                PATTERN,
                renderedText);
        }
    }
}
