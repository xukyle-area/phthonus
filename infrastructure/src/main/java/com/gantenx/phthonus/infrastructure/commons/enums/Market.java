package com.gantenx.phthonus.infrastructure.commons.enums;

public enum Market {
    BINANCE(1),
    CRYPTO_COM(2),
    UNRECOGNIZED(0);

    private static final Market[] VALUES = values();
    private final int value;

    public final int getNumber() {
        if (this == UNRECOGNIZED) {
            throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
        } else {
            return this.value;
        }
    }

    public static Market forNumber(int value) {
        switch (value) {
            case 1:
                return BINANCE;
            case 2:
                return CRYPTO_COM;
            default:
                return null;
        }
    }

    Market(int value) {
        this.value = value;
    }
}