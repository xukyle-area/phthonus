package com.gantenx.phthonus.socket.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gantenx.phthonus.common.model.Symbol;
import com.gantenx.phthonus.socket.cache.SymbolCache;
import com.gantenx.phthonus.socket.writer.QuoteWriter;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
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
        log.info("WebSocket 连接已打开!");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Connection closed by {}, Code: {}, Reason: {}", (remote ? "remote peer" : "us"), code, reason);
        log.info("WebSocket连接已关闭, 准备重新连接...");
    }

    @Override
    public void onError(Exception ex) {
        log.error("WebSocket连接发生错误...", ex);
    }

    @Override
    public void onMessage(String message) {
        Consumer<String> callback = this.getCallback();
        callback.accept(message);
    }

    protected abstract Consumer<String> getCallback();

    protected abstract String buildSubscription(Set<Symbol> symbols);

    public void subscription() {
        Set<Symbol> symbols = SymbolCache.getSymbolsMap().keySet();
        String subscription = buildSubscription(symbols);
        log.info("WebSocket 订阅消息: {}", subscription);
        this.send(subscription);
    }
}
