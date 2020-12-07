package com.kevinarpe.suruga_bank.db;

import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.kevinarpe.suruga_bank.AccountName;
import com.kevinarpe.suruga_bank.AccountTypes;

import java.time.LocalDateTime;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public final class AccountRecord {

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

    public final LocalDateTime utcTimestamp;

    public AccountRecord(AccountName accountName,
                         final long signedBalance,
                         LocalDateTime utcTimestamp)
    throws Exception {

        this.accountName = ObjectArgs.checkNotNull(accountName, "accountName");
        this.signedBalance = AccountTypes.checkSignedBalance(accountName, signedBalance);
        this.isoCurrency = Accounts.ISO_CURRENCY;
        this.utcTimestamp = ObjectArgs.checkNotNull(utcTimestamp, "utcTimestamp");
    }
}
