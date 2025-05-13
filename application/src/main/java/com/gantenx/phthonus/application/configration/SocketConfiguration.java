package com.gantenx.phthonus.application.configration;

import com.gantenx.phthonus.infrastructure.commons.enums.Market;
import com.gantenx.phthonus.socket.SocketTask;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "binance.socket", name = "enabled", havingValue = "true")
    public SocketTask binanceSocketTask() {
        return new SocketTask(Market.BINANCE);
    }

    @Bean
    @ConditionalOnProperty(prefix = "crypto.socket", name = "enabled", havingValue = "true")
    public SocketTask cryptoSocketTask() {
        return new SocketTask(Market.CRYPTO_COM);
    }
}
