package com.gantenx.phthonus.history;

import com.gantenx.phthonus.infrastructure.commons.enums.Market;
import com.gantenx.phthonus.infrastructure.commons.model.DayHistoryQuote;
import com.gantenx.phthonus.infrastructure.commons.utils.TimestampUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

@Slf4j
public class CryptoHandler extends HistoryQuoteHandler {

    private static final String CRYPTO_CANDLE_URL = "https://api.crypto.com/v2/public/get-candlestick?instrument_name=USDT_USD&timeframe=1d";
    private static final String USDT_USD = "USDT.USD";

    @Override
    public void handleHistory() {
        Long lastExecuteTime = executeRecordMap.getOrDefault(Market.CRYPTO_COM, 0L);
        log.info("lastExecuteTime:{}", lastExecuteTime);
        if (this.isAfter1amUTC(lastExecuteTime)) {
            log.info("the task has been executed, lastExecuteTime:{}", lastExecuteTime);
            return;
        }
        log.info("handleCryptoHistory start");
        try {
            Request request = new Request.Builder().url(CRYPTO_CANDLE_URL).get().build();
            ResponseBody responseBody = client.newCall(request).execute().body();
            String body = Objects.requireNonNull(responseBody).string();
            log.info("handleCryptoHistory get Response:{}", body);
            JSONArray data = new JSONObject(body).getJSONObject("result").getJSONArray("data");
            for (int i = 0; i < 2; i++) {
                JSONObject candle = data.getJSONObject(data.length() - 3 + i);
                long time = candle.getLong("t");
                if (time == TimestampUtils.midnightTimestampBefore(2 - i)) {
                    DayHistoryQuote quote = new DayHistoryQuote(USDT_USD, time, Market.CRYPTO_COM, candle.getString("c"));
                    writer.updateDayHistoryQuote(quote);
                    log.info("save 1d crypto quote success, dayHistoryQuote:{}", quote);
                } else {
                    log.error("handle crypto dayHistoryQuote error.");
                }
            }
            executeRecordMap.put(Market.CRYPTO_COM, System.currentTimeMillis());
        } catch (Exception e) {
            log.error("error during handle history quote.", e);
        }
    }
}
