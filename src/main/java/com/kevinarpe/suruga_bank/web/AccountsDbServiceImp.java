package com.kevinarpe.suruga_bank.web;

import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.container.ImmutableFullEnumMap;
import com.kevinarpe.suruga_bank.AccountName;
import com.kevinarpe.suruga_bank.db.AccountRecord;
import com.kevinarpe.suruga_bank.db.DSLConnection;
import com.kevinarpe.suruga_bank.db.DSLContextService;
import com.kevinarpe.suruga_bank.db.DbTable;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
// Scope: Global singleton
public final class AccountsDbServiceImp
implements AccountsDbService {

    private final DSLConnection dslConn;
    private final DSLContextService dslContextService;

    public AccountsDbServiceImp(DSLConnection dslConn,
                                DSLContextService dslContextService) {

        this.dslConn = ObjectArgs.checkNotNull(dslConn, "dslConn");
        this.dslContextService = ObjectArgs.checkNotNull(dslContextService, "dslContextService");
    }

    @Override
    public void
    insert(ZonedDateTime asOf,
           ImmutableFullEnumMap<AccountName, AccountsWebPageParserService.Result.Account> accountMap)
    throws Exception {

        final String asOfLocalDate = asOf.toLocalDate().format(DateTimeFormatter.BASIC_ISO_DATE);

        for (final AccountsWebPageParserService.Result.Account account : accountMap.values()) {

            _insert(account, asOfLocalDate);
        }
    }

    private void
    _insert(AccountsWebPageParserService.Result.Account account, String asOfLocalDate)
    throws Exception {

        dslContextService.execute(
            dslConn.create.insertInto(DbTable.Account.TABLE)
                .set(DbTable.Account.Fields.AS_OF_LOCAL_DATE, asOfLocalDate)
                .set(DbTable.Account.Fields.TYPE, account.accountName.accountType.name())
                .set(DbTable.Account.Fields.NAME, account.accountName.name())
                .set(DbTable.Account.Fields.NAME_JA, account.accountName.japaneseText)
                .set(DbTable.Account.Fields.SIGNED_BALANCE, account.signedBalance)
                .set(DbTable.Account.Fields.ISO_CURRENCY, account.isoCurrency)
                .onConflict(DbTable.Account.INDEX_FIELD_LIST)
                .doUpdate()
                .set(DbTable.Account.Fields.SIGNED_BALANCE, account.signedBalance)
        );
    }

    @Override
    public Result
    select(AccountName accountName, ZonedDateTime inclusiveBegin)
    throws Exception {

        final ZonedDateTime inclusiveBeginUtc = inclusiveBegin.withZoneSameInstant(ZoneOffset.UTC);
        final LocalDateTime inclusiveBeginUtcLocalDateTime = inclusiveBeginUtc.toLocalDateTime();

        final AccountRecord current = _selectLatestForDate(accountName, inclusiveBeginUtcLocalDateTime);
        final AccountRecord previous = _selectPreviousForDateTime(accountName, inclusiveBeginUtcLocalDateTime);
        final Result x = new Result(current, previous);
        return x;
    }

    private static final Table<Record> A1 = DbTable.Account.TABLE.as("A1");
    private static final Table<Record> A2 = DbTable.Account.TABLE.as("A2");

    private AccountRecord
    _selectLatestForDate(AccountName accountName, LocalDateTime inclusiveBeginUtcLocalDateTime)
    throws Exception {

        final LocalDateTime end = inclusiveBeginUtcLocalDateTime.truncatedTo(ChronoUnit.DAYS).plusDays(1);

        final String inclusiveBegin = inclusiveBeginUtcLocalDateTime.toString();
        final String exclusiveEnd = end.toString();

        final Record2<Long, String> record =
            dslContextService.selectExactlyOne(
                dslConn.create.select(
                    A1.field(DbTable.Account.Fields.SIGNED_BALANCE),
                    A1.field(DbTable.Account.Fields.AS_OF_LOCAL_DATE))
                    .from(A1)
                    .where(
                        A1.field(DbTable.Account.Fields.AS_OF_LOCAL_DATE).equal(
                            dslConn.create.select(DSL.max(A2.field(DbTable.Account.Fields.AS_OF_LOCAL_DATE)))
                                .from(A2)
                                .where(
                                    A2.field(DbTable.Account.Fields.AS_OF_LOCAL_DATE).greaterOrEqual(inclusiveBegin)
                                        .and(
                                            A2.field(DbTable.Account.Fields.AS_OF_LOCAL_DATE).lessThan(exclusiveEnd))))));

        final AccountRecord x = _createAccountRecord(accountName, record);
        return x;
    }

    private AccountRecord
    _selectPreviousForDateTime(AccountName accountName, LocalDateTime exclusiveBeginUtcLocalDateTime)
    throws Exception {

        final String exclusiveBegin = exclusiveBeginUtcLocalDateTime.toString();

        final Record2<Long, String> record =
            dslContextService.selectExactlyOne(
                dslConn.create.select(
                    A1.field(DbTable.Account.Fields.SIGNED_BALANCE),
                    A1.field(DbTable.Account.Fields.AS_OF_LOCAL_DATE))
                    .from(A1)
                    .where(
                        A1.field(DbTable.Account.Fields.AS_OF_LOCAL_DATE).equal(
                            dslConn.create.select(DSL.max(A2.field(DbTable.Account.Fields.AS_OF_LOCAL_DATE)))
                                .from(A2)
                                .where(A2.field(DbTable.Account.Fields.AS_OF_LOCAL_DATE).lessThan(exclusiveBegin)))));

        final AccountRecord x = _createAccountRecord(accountName, record);
        return x;
    }

    private AccountRecord
    _createAccountRecord(AccountName accountName, Record2<Long, String> record)
    throws Exception {

        final long signedBalance = record.get(DbTable.Account.Fields.SIGNED_BALANCE);
        final String utcTimestampStr = record.get(DbTable.Account.Fields.AS_OF_LOCAL_DATE);
        final LocalDateTime utcTimestamp = LocalDateTime.parse(utcTimestampStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        final AccountRecord x = new AccountRecord(accountName, signedBalance, utcTimestamp);
        return x;
    }

    @Override
    public ImmutableFullEnumMap<AccountName, Result>
    selectAll(ZonedDateTime inclusiveBegin)
    throws Exception {

        final ImmutableFullEnumMap<AccountName, Result> x =
            ImmutableFullEnumMap.ofKeys2(AccountName.class,
                (AccountName accountName) -> select(accountName, inclusiveBegin));
        return x;
    }
}
