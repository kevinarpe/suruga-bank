package com.kevinarpe.suruga_bank.web;

import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.container.ImmutableFullEnumMap;
import com.kevinarpe.suruga_bank.AccountName;
import com.kevinarpe.suruga_bank.AccountTypes;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public interface AccountsWebPageParserService {

    Result parse(String html)
    throws Exception;

    public static final class Result {

        public final ImmutableFullEnumMap<AccountName, Result.Account> accountMap;

        public Result(ImmutableFullEnumMap<AccountName, Result.Account> accountMap) {

            this.accountMap = ObjectArgs.checkNotNull(accountMap, "accountMap");
        }

        public static final class Account {

            private static final String ISO_CURRENCY = "JPY";

            public final AccountName accountName;
            /**
             * If {@code accountName.accountType} is {@link com.kevinarpe.suruga_bank.AccountType#SAVINGS}, this is
             * normally positive, but may <i>rarely</i> be negative!  Overdraw causing negative balance.
             * <p>
             * If {@code accountName.accountType} is {@link com.kevinarpe.suruga_bank.AccountType#LOAN}, this is
             * always positive.  A negative loan balance is not currently allowed.
             * <p>
             * Captain Obvious says: Zero is allowed.
             */
            public final long signedBalance;

            public final String isoCurrency;

            public Account(AccountName accountName,
                           final long signedBalance)
            throws Exception {

                this.accountName = ObjectArgs.checkNotNull(accountName, "accountName");
                this.signedBalance = AccountTypes.checkSignedBalance(accountName, signedBalance);;
                this.isoCurrency = ISO_CURRENCY;
            }
        }

    }

}
