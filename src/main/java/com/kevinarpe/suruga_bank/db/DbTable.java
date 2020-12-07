package com.kevinarpe.suruga_bank.db;

import com.google.common.collect.ImmutableList;
import org.jooq.Field;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public final class DbTable {

    public static final class Account {

        public static final Name TABLE_NAME = DSL.name("account");

        public static final Table<Record> TABLE = DSL.table(TABLE_NAME);

        public static final Name INDEX_NAME = DSL.name("account_index");

        public static final Index INDEX = DSL.index(INDEX_NAME);

        public static final ImmutableList<Field<?>> INDEX_FIELD_LIST =
            ImmutableList.of(Fields.AS_OF_LOCAL_DATE, Fields.TYPE, Fields.NAME);

        public static final class Fields {
            /**
             * Ex: "2019-08-15"
             */
            public static final Field<String> AS_OF_LOCAL_DATE =
                DSL.field(TABLE_NAME.append("as_of_local_date"),
                    SQLDataType.VARCHAR.nullable(false));
            /**
             * Ex: "SAVINGS", "LOAN"
             */
            public static final Field<String> TYPE =
                DSL.field(TABLE_NAME.append("type"),
                    SQLDataType.VARCHAR.nullable(false));
            /**
             * Ex: savings: "REGULAR_SAVINGS", loan: "MORTGAGE_LOAN",
             */
            public static final Field<String> NAME =
                DSL.field(TABLE_NAME.append("name"),
                    SQLDataType.VARCHAR.nullable(false));
            /**
             * Ex: savings: "普通預金", loan: "ローン"
             */
            public static final Field<String> NAME_JA =
                DSL.field(TABLE_NAME.append("name_ja"),
                    SQLDataType.VARCHAR.nullable(false));
            /**
             * Intentional: Signed!  Value can be negative, zero, or positive.
             */
            public static final Field<Long> SIGNED_BALANCE =
                DSL.field(TABLE_NAME.append("signed_balance"),
                    SQLDataType.BIGINT.nullable(false));
            /**
             * Ex: "JPY"
             */
            public static final Field<String> ISO_CURRENCY =
                DSL.field(TABLE_NAME.append("iso_currency"),
                    SQLDataType.VARCHAR.nullable(false));
        }
    }
}
