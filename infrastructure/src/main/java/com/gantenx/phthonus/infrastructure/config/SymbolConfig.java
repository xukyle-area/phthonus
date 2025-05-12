package com.gantenx.phthonus.infrastructure.config;

import com.gantenx.phthonus.infrastructure.commons.model.Symbol;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Slf4j
public class SymbolConfig {
    @Getter
    private final static Set<String> currencies = new HashSet<>();
    @Getter
    private final static Map<String, Long> contractMap = new HashMap<>();
    @Getter
    private final static Map<Symbol, Long> symbolsMap = new HashMap<>();

    static {
        symbolsMap.put(new Symbol("BTC", "USDT"), 1L);
        symbolsMap.put(new Symbol("ETH", "USDT"), 2L);
        symbolsMap.put(new Symbol("USDT", "USD"), 3L);
        contractMap.put(new Symbol("BTC", "USDT").getSymbol(), 1L);
        contractMap.put(new Symbol("ETH", "USDT").getSymbol(), 2L);
        contractMap.put(new Symbol("USDT", "USD").getSymbol(), 3L);
        currencies.add("BTC");
        currencies.add("ETH");
        currencies.add("USDT");
        currencies.add("USD");
    }

    public static long getIdBySymbol(String symbol) {
        return contractMap.get(symbol);
    }
}
