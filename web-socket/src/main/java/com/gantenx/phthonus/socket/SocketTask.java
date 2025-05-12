package com.gantenx.phthonus.socket;

import com.gantenx.phthonus.infrastructure.commons.enums.Market;
import com.gantenx.phthonus.infrastructure.commons.utils.ThreadPool;
import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SocketTask {
    private static final int INITIAL_DELAY = 5;
    private static final int FIXED_DELAY = 15;
    private BaseSocketClient currentClient = null;
    private final Market type;

    public SocketTask(Market type) {
        this.type = type;
    }

    public void scheduleConnect() {
        ThreadPool.scheduleWithFixedDelay(this::checkAndHandle, INITIAL_DELAY, FIXED_DELAY, TimeUnit.SECONDS);
    }

    private void checkAndHandle() {
        if (currentClient == null || !currentClient.isOpen()) {
            this.reconnect();
        } else {
            currentClient.subscription();
        }
    }

    public synchronized void reconnect() {
        BaseSocketClient nextClient;
        try {
            log.info("try to connect websocket of {}", type);
            if (type.equals(Market.BINANCE)) {
                nextClient = new BinanceBaseSocketClient();
            } else if (type.equals(Market.CRYPTO_COM)) {
                nextClient = new CryptoBaseSocketClient();
            } else {
                log.error("Unsupported QuoteEnum type: {}", type);
                return;
            }
        } catch (URISyntaxException e) {
            log.error("URL format is invalid!", e);
            return;
        }
        log.info("build websocket client success, websocket of {}", type);
        try {
            if (currentClient != null) {
                currentClient.close();
            }
            nextClient.connect();
            currentClient = nextClient;
        } catch (Exception e) {
            log.error("Error reconnecting: {}", e.getMessage(), e);
        }
    }
}