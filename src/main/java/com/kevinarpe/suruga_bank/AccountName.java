package com.kevinarpe.suruga_bank;

import com.google.common.collect.ImmutableBiMap;
import com.googlecode.kevinarpe.papaya.argument.ObjectArgs;
import com.googlecode.kevinarpe.papaya.argument.StringArgs;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public enum AccountName {

    REGULAR_SAVINGS(
        AccountType.SAVINGS,
        "普通預金"),

    TIME_DEPOSIT_SAVINGS(
        AccountType.SAVINGS,
        "定期預金"),

    ACCUM_TIME_DEPOSIT_SAVINGS(
        AccountType.SAVINGS,
        "積立定期預金"),

    SAVINGS_SAVINGS(
        AccountType.SAVINGS,
        "貯蓄預金"),

    MORTGAGE_LOAN(
        AccountType.LOAN,
        "ローン"),

    CARD_LOAN(
        AccountType.LOAN,
        "カードローン"),
    ;

    public static ImmutableBiMap<AccountName, String> TO_JAPANESE_TEXT_BIMAP =
        ImmutableBiMap.copyOf(
            Arrays.stream(values())
                .collect(
                    Collectors.toMap(
                        v -> v,
                        v -> v.japaneseText)));

    public final AccountType accountType;
    public final String japaneseText;

    private AccountName(AccountType accountType,
                        String japaneseText) {

        this.accountType = ObjectArgs.checkNotNull(accountType, "accountType");
        this.japaneseText = StringArgs.checkNotEmptyOrWhitespace(japaneseText, "japaneseText");
    }
}
