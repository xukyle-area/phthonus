package com.gantenx.phthonus.application.service;

import com.gantenx.phthonus.history.CryptoHandler;
import com.gantenx.phthonus.infrastructure.commons.enums.Market;
import com.gantenx.phthonus.socket.SocketTask;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class CryptoService {

    private SocketTask socketTask;

    @PostConstruct
    public void init() {
        SocketTask socketTask = new SocketTask(Market.CRYPTO_COM);
        socketTask.scheduleConnect();

        CryptoHandler cryptoHandler = new CryptoHandler();
        cryptoHandler.scheduleConnect();
    }
}
