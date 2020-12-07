package com.kevinarpe.suruga_bank.web;

import com.googlecode.kevinarpe.papaya.PathUtils;
import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.container.ImmutableFullEnumMap;
import com.googlecode.kevinarpe.papaya.exception.PathException;
import com.googlecode.kevinarpe.papaya.logging.slf4j.LoggerLevel;
import com.googlecode.kevinarpe.papaya.logging.slf4j.LoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
// Scope: Global singleton
public final class WebPageHtmlFileWriterImp
implements WebPageHtmlFileWriter {

    private static final Logger logger = LoggerFactory.getLogger(WebPageHtmlFileWriterImp.class);

    private final File dirPath;
    private final LoggerService loggerService;

    public WebPageHtmlFileWriterImp(File dirPath,
                                    LoggerService loggerService) {

        this.dirPath = ObjectArgs.checkNotNull(dirPath, "dirPath");
        this.loggerService = ObjectArgs.checkNotNull(loggerService, "loggerService");
    }

    @Override
    public void
    writeAllToFiles(ZonedDateTime asOf,
                    ImmutableFullEnumMap<WebPage, String> webPageToHtmlMap)
    throws Exception {

        for (final WebPage webPage : WebPage.values()) {

            final String html = webPageToHtmlMap.get(webPage);
            final File filePath = _getFilePath(asOf, webPage);
            _makeDirectory(filePath);

            loggerService.formatThenLog(logger, LoggerLevel.INFO, "%s.%s: Writing to file [%s]...",
                webPage.getClass().getSimpleName(), webPage.name(), filePath.getAbsolutePath());

            Files.writeString(filePath.toPath(), html, StandardCharsets.UTF_8);
        }
    }

    private void
    _makeDirectory(File filePath)
    throws PathException {

        final File dirPath = filePath.getParentFile();
        if (false == dirPath.exists()) {

            loggerService.formatThenLog(logger, LoggerLevel.INFO,
                "$ mkdir --parents %s", dirPath.getAbsolutePath());

            PathUtils.makeDirectoryAndParents(dirPath);
        }
    }

    private File
    _getFilePath(ZonedDateTime asOf, WebPage webPage) {

        final String fileName =
            (1 + webPage.ordinal()) + "-" + webPage.name().replace('_', '-').toLowerCase() + ".html";

        final String asOfLocalDateStr = asOf.toLocalDate().format(DateTimeFormatter.BASIC_ISO_DATE);
        final File x = new File(new File(dirPath, asOfLocalDateStr), fileName);
        return x;
    }
}
