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
public class WebPageValidatorImp_WelcomeTest {

    private WebPageValidatorImp_Welcome classUnderTest;

    @BeforeMethod
    public void beforeEachTestMethod() {

        classUnderTest =
            new WebPageValidatorImp_Welcome(
                new JerichoHtmlParserServiceImp(TestGlobals.exceptionThrower),
                TestGlobals.exceptionThrower);
    }

    @Test
    public void validate_Pass()
    throws Exception {

        final String html = Files.readString(Path.of("test-data", "html-validation", "03-welcome.html"));
        classUnderTest.validate(html);
    }
}