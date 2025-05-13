package com.gantenx.phthonus.application.configration;

import com.gantenx.phthonus.history.BinanceHandler;
import com.gantenx.phthonus.history.CryptoHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HistoryConfiguration {

    @Bean
    public CryptoHandler cryptoHandler() {
        return new CryptoHandler();
    }


    @Bean
    public BinanceHandler binanceHandler() {
        return new BinanceHandler();
    }
}
