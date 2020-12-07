package com.kevinarpe.suruga_bank.web;

import com.kevinarpe.suruga_bank.TestGlobals;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public class WebPageValidatorImp_AccountsTest {

    private WebPageValidatorImp_Accounts classUnderTest;

    @BeforeMethod
    public void beforeEachTestMethod() {

        classUnderTest =
            new WebPageValidatorImp_Accounts(
                new JerichoHtmlParserServiceImp(TestGlobals.exceptionThrower),
                TestGlobals.exceptionThrower);
    }

    @Test
    public void validate_Pass()
    throws Exception {

        final String html = Files.readString(Path.of("test-data", "html-validation", "04-accounts.html"));
        classUnderTest.validate(html);
    }
}