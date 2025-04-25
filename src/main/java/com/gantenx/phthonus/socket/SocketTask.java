package com.gantenx.phthonus.socket;

import com.gantenx.phthonus.socket.binance.BinanceSocketClient;
import com.gantenx.phthonus.socket.cryptocom.CryptoSocketClient;
import com.gantenx.phthonus.socket.service.RefreshTask;
import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SocketTask {
    private static final int CHECK_INITIAL_DELAY = 5;
    private static final int CHECK_FIXED_DELAY = 15;
    private static final int SUBSCRIPTION_INITIAL_DELAY = 5;
    private static final int SUBSCRIPTION_FIXED_DELAY = 30;
    private AbstractSocketClient curClient = null;
    private final MARKET type;

    public SocketTask(MARKET type) {
        this.type = type;
    }

    public void scheduleConnect() {
        ScheduledThreadPool.scheduleWithFixedDelay(this::checkAndReconnect, CHECK_INITIAL_DELAY, CHECK_FIXED_DELAY, TimeUnit.SECONDS);
        ScheduledThreadPool.scheduleWithFixedDelay(() -> {
            if (curClient != null && curClient.isOpen()) {
                RefreshTask.run(curClient::send, type);
            } else {
                log.info("websocket of {} is not open", type);
            }
        }, SUBSCRIPTION_INITIAL_DELAY, SUBSCRIPTION_FIXED_DELAY, TimeUnit.SECONDS);
    }

    private void checkAndReconnect() {
        if (curClient == null || !curClient.isOpen()) {
            reconnect();
        }
    }

    public synchronized void reconnect() {
        AbstractSocketClient nextClient;
        try {
            log.info("try to connect websocket of {}", type);
            if (type.equals(MARKET.MARKET_BINANCE)) {
                nextClient = new BinanceSocketClient();
            } else if (type.equals(MARKET.MARKET_CRYPTO_COM)) {
                nextClient = new CryptoSocketClient();
            } else {
                log.error("Unsupported QuoteEnum type: " + type);
                return;
            }
        } catch (URISyntaxException e) {
            log.error("URL format is invalid!", e);
            return;
        }
        log.info("build websocket client success, websocket of {}", type);
        try {
            if (curClient != null) {
                curClient.close();
            }
            nextClient.connect();
            curClient = nextClient;
        } catch (Exception e) {
            log.error("Error reconnecting: " + e.getMessage(), e);
        }
    }
}
