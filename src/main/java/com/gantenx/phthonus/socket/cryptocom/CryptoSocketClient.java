package com.gantenx.phthonus.socket.cryptocom;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.gantenx.phthonus.socket.AbstractSocketClient;
import com.gantenx.phthonus.socket.ApiCallback;
import com.gantenx.phthonus.socket.MARKET;
import com.gantenx.phthonus.socket.service.CurrencyService;
import com.gantenx.phthonus.socket.writer.QuoteWriter;
import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;

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
    protected ApiCallback getApiCallback() {
        return text -> {
            try {
                CryptoEvent cryptoEvent = objectMapper.readValue(text, CryptoEvent.class);
                CryptoEvent.Dat data = cryptoEvent.getResult().getData()[0];
                String symbol = cryptoEvent.getResult().getSubscription();
                long contractId = CurrencyService.getIdBySymbol(symbol.split("ticker.")[1].replace("_", ""));
                QuoteWriter.RealTimeQuote realTimeQuote =
                        new QuoteWriter.RealTimeQuote(System.currentTimeMillis(), contractId, MARKET.MARKET_CRYPTO_COM,
                                data.getLast(), data.getAsk(), data.getBid());
                quoteWriter.updateRealTimeQuote(realTimeQuote);
            } catch (Exception e) {
                log.error("error during sink.{}", text, e);
            }
        };
    }
}
