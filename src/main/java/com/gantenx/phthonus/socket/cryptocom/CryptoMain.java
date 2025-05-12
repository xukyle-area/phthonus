package com.gantenx.phthonus.socket.cryptocom;

import com.gantenx.phthonus.common.MARKET;
import com.gantenx.phthonus.socket.writer.QuoteHandler;
import com.gantenx.phthonus.common.ScheduledThreadPool;
import com.gantenx.phthonus.socket.client.SocketTask;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class CryptoMain {

    private final static int PERIOD = 20 * 60 * 1000;
    private final static int INITIAL_DELAY = 0;

    public static void main(String[] args) throws Exception {
        SocketTask socketTask = new SocketTask(MARKET.MARKET_CRYPTO_COM);
        socketTask.scheduleConnect();

        // 每天 1.AM 点 处理每日收盘价行情
        ScheduledThreadPool.scheduleAtFixedRate(QuoteHandler::handleCryptoHistory, INITIAL_DELAY, PERIOD, TimeUnit.MILLISECONDS);
    }
}
