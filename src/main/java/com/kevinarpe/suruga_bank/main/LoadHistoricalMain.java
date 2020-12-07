package com.kevinarpe.suruga_bank.main;

import com.googlecode.kevinarpe.papaya.annotation.EmptyContainerAllowed;
import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.logging.slf4j.LoggerLevel;
import com.kevinarpe.suruga_bank.AppContext;
import com.kevinarpe.suruga_bank.AppContextImp;
import com.kevinarpe.suruga_bank.web.AccountsWebPageParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public final class LoadHistoricalMain {

    private static final Logger logger = LoggerFactory.getLogger(LoadHistoricalMain.class);

    public static void main(String[] argArr)
    throws Exception {

        final File dataDirPath = new File("./data").getAbsoluteFile();
        final AppContext appContext = new AppContextImp(dataDirPath);
        @EmptyContainerAllowed
        @Nullable
        final File[] dirPathArr = dataDirPath.listFiles(File::isDirectory);
        ObjectArgs.checkNotNull(dirPathArr, "dirPathArr");

        for (final File dirPath : dirPathArr) {

            final File filePath = new File(dirPath, "04-accounts.html");
            if (false == filePath.exists()) {

                appContext.getLoggerService().formatThenLog(logger, LoggerLevel.INFO,
                    "Does not exist: [%s]", filePath.getAbsolutePath());
                continue;
            }
            appContext.getLoggerService().formatThenLog(logger, LoggerLevel.INFO,
                "Processing file... [%s]", filePath.getAbsolutePath());

            final String accountsHtml = Files.readString(filePath.toPath());
            final AccountsWebPageParserService.Result result =
                appContext.getAccountsWebPageParserService().parse(accountsHtml);

            final LocalDate localDate = LocalDate.parse(dirPath.getName(), DateTimeFormatter.BASIC_ISO_DATE);
            final ZonedDateTime asOf = ZonedDateTime.of(localDate, LocalTime.MIDNIGHT, ZoneId.systemDefault());

            appContext.getAccountsDbService().insert(asOf, result.accountMap);
        }
    }
}
