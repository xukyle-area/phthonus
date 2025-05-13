package com.gantenx.phthonus.history;


import com.gantenx.phthonus.infrastructure.commons.enums.Market;
import com.gantenx.phthonus.infrastructure.commons.enums.Symbol;
import com.gantenx.phthonus.infrastructure.commons.model.DayHistoryQuote;
import com.gantenx.phthonus.infrastructure.commons.utils.TimestampUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.json.JSONArray;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "binance.history", name = "enabled", havingValue = "true")
public class BinanceHandler extends HistoryQuoteHandler {

    private static final String BINANCE_CANDLE_URL = "https://api.binance.com/api/v3/klines?interval=1d&limit=3&symbol=";

    @Override
    public void handleHistory() {
        Long lastExecuteTime = executeRecordMap.getOrDefault(Market.BINANCE, 0L);
        log.info("lastExecuteTime:{}", lastExecuteTime);
        if (this.isAfter1amUTC(lastExecuteTime)) {
            log.info("the task has been executed, lastExecuteTime:{}", lastExecuteTime);
            return;
        }
        log.info("handleBinanceHistory start");
        Symbol[] symbols = Symbol.getAllSymbols();
        for (Symbol symbol : symbols) {
            try {
                log.info("handleBinanceHistory symbol:{}", symbol);
                Request request = new Request.Builder().url(BINANCE_CANDLE_URL + symbol.getSymbol()).get().build();
                String body = Objects.requireNonNull(client.newCall(request).execute().body()).string();
                for (int i = 0; i < 2; i++) {
                    JSONArray candle = new JSONArray(body).getJSONArray(i);
                    long time = candle.getLong(0);
                    if (time == TimestampUtils.midnightTimestampBefore(2 - i)) {
                        DayHistoryQuote quote = new DayHistoryQuote(symbol, time, Market.BINANCE, candle.getString(4));
                        writer.updateDayHistoryQuote(quote);
                    } else {
                        log.error("time {} error. {}", time, symbol);
                    }
                }
            } catch (Exception e) {
                log.error("error during handle history quote. {}", symbol);
            }
        }
        executeRecordMap.put(Market.BINANCE, System.currentTimeMillis());
    }
}
