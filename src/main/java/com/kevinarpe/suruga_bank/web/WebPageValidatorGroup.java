package com.kevinarpe.suruga_bank.web;

import com.googlecode.kevinarpe.papaya.container.ImmutableFullEnumMap;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public interface WebPageValidatorGroup {

    void validate(WebPage webPage, String html)
    throws Exception;

    void
    validateAll(ImmutableFullEnumMap<WebPage, String> webPageToHtmlMap)
    throws Exception;
}
