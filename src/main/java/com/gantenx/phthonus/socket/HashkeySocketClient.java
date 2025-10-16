package com.gantenx.phthonus.socket;


import static com.gantenx.phthonus.constants.Constant.BINARY;
import static com.gantenx.phthonus.constants.Constant.KAFKA_TOPIC;
import static com.gantenx.phthonus.constants.Constant.MQTT_TOPIC_REALTIME_QUOTE;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;
import com.gantenx.phthonus.enums.Market;
import com.gantenx.phthonus.enums.Symbol;
import com.gantenx.phthonus.model.websocket.*;
import com.gantenx.phthonus.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HashkeySocketClient extends BaseSocketClient {

    private volatile int symbolId = 0;
    private volatile long messageId = 1;

    public HashkeySocketClient() throws URISyntaxException {
        super(Market.HASHKEY.getWebSocketUrl());
    }

    @Override
    public void onMessage(String message) {
        if (message.contains("ping")) {
            HashkeyPing request = JsonUtils.readValue(message, HashkeyPing.class);
            Long ping = request.getPing();
            HashkeyPong pong = new HashkeyPong();
            pong.setPong(ping);
            this.send(JsonUtils.toJson(pong));
        } else {
            super.onMessage(message);
        }
    }

    @Override
    protected Consumer<String> getCallback() {
        return text -> {

            log.info(text);
            try {
                HashkeyEvent hashkeyEvent = JsonUtils.readValue(text, HashkeyEvent.class);
                if(hashkeyEvent == null || hashkeyEvent.getData() == null || hashkeyEvent.getData().length == 0) {
                    return;
                }
                HashkeyEvent.Data data = hashkeyEvent.getData()[0];
                String symbol = hashkeyEvent.getSymbolName();
                Symbol symbolEnum = Symbol.findBySymbolWithSubline(symbol);
                RealTimeQuote realTimeQuote = new RealTimeQuote();
                realTimeQuote.setSymbol(symbolEnum);
                realTimeQuote.setTimestamp(System.currentTimeMillis());
                realTimeQuote.setLast(data.getClose());
                log.info("Hashkey real-time, symbol: {}, quote: {}", symbol, data.getClose());
                realTimeQuote.setMarket(Market.HASHKEY);
                kafkaSender.send(KAFKA_TOPIC, MQTT_TOPIC_REALTIME_QUOTE, realTimeQuote);
            } catch (Exception e) {
                log.error("error during sink.{}", text, e);
            }
        };
    }

    @Override
    protected String buildSubscription(Symbol[] symbols) {
        HashkeyRequest request = new HashkeyRequest();
        Map<String, Object> paramsMap = Collections.singletonMap(BINARY, false);
        request.setParams(paramsMap);
        request.setSymbol(symbols[symbolId].getSymbol());
        request.setId(messageId++);
        symbolId++;
        if (symbolId > symbols.length) {
            symbolId = 0;
        }
        return JsonUtils.toJson(request);
    }
}
