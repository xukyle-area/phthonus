package com.gantenx.phthonus.socket.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.gantenx.phthonus.common.MARKET;
import com.gantenx.phthonus.common.model.Symbol;
import com.gantenx.phthonus.socket.cache.SymbolCache;
import com.gantenx.phthonus.socket.cryptocom.CryptoEvent;
import com.gantenx.phthonus.socket.cryptocom.CryptoRequest;
import com.gantenx.phthonus.socket.writer.QuoteWriter;
import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
public class CryptoSocketClient extends AbstractSocketClient {

    private final static String CRYPTO_URL = "wss://stream.crypto.com/v2/market";

    public CryptoSocketClient() throws URISyntaxException {
        super(CRYPTO_URL);
    }

    @Override
    public void onMessage(String message) {
        if (message.contains("public/heartbeat")) {
            try {
                CryptoRequest request = objectMapper.readValue(message, CryptoRequest.class);
                request.setMethod("public/respond-heartbeat");
                request.setNonce(System.currentTimeMillis());
                this.send(objectMapper.writeValueAsString(request));
            } catch (JsonProcessingException e) {
                log.info("handle heartbeat exception, message:{}", message, e);
            }
        } else {
            super.onMessage(message);
        }
    }

    @Override
    protected Consumer<String> getApiCallback() {
        return text -> {
            try {
                CryptoEvent cryptoEvent = objectMapper.readValue(text, CryptoEvent.class);
                CryptoEvent.Dat data = cryptoEvent.getResult().getData()[0];
                String symbol = cryptoEvent.getResult().getSubscription();
                long contractId = SymbolCache.getIdBySymbol(symbol.split("ticker.")[1].replace("_", ""));
                QuoteWriter.RealTimeQuote realTimeQuote =
                        new QuoteWriter.RealTimeQuote(System.currentTimeMillis(), contractId, MARKET.MARKET_CRYPTO_COM,
                                data.getLast(), data.getAsk(), data.getBid());
                quoteWriter.updateRealTimeQuote(realTimeQuote);
            } catch (Exception e) {
                log.error("error during sink.{}", text, e);
            }
        };
    }

    @Override
    protected String buildSubscription(Set<Symbol> symbols) {
        try {
            ArrayList<String> channels = new ArrayList<>();
            for (Symbol s : symbols) {
                String x = "ticker." + s.getBase() + "_" + s.getQuote();
                channels.add(x);
            }
            Map<String, Object> channelsMap = Collections.singletonMap(CRYPTO_CHANNELS, channels);
            CryptoRequest request = new CryptoRequest(id++, CRYPTO_COM_SUBSCRIBE, channelsMap, System.currentTimeMillis());
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
