package com.gantenx.phthonus.socket.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.gantenx.phthonus.socket.MARKET;
import com.gantenx.phthonus.socket.binance.BinanceRequest;
import com.gantenx.phthonus.socket.cryptocom.CryptoRequest;
import com.gantenx.phthonus.socket.service.CurrencyService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public final class SubscriptionUtils {

    private SubscriptionUtils() {
    }

    private static final ObjectMapper mapper = new ObjectMapper();
    private final static String BINANCE_SUBSCRIBE = "SUBSCRIBE";
    private final static String CRYPTO_COM_SUBSCRIBE = "subscribe";
    private final static String CRYPTO_CHANNELS = "channels";
    private static long id = 1L;

    public static String ofTickerSubscription(MARKET market, Set<CurrencyService.CurrencyPair> symbols) throws JsonProcessingException {
        log.info("build TickerSubscription market:{}, symbols:{}", market, symbols);
        if (market == MARKET.MARKET_BINANCE) {
            List<String> params = SubscriptionUtils.buildParamsForBinance(symbols);
            BinanceRequest request = new BinanceRequest(BINANCE_SUBSCRIBE, params.toArray(new String[0]), id++);
            return mapper.writeValueAsString(request);
        } else if (market == MARKET.MARKET_CRYPTO_COM) {
            List<String> channels = SubscriptionUtils.buildParamForCrypto(symbols);
            Map<String, Object> channelsMap = Collections.singletonMap(CRYPTO_CHANNELS, channels);
            CryptoRequest request = new CryptoRequest(id++, CRYPTO_COM_SUBSCRIBE, channelsMap, System.currentTimeMillis());
            return mapper.writeValueAsString(request);
        } else {
            throw new RuntimeException("cannot convert the ticker subscription");
        }
    }

    private static List<String> buildParamsForBinance(Set<CurrencyService.CurrencyPair> symbols) {
        ArrayList<String> list = new ArrayList<>();
        for (CurrencyService.CurrencyPair s : symbols) {
            String symbol = s.getBase() + s.getQuote();
            String x = symbol.toUpperCase() + "@ticker";
            list.add(x.toLowerCase());
        }
        return list;
    }

    private static List<String> buildParamForCrypto(Set<CurrencyService.CurrencyPair> symbols) {
        ArrayList<String> list = new ArrayList<>();
        for (CurrencyService.CurrencyPair s : symbols) {
            String x = "ticker." + s.getBase() + "_" + s.getQuote();
            list.add(x);
        }
        return list;
    }
}
