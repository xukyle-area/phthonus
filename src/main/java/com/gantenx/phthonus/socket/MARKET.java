package com.gantenx.phthonus.socket;

public enum MARKET {
    MARKET_UNSPECIFIED(0),
    MARKET_BINANCE(1),
    MARKET_FTX(2),
    MARKET_EXODUS(3),
    MARKET_CRYPTO_COM(4),
    MARKET_TIGR(5),
    UNRECOGNIZED(-1);


    private static final MARKET[] VALUES = values();
    private final int value;

    public final int getNumber() {
        if (this == UNRECOGNIZED) {
            throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
        } else {
            return this.value;
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static MARKET valueOf(int value) {
        return forNumber(value);
    }

    public static MARKET forNumber(int value) {
        switch (value) {
            case 0:
                return MARKET_UNSPECIFIED;
            case 1:
                return MARKET_BINANCE;
            case 2:
                return MARKET_FTX;
            case 3:
                return MARKET_EXODUS;
            case 4:
                return MARKET_CRYPTO_COM;
            case 5:
                return MARKET_TIGR;
            default:
                return null;
        }
    }



    MARKET(int value) {
        this.value = value;
    }
}