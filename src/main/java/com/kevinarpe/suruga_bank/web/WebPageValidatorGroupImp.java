package com.kevinarpe.suruga_bank.web;

import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.container.ImmutableFullEnumMap;

import java.util.Collection;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
// Scope: Global singleton
public final class WebPageValidatorGroupImp
implements WebPageValidatorGroup {

    private final ImmutableFullEnumMap<WebPage, WebPageValidator> map;

    public WebPageValidatorGroupImp(Collection<WebPageValidator> c) {

        this.map = ImmutableFullEnumMap.ofValues(WebPage.class, c, WebPageValidator::getWebPage);
    }

    @Override
    public void
    validate(WebPage webPage, String html)
    throws Exception {

        ObjectArgs.checkNotNull(webPage, "webPage");
        ObjectArgs.checkNotNull(html, "html");

        final WebPageValidator v = map.get(webPage);
        v.validate(html);
    }

    @Override
    public void
    validateAll(ImmutableFullEnumMap<WebPage, String> webPageToHtmlMap)
    throws Exception {

        for (final WebPage webPage : WebPage.values()) {

            final String html = webPageToHtmlMap.get(webPage);
            validate(webPage, html);
        }
    }
}
