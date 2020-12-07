package com.kevinarpe.suruga_bank.db;

import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.exception.ExceptionThrower;
import com.googlecode.kevinarpe.papaya.logging.slf4j.LoggerLevel;
import com.googlecode.kevinarpe.papaya.logging.slf4j.LoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
// Scope: Global singleton
public final class SqliteConnectionServiceImp
implements SqliteConnectionService {

    private static final String PREFIX = "jdbc:sqlite:";
    private static final Logger logger = LoggerFactory.getLogger(SqliteConnectionServiceImp.class);

    private final LoggerService loggerService;
    private final ExceptionThrower exceptionThrower;

    public SqliteConnectionServiceImp(LoggerService loggerService,
                                      ExceptionThrower exceptionThrower) {

        this.loggerService = ObjectArgs.checkNotNull(loggerService, "loggerService");
        this.exceptionThrower = ObjectArgs.checkNotNull(exceptionThrower, "exceptionThrower");
    }

    @Override
    public Connection
    getConnectionFromFile(File filePath)
    throws Exception {

        final String absPathname = filePath.getAbsolutePath();
        final String url = PREFIX + absPathname.replace('\\', '/');
        final Connection x = _getConnection(url);
        return x;
    }

    @Override
    public Connection
    getConnectionFromInMemory()
    throws Exception {

        final String url = PREFIX + ":memory:";
        final Connection x = _getConnection(url);
        return x;
    }

    private Connection
    _getConnection(String url)
    throws Exception {

        loggerService.formatThenLog(logger, LoggerLevel.INFO,
            "Opening JDBC connnection to SQLite database @ [%s]...", url);
        try {
            final Connection x = DriverManager.getConnection(url);
            return x;
        }
        catch (SQLException e) {
            throw exceptionThrower.throwChainedCheckedException(Exception.class,
                e,
                "Failed to create JDBC database connection for URL: [%s]", url);
        }
    }
}
