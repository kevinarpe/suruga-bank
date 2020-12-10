package com.kevinarpe.suruga_bank.web;

import com.googlecode.kevinarpe.papaya.web.jericho_html_parser.JerichoHtmlParserServiceImp;
import com.kevinarpe.suruga_bank.TestGlobals;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public class WebPageValidatorImp_LoginTest {

    private WebPageValidatorImp_Login classUnderTest;

    @BeforeMethod
    public void beforeEachTestMethod() {

        classUnderTest =
            new WebPageValidatorImp_Login(
                new JerichoHtmlParserServiceImp(TestGlobals.exceptionThrower));
    }

    @Test
    public void validate_Pass()
    throws Exception {

        final String html = Files.readString(Path.of("test-data", "html-validation", "01-login.html"));
        classUnderTest.validate(html);
    }
}