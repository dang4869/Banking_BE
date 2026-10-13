package com.me.base.enums;

import lombok.Getter;

@Getter
public enum SavingsTerm {
    ONE_MONTH(1, 3.5),
    THREE_MONTHS(3, 4.0),
    SIX_MONTHS(6, 5.5),
    TWELVE_MONTHS(12, 6.5),
    TWENTY_FOUR_MONTHS(24, 7.0);

    private final int months;
    private final double interestRate;

    SavingsTerm(int months, double interestRate) {
        this.months = months;
        this.interestRate = interestRate;
    }
}
