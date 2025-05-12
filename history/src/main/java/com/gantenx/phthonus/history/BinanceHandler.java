package com.gantenx.phthonus.history;



import com.gantenx.phthonus.infrastructure.commons.enums.Market;
import com.gantenx.phthonus.infrastructure.commons.model.DayHistoryQuote;
import com.gantenx.phthonus.infrastructure.commons.utils.TimestampUtils;
import com.gantenx.phthonus.infrastructure.config.SymbolConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.json.JSONArray;

import java.util.Objects;

@Slf4j
public class BinanceHandler extends HistoryQuoteHandler {

    private static final String BINANCE_CANDLE_URL = "https://proxy-binance-api.yax.tech/api/v3/klines?interval=1d&limit=3&symbol=";

    @Override
    public void handleHistory() {
        Long lastExecuteTime = executeRecordMap.getOrDefault(Market.BINANCE, 0L);
        log.info("lastExecuteTime:{}", lastExecuteTime);
        if (this.isAfter1amUTC(lastExecuteTime)) {
            log.info("the task has been executed, lastExecuteTime:{}", lastExecuteTime);
            return;
        }
        log.info("handleBinanceHistory start");
        SymbolConfig.getContractMap().keySet().forEach((symbol) -> {
            try {
                Request request = new Request.Builder().url(BINANCE_CANDLE_URL).get().build();
                String body = Objects.requireNonNull(client.newCall(request).execute().body()).string();
                log.info("handleBinanceHistory get Response:{}", body);
                for (int i = 0; i < 2; i++) {
                    JSONArray candle = new JSONArray(body).getJSONArray(i);
                    long time = candle.getLong(0);
                    if (time == TimestampUtils.midnightTimestampBefore(2 - i)) {
                        DayHistoryQuote quote = new DayHistoryQuote(symbol, time, Market.BINANCE, candle.getString(4));
                        writer.updateDayHistoryQuote(quote);
                        log.info("save 1d crypto quote success, quote:{}", quote);
                    } else {
                        log.error("time {} error. {}", time, symbol);
                    }
                }
            } catch (Exception e) {
                log.error("error during handle history quote.", e);
            }
        });
        executeRecordMap.put(Market.BINANCE, System.currentTimeMillis());
    }
}
