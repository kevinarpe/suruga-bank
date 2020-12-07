package com.kevinarpe.suruga_bank.db;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public final class DbTables {

    public static void init(DSLConnection dslConn)
    throws Exception {

        final int t1 =
            dslConn.create.createTableIfNotExists(DbTable.Account.TABLE)
                .column(DbTable.Account.Fields.AS_OF_LOCAL_DATE)
                .column(DbTable.Account.Fields.TYPE)
                .column(DbTable.Account.Fields.NAME)
                .column(DbTable.Account.Fields.NAME_JA)
                .column(DbTable.Account.Fields.SIGNED_BALANCE)
                .column(DbTable.Account.Fields.ISO_CURRENCY)
                .execute();

        final int ti1 =
            dslConn.create.createUniqueIndexIfNotExists(DbTable.Account.INDEX)
                .on(DbTable.Account.TABLE, DbTable.Account.INDEX_FIELD_LIST)
                .execute();
    }
}
