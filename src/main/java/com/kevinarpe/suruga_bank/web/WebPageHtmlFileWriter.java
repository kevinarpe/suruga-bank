package com.kevinarpe.suruga_bank.web;

import com.googlecode.kevinarpe.papaya.container.ImmutableFullEnumMap;

import java.time.ZonedDateTime;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public interface WebPageHtmlFileWriter {

    void writeAllToFiles(ZonedDateTime asOf,
                         ImmutableFullEnumMap<WebPage, String> webPageToHtmlMap)
    throws Exception;
}
