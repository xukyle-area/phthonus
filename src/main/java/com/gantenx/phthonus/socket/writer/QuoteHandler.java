package com.gantenx.phthonus.socket.writer;


import com.gantenx.phthonus.common.ApiWebClientFactory;
import com.gantenx.phthonus.common.MARKET;
import com.gantenx.phthonus.socket.cache.SymbolCache;
import com.gantenx.phthonus.common.TimestampUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.gantenx.phthonus.common.TimestampUtils.MILLIS_OF_ONE_HOUR;


@Slf4j
public class QuoteHandler {
    private static final String CRYPTO_CANDLE_URL = "https://api.crypto.com/v2/public/get-candlestick?instrument_name=USDT_USD&timeframe=1d";
    private static final String BINANCE_CANDLE_URL = "https://proxy-binance-api.yax.tech/api/v3/klines?interval=1d&limit=3&symbol=";
    private static final OkHttpClient client = ApiWebClientFactory.getSharedClient();
    private static final String USDTUSD = "USDTUSD";
    private static final QuoteWriter dynamodbWriter = new QuoteWriter();
    private static final Map<MARKET, Long> executeRecordMap = new HashMap<>();

    /**
     * https://exchange-docs.crypto.com/spot/index.html#ticker-instrument_name
     */
    public static void handleCryptoHistory() {
        Long lastExecuteTime = executeRecordMap.getOrDefault(MARKET.MARKET_CRYPTO_COM, 0L);
        log.info("lastExecuteTime:{}", lastExecuteTime);
        if (QuoteHandler.isAfter1amUTC(lastExecuteTime)) {
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
                    Long contractId = SymbolCache.getIdBySymbol(USDTUSD);
                    QuoteWriter.DayHistoryQuote quote = new QuoteWriter.DayHistoryQuote(time, contractId, MARKET.MARKET_CRYPTO_COM, candle.getString("c"));
                    dynamodbWriter.updateDayHistoryQuote(quote);
                    log.info("save 1d crypto quote success, dayHistoryQuote:{}", quote);
                } else {
                    log.error("handle crypto dayHistoryQuote error.");
                }
            }
            executeRecordMap.put(MARKET.MARKET_CRYPTO_COM, System.currentTimeMillis());
        } catch (Exception e) {
            log.error("error during handle history quote.", e);
        }
    }

    /**
     * https://binance-docs.github.io/apidocs/spot/en/#kline-candlestick-data
     */
    public static void handleBinanceHistory() {
        Long lastExecuteTime = executeRecordMap.getOrDefault(MARKET.MARKET_BINANCE, 0L);
        log.info("lastExecuteTime:{}", lastExecuteTime);
        if (QuoteHandler.isAfter1amUTC(lastExecuteTime)) {
            log.info("the task has been executed, lastExecuteTime:{}", lastExecuteTime);
            return;
        }
        log.info("handleBinanceHistory start");
        SymbolCache.getContractMap().forEach((symbol, id) -> {
            try {
                Request request = new Request.Builder().url(BINANCE_CANDLE_URL + symbol).get().build();
                String body = Objects.requireNonNull(client.newCall(request).execute().body()).string();
                log.info("handleBinanceHistory get Response:{}", body);
                for (int i = 0; i < 2; i++) {
                    JSONArray candle = new JSONArray(body).getJSONArray(i);
                    long time = candle.getLong(0);
                    if (time == TimestampUtils.midnightTimestampBefore(2 - i)) {
                        QuoteWriter.DayHistoryQuote quote = new QuoteWriter.DayHistoryQuote(time, id, MARKET.MARKET_BINANCE, candle.getString(4));
                        dynamodbWriter.updateDayHistoryQuote(quote);
                        log.info("save 1d crypto quote success, quote:{}", quote);
                    } else {
                        log.error("time {} error. {}", time, symbol);
                    }
                }
            } catch (Exception e) {
                log.error("error during handle history quote.", e);
            }
        });
        executeRecordMap.put(MARKET.MARKET_BINANCE, System.currentTimeMillis());
    }

    /**
     * 判断给定的时间戳是否在当天的1am UTC之后
     *
     * @param lastExecuteTime 时间戳
     * @return 如果在当天1am UTC之后返回true，否则返回false
     */
    public static boolean isAfter1amUTC(long lastExecuteTime) {
        // 上一个1am UTC的时间戳
        long time = TimestampUtils.midnightTimestampToday() + MILLIS_OF_ONE_HOUR;
        return lastExecuteTime > time;
    }

}
