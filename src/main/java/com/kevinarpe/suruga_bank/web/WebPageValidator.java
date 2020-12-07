package com.kevinarpe.suruga_bank.web;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public interface WebPageValidator {

    WebPage getWebPage();

    void validate(String html)
    throws Exception;
}
