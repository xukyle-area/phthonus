package com.gantenx.phthonus.socket;


import com.fasterxml.jackson.core.JsonProcessingException;

import com.gantenx.phthonus.infrastructure.commons.enums.Market;
import com.gantenx.phthonus.infrastructure.commons.model.CryptoEvent;
import com.gantenx.phthonus.infrastructure.commons.model.CryptoRequest;
import com.gantenx.phthonus.infrastructure.commons.model.RealTimeQuote;
import com.gantenx.phthonus.infrastructure.commons.model.Symbol;
import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
public class CryptoBaseSocketClient extends BaseSocketClient {

    private final static String URL = "wss://stream.crypto.com/v2/market";
    private final static String SUBSCRIBE = "subscribe";
    private final static String CHANNELS = "channels";

    public CryptoBaseSocketClient() throws URISyntaxException {
        super(URL);
    }

    @Override
    public void onMessage(String message) {
        if (message.contains("public/heartbeat")) {
            try {
                CryptoRequest request = mapper.readValue(message, CryptoRequest.class);
                request.setMethod("public/respond-heartbeat");
                request.setNonce(System.currentTimeMillis());
                this.send(mapper.writeValueAsString(request));
            } catch (JsonProcessingException e) {
                log.info("handle heartbeat exception, message:{}", message, e);
            }
        } else {
            super.onMessage(message);
        }
    }

    @Override
    protected Consumer<String> getCallback() {
        return text -> {
            try {
                CryptoEvent cryptoEvent = mapper.readValue(text, CryptoEvent.class);
                CryptoEvent.Dat data = cryptoEvent.getResult().getData()[0];
                String symbol = cryptoEvent.getResult().getSubscription();
                RealTimeQuote realTimeQuote =
                        new RealTimeQuote(symbol, System.currentTimeMillis(), Market.CRYPTO_COM,
                                data.getLast(), data.getAsk(), data.getBid());
                writer.updateRealTimeQuote(realTimeQuote);
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
            Map<String, Object> channelsMap = Collections.singletonMap(CHANNELS, channels);
            CryptoRequest request = new CryptoRequest(id++, SUBSCRIBE, channelsMap, System.currentTimeMillis());
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
