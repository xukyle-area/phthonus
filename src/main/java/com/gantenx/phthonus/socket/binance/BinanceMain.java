package com.gantenx.phthonus.socket.binance;


import com.gantenx.phthonus.common.Market;
import com.gantenx.phthonus.socket.writer.QuoteHandler;
import com.gantenx.phthonus.common.ScheduledThreadPool;
import com.gantenx.phthonus.socket.client.SocketTask;

import java.util.concurrent.TimeUnit;

public class BinanceMain {
    private final static int PERIOD = 20 * 60 * 1000;
    private final static int INITIAL_DELAY = 0;

    public static void main(String... args) throws Exception {
        SocketTask socketTask = new SocketTask(Market.BINANCE);
        socketTask.scheduleConnect();

        // 每天 1.AM 点 处理每日收盘价行情
        ScheduledThreadPool.scheduleAtFixedRate(QuoteHandler::handleBinanceHistory, INITIAL_DELAY, PERIOD, TimeUnit.MILLISECONDS);
    }
}
