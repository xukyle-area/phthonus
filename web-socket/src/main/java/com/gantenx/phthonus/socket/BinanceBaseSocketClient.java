package com.gantenx.phthonus.socket;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.gantenx.phthonus.infrastructure.commons.enums.Market;
import com.gantenx.phthonus.infrastructure.commons.enums.Symbol;
import com.gantenx.phthonus.infrastructure.commons.model.BinanceEvent;
import com.gantenx.phthonus.infrastructure.commons.model.BinanceRequest;
import com.gantenx.phthonus.infrastructure.commons.model.BinanceTicker;
import com.gantenx.phthonus.infrastructure.commons.model.RealTimeQuote;
import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class BinanceBaseSocketClient extends BaseSocketClient {

    private static final String BINANCE_URL = "wss://stream.binance.com:443/stream";
    private final static String BINANCE_SUBSCRIBE = "SUBSCRIBE";

    public BinanceBaseSocketClient() throws URISyntaxException {
        super(BINANCE_URL);
    }

    @Override
    protected Consumer<String> getCallback() {
        return text -> {
            try {
                BinanceEvent binanceEvent = mapper.readValue(text, BinanceEvent.class);
                BinanceTicker data = binanceEvent.getData();
                String symbol = data.getSymbol();
                Symbol symbolEnum = Symbol.findBySymbolWithoutDot(symbol);
                RealTimeQuote realTimeQuote = new RealTimeQuote(symbolEnum, data.getEventTime(), Market.BINANCE,
                                data.getLastTradedPrice(), data.getBestAskPrice(), data.getBestBidPrice());
                writer.updateRealTimeQuote(realTimeQuote);
            } catch (Exception e) {
                log.error("error during sink.{}", text, e);
            }
        };
    }

    @Override
    protected String buildSubscription(Symbol[] symbols) {
        try {
            List<String> list = new ArrayList<>();
            for (Symbol s : symbols) {
                String symbol = s.getBase() + s.getQuote();
                String x = symbol.toUpperCase() + "@ticker";
                list.add(x.toLowerCase());
            }
            BinanceRequest request = new BinanceRequest(BINANCE_SUBSCRIBE, list.toArray(new String[0]), id++);
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
