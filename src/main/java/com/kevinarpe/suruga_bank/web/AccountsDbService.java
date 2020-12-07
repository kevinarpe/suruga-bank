package com.kevinarpe.suruga_bank.web;

import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.container.ImmutableFullEnumMap;
import com.kevinarpe.suruga_bank.AccountName;
import com.kevinarpe.suruga_bank.db.AccountRecord;

import java.time.ZonedDateTime;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public interface AccountsDbService {

    void insert(ZonedDateTime asOf,
                ImmutableFullEnumMap<AccountName, AccountsWebPageParserService.Result.Account> accountMap)
    throws Exception;

    public static final class Result {

        public final AccountRecord current;
        public final AccountRecord previous;

        public Result(AccountRecord current, AccountRecord previous) {

            this.current = ObjectArgs.checkNotNull(current, "current");
            this.previous = ObjectArgs.checkNotNull(previous, "previous");
        }
    }

    Result select(AccountName accountName, ZonedDateTime inclusiveBegin)
    throws Exception;

    ImmutableFullEnumMap<AccountName, Result>
    selectAll(ZonedDateTime inclusiveBegin)
    throws Exception;
}
