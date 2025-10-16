package com.gantenx.phthonus.history;

import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.gantenx.phthonus.enums.Market;
import com.gantenx.phthonus.enums.Symbol;
import com.gantenx.phthonus.model.common.DayQuote;
import com.gantenx.phthonus.utils.HttpRequestBuilder;
import com.gantenx.phthonus.utils.TimestampUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.ResponseBody;

@Slf4j
@Service
@ConditionalOnProperty(name = "hashkey.history", havingValue = "true")
public class HashkeyHandler extends HistoryQuoteHandler {

    @Override
    public void handleHistory() {
        Long lastExecuteTime = executeRecordMap.getOrDefault(Market.HASHKEY, 0L);
        log.info("lastExecuteTime:{}", lastExecuteTime);
        if (this.isAfter1amUTC(lastExecuteTime)) {
            log.info("the task has been executed, lastExecuteTime:{}", lastExecuteTime);
            return;
        }
        log.info("handleHashkeyHistory start");

        Symbol[] symbols = Symbol.getAllSymbols();
        for (Symbol symbol : symbols) {
            try {

                Request request = new HttpRequestBuilder(Market.HASHKEY.getUrl()).addParam("symbol", symbol.getSymbol())
                        .addParam("interval", "1d").addParam("limit", "3").addHeader("User-Agent", "Phthonus/1.0")
                        .buildGetRequest();

                log.info("Request URL: {}",
                        new HttpRequestBuilder(Market.HASHKEY.getUrl()).addParam("symbol", symbol.name())
                                .addParam("interval", "1d").addParam("limit", "100").getFullUrl());
                ResponseBody responseBody = client.newCall(request).execute().body();
                String body = Objects.requireNonNull(responseBody).string();
                log.info("Response body: {}", body);

                // 检查响应是否为错误格式
                if (body.trim().startsWith("{")) {
                    // 可能是错误响应，尝试解析为 JSONObject
                    JSONObject errorResponse = new JSONObject(body);
                    if (errorResponse.has("code") && errorResponse.getInt("code") != 0) {
                        log.error("API returned error for symbol {}: code={}, msg={}", symbol,
                                errorResponse.getInt("code"), errorResponse.optString("msg", "Unknown error"));
                        continue; // 跳过这个symbol，处理下一个
                    }
                }

                // 检查响应是否为数组格式
                if (!body.trim().startsWith("[")) {
                    log.error("Unexpected response format for symbol {}: {}", symbol, body);
                    continue;
                }

                JSONArray data = new JSONArray(body);

                // 检查数据是否为空
                if (data.length() == 0) {
                    log.warn("No data returned for symbol {}", symbol);
                    continue;
                }

                // 处理最近的2条记录（排除最后一条可能未完成的记录）
                int dataLength = data.length();
                for (int i = 0; i < Math.min(2, dataLength - 1); i++) {
                    JSONArray candle = data.getJSONArray(dataLength - 2 + i);
                    long time = candle.getLong(0); // timestamp
                    String close = candle.getString(4); // close price
                    if (time == TimestampUtils.midnightTimestampBefore(2 - i)) {
                        DayQuote quote = new DayQuote(symbol, time, Market.HASHKEY, close);
                        log.info("handle hashkey dayQuote: {}", quote);
                    } else {
                        log.error("handle hashkey dayQuote for {} error. Expected time: {}, actual time: {}", symbol,
                                TimestampUtils.midnightTimestampBefore(2 - i), time);
                    }
                }
                executeRecordMap.put(Market.HASHKEY, System.currentTimeMillis());
            } catch (Exception e) {
                log.error("error during handle history {} quote.", symbol, e);
            }
        }
    }
}
