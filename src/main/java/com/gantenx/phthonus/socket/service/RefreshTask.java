package com.gantenx.phthonus.socket.service;

import com.gantenx.phthonus.common.MARKET;
import com.gantenx.phthonus.socket.client.SubscriptionUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class RefreshTask {

    public static void run(Consumer<String> send, MARKET market) {
        try {
            String sub = SubscriptionUtils.ofTickerSubscription(market, CurrencyService.getCurrencyPairMap().keySet());
            send.accept(sub);
        } catch (Throwable e) {
            log.error("send subscription exception, market:{}", market, e);
        }
    }
}
