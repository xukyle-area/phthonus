package com.gantenx.phthonus.socket;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gantenx.phthonus.infrastructure.commons.enums.Symbol;
import com.gantenx.phthonus.infrastructure.dao.QuoteWriter;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

@Slf4j
public abstract class BaseSocketClient extends WebSocketClient {

    protected static final ObjectMapper mapper = new ObjectMapper();
    protected static final QuoteWriter writer = new QuoteWriter();
    protected static long id = 1L;

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public BaseSocketClient(String serverUri) throws URISyntaxException {
        super(new URI(serverUri));
    }

    @Override
    public void onOpen(ServerHandshake data) {
        log.info("WebSocket connection opened!");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Connection closed by {}, Code: {}, Reason: {}", (remote ? "remote peer" : "us"), code, reason);
        log.info("WebSocket connection closed, preparing to reconnect...");
    }

    @Override
    public void onError(Exception ex) {
        log.error("Error occurred in WebSocket connection...", ex);
    }

    @Override
    public void onMessage(String message) {
        Consumer<String> callback = this.getCallback();
        callback.accept(message);
    }

    protected abstract Consumer<String> getCallback();

    protected abstract String buildSubscription(Symbol[] symbols);

    public void subscription() {
        String subscription = this.buildSubscription(Symbol.getAllSymbols());
        log.info("WebSocket subscription: {}", subscription);
        this.send(subscription);
    }
}
