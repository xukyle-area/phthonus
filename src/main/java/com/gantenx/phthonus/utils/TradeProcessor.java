package com.gantenx.phthonus.utils;

import java.util.Set;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tiger.exodus.quote.proto.QuoteOuterClass;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Tuple;

@Slf4j
public class TradeProcessor {
    public static void processTrades(Set<Tuple> trades) {
        for (Tuple trade : trades) {
            try {
                processTradeData(trade);
            } catch (Exception e) {
                log.error("Error processing trade: {}", e.getMessage());
            }
        }
    }

    private static void processTradeData(Tuple trade) throws InvalidProtocolBufferException {
        byte[] bytes = trade.getBinaryElement();
        long timestamp = (long) trade.getScore();
        String formattedTime = TimestampUtils.formatTimestamp(timestamp);

        QuoteOuterClass.TradeInfo tradeInfo = QuoteOuterClass.TradeInfo.parseFrom(bytes);
        logTradeInfo(tradeInfo, formattedTime);
    }

    private static void logTradeInfo(QuoteOuterClass.TradeInfo tradeInfo, String time) {
        log.info("Trade: id: {}, price: {}, volume: {}, isBuyerMaker: {}, Time: {}", tradeInfo.getId(),
                tradeInfo.getPrice().toString().trim(), tradeInfo.getVolume().toString().trim(),
                tradeInfo.getIsBuyerMaker(), time);
    }
}
