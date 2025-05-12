package com.gantenx.phthonus.socket.binance;


import com.gantenx.phthonus.socket.client.AbstractSocketClient;
import com.gantenx.phthonus.socket.client.ApiCallback;
import com.gantenx.phthonus.common.MARKET;
import com.gantenx.phthonus.socket.service.CurrencyService;
import com.gantenx.phthonus.socket.writer.QuoteWriter;
import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;

@Slf4j
public class BinanceSocketClient extends AbstractSocketClient {

//    private static final String BINANCE_URL = "wss://proxy-binance-stream.yax.tech/stream";
//    private static final String BINANCE_URL = "wss://stream.binance.com:443/stream";
    private static final String BINANCE_URL = "wss://stream.binance.com:9443/stream";

    public BinanceSocketClient() throws URISyntaxException {
        super(BINANCE_URL);
    }

    @Override
    protected ApiCallback getApiCallback() {
        return text -> {
            try {
                BinanceEvent binanceEvent = objectMapper.readValue(text, BinanceEvent.class);
                BinanceTicker data = binanceEvent.getData();
                String symbol = data.getSymbol();
                long contractId = CurrencyService.getIdBySymbol(symbol);
                QuoteWriter.RealTimeQuote realTimeQuote =
                        new QuoteWriter.RealTimeQuote(data.getEventTime(), contractId, MARKET.MARKET_BINANCE,
                                data.getLastTradedPrice(), data.getBestAskPrice(), data.getBestBidPrice());
                quoteWriter.updateRealTimeQuote(realTimeQuote);
            } catch (Exception e) {
                log.error("error during sink.{}", text, e);
            }
        };
    }
}
