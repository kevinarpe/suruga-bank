package com.kevinarpe.suruga_bank.web;

import com.googlecode.kevinarpe.papaya.annotation.Blocking;
import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.container.ImmutableFullEnumMap;
import com.googlecode.kevinarpe.papaya.web.chrome_dev_tools.Chrome;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public interface SurugaBankWebPageService {

    @Blocking
    Result getHtmlMap(Chrome chrome, SurugaBankWebCredentials credentials)
    throws Exception;

    public static final class Result {

        public final ImmutableFullEnumMap<WebPage, String> webPageToHtmlMap;

        public Result(ImmutableFullEnumMap<WebPage, String> webPageToHtmlMap) {

            this.webPageToHtmlMap = ObjectArgs.checkNotNull(webPageToHtmlMap, "webPageToHtmlMap");
        }
    }
}
