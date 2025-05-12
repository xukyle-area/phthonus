package com.gantenx.phthonus.socket.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.gantenx.phthonus.common.MARKET;
import com.gantenx.phthonus.common.model.Symbol;
import com.gantenx.phthonus.socket.binance.BinanceEvent;
import com.gantenx.phthonus.socket.binance.BinanceRequest;
import com.gantenx.phthonus.socket.binance.BinanceTicker;
import com.gantenx.phthonus.socket.cache.SymbolCache;
import com.gantenx.phthonus.socket.writer.QuoteWriter;
import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
public class BinanceSocketClient extends AbstractSocketClient {

    //    private static final String BINANCE_URL = "wss://stream.binance.com:443/stream";
    private static final String BINANCE_URL = "wss://stream.binance.com:9443/stream";

    public BinanceSocketClient() throws URISyntaxException {
        super(BINANCE_URL);
    }

    @Override
    protected Consumer<String> getApiCallback() {
        return text -> {
            try {
                BinanceEvent binanceEvent = objectMapper.readValue(text, BinanceEvent.class);
                BinanceTicker data = binanceEvent.getData();
                String symbol = data.getSymbol();
                long contractId = SymbolCache.getIdBySymbol(symbol);
                QuoteWriter.RealTimeQuote realTimeQuote =
                        new QuoteWriter.RealTimeQuote(data.getEventTime(), contractId, MARKET.MARKET_BINANCE,
                                data.getLastTradedPrice(), data.getBestAskPrice(), data.getBestBidPrice());
                quoteWriter.updateRealTimeQuote(realTimeQuote);
            } catch (Exception e) {
                log.error("error during sink.{}", text, e);
            }
        };
    }

    @Override
    protected String buildSubscription(Set<Symbol> symbols) {
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
