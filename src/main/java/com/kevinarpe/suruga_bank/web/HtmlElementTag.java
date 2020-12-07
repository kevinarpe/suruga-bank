package com.kevinarpe.suruga_bank.web;

import com.googlecode.kevinarpe.papaya.argument.StringArgs;
import net.htmlparser.jericho.HTMLElementName;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public enum HtmlElementTag {

    FORM(HTMLElementName.FORM),
    INPUT(HTMLElementName.INPUT),
    TABLE(HTMLElementName.TABLE),
    TR(HTMLElementName.TR),
    TH(HTMLElementName.TH),
    TD(HTMLElementName.TD),
    ;
    public final String tag;

    private HtmlElementTag(String tag) {

        this.tag = StringArgs.checkNotEmptyOrWhitespace(tag, "tag");
    }
}
