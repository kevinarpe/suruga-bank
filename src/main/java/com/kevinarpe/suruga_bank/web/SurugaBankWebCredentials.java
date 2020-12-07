package com.kevinarpe.suruga_bank.web;

import com.googlecode.kevinarpe.papaya.argument.StringArgs;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public final class SurugaBankWebCredentials {

    /**
     * Japanese: @code {"店番号"}
     */
    public final String branchNumber;
    /**
     * Japanese: @code {"口座番号"}
     */
    public final String accountNumber;
    /**
     * Japanese: @code {"キャッシュカード暗証番号"}
     */
    public final String cashCardPasswordNumber;
    /**
     * Japanese: @code {"口座名義（全角カナ）"}
     */
    public final String accountHolderName;

    public SurugaBankWebCredentials(String branchNumber,
                                    String accountNumber,
                                    String cashCardPasswordNumber,
                                    String accountHolderName) {

        this.branchNumber = StringArgs.checkNotEmptyOrWhitespace(branchNumber, "branchNumber");
        this.accountNumber = StringArgs.checkNotEmptyOrWhitespace(accountNumber, "accountNumber");
        this.cashCardPasswordNumber =
            StringArgs.checkNotEmptyOrWhitespace(cashCardPasswordNumber, "cashCardPasswordNumber");

        this.accountHolderName = StringArgs.checkNotEmptyOrWhitespace(accountHolderName, "accountHolderName");
    }
}
