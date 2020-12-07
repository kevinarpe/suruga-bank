package com.kevinarpe.suruga_bank.db;

import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

import java.io.File;
import java.sql.Connection;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
// Scope: Global singleton
public final class SqliteDslConnectionServiceImp
implements SqliteDslConnectionService {

    private final SqliteConnectionService sqliteConnectionService;

    public SqliteDslConnectionServiceImp(SqliteConnectionService sqliteConnectionService) {

        this.sqliteConnectionService =
            ObjectArgs.checkNotNull(sqliteConnectionService, "sqliteConnectionService");
    }

    @Override
    public DSLConnection
    getConnectionFromFile(File filePath)
    throws Exception {

        final Connection dbConn = sqliteConnectionService.getConnectionFromFile(filePath);
        final DSLConnection x = _createDSLConnection(dbConn);
        return x;
    }

    @Override
    public DSLConnection
    getConnectionFromInMemory()
    throws Exception {

        final Connection dbConn = sqliteConnectionService.getConnectionFromInMemory();
        final DSLConnection x = _createDSLConnection(dbConn);
        return x;
    }

    private DSLConnection
    _createDSLConnection(Connection dbConn) {

        final Settings settings = new Settings();

        final Configuration config =
            new DefaultConfiguration()
                .set(dbConn)
                .set(SQLDialect.SQLITE)
                .set(settings);

        final DSLContext create = DSL.using(config);

        final DSLConnection x = new DSLConnection(dbConn, create);
        return x;
    }
}
