package com.gantenx.phthonus.application.service;


import com.gantenx.phthonus.history.BinanceHandler;
import com.gantenx.phthonus.infrastructure.commons.enums.Market;
import com.gantenx.phthonus.socket.SocketTask;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class BinanceService {

    private SocketTask socketTask;

    @PostConstruct
    public void init() {
        SocketTask socketTask = new SocketTask(Market.BINANCE);
        socketTask.scheduleConnect();

        BinanceHandler binanceHandler = new BinanceHandler();
        binanceHandler.scheduleConnect();
    }
}
