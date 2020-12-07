package com.kevinarpe.suruga_bank.db;

import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import org.jooq.DSLContext;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public final class DSLConnection
implements AutoCloseable {

    private final Connection dbConn;
    public final DSLContext create;

    public DSLConnection(Connection dbConn, DSLContext create) {

        this.dbConn = ObjectArgs.checkNotNull(dbConn, "dbConn");
        this.create = ObjectArgs.checkNotNull(create, "create");
    }

    @Override
    public void close()
    throws Exception {
        try {
            dbConn.close();
        }
        catch (SQLException e) {
            // @DebugBreakpoint
            throw e;
        }
    }
}
