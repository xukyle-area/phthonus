package com.gantenx.phthonus.history;

import com.gantenx.phthonus.infrastructure.commons.enums.Market;
import com.gantenx.phthonus.infrastructure.commons.utils.HttpFactory;
import com.gantenx.phthonus.infrastructure.commons.utils.ThreadPool;
import com.gantenx.phthonus.infrastructure.commons.utils.TimestampUtils;
import com.gantenx.phthonus.infrastructure.dao.QuoteWriter;
import okhttp3.OkHttpClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.gantenx.phthonus.infrastructure.commons.utils.TimestampUtils.MILLIS_OF_ONE_HOUR;


public abstract class HistoryQuoteHandler {
    private final static int PERIOD = 20 * 60 * 1000;
    protected static final OkHttpClient client = HttpFactory.getInstance().getSharedClient();
    protected static final QuoteWriter writer = new QuoteWriter();
    protected static final Map<Market, Long> executeRecordMap = new HashMap<>();


    public HistoryQuoteHandler() {
        this.scheduleConnect();
    }

    /**
     * 判断给定的时间戳是否在当天的1am UTC之后
     *
     * @param lastExecuteTime 时间戳
     * @return 如果在当天1am UTC之后返回true，否则返回false
     */
    protected boolean isAfter1amUTC(long lastExecuteTime) {
        // 上一个1am UTC的时间戳
        long time = TimestampUtils.midnightTimestampToday() + MILLIS_OF_ONE_HOUR;
        return lastExecuteTime > time;
    }

    public abstract void handleHistory();

    public void scheduleConnect() {
        this.handleHistory();
        long initialDelay = TimestampUtils.millisecondsUntilUTCHour(1);
        ThreadPool.scheduleWithFixedDelay(this::handleHistory, initialDelay, PERIOD, TimeUnit.SECONDS);
    }
}
