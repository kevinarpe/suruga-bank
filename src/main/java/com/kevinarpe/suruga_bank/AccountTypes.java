package com.kevinarpe.suruga_bank;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public final class AccountTypes {

    private AccountTypes() {}

    public static long
    checkSignedBalance(AccountName accountName, final long signedBalance)
    throws Exception {

        // Intentional: All other accounts: Neg/Zero/Pos all OK!

        if (AccountType.LOAN.equals(accountName.accountType) && signedBalance < 0) {

            final String msg = "Loan account [" + accountName.name() + ":" + accountName.japaneseText
                + "] is negative: " + signedBalance;
            throw new Exception(msg);
        }
        return signedBalance;
    }
}
