package com.gantenx.phthonus.application.configration;

import com.gantenx.phthonus.infrastructure.commons.enums.Market;
import com.gantenx.phthonus.socket.SocketTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketConfiguration {

    @Bean
    public SocketTask binanceSocketTask() {
        return new SocketTask(Market.BINANCE);
    }

    @Bean
    public SocketTask cryptoSocketTask() {
        return new SocketTask(Market.CRYPTO_COM);
    }
}
