package com.gantenx.phthonus.enums;

import lombok.Getter;

@Getter
public enum Market {
    BINANCE(1, "wss://stream.binance.com:443/stream", "https://api.binance.com/api/v3/klines?interval=1d&limit=3&symbol="),
    CRYPTO_COM(2, "wss://stream.crypto.com/v2/market", "https://api.crypto.com/v2/public/get-candlestick?timeframe=1d&instrument_name="),
    UNRECOGNIZED(0, "", "");

    private final int value;
    private final String url;
    private final String webSocketUrl;

    Market(int value, String webSocketUrl, String url) {
        this.url = url;
        this.value = value;
        this.webSocketUrl = webSocketUrl;
    }
}