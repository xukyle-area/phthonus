package com.gantenx.phthonus.infrastructure.commons.utils;

public class TimestampUtils {
    public static final long MILLIS_OF_ONE_DAY = 24 * 60 * 60 * 1000;
    public static final long MILLIS_OF_ONE_HOUR = 60 * 60 * 1000;

    public static long midnightTimestampToday() {
        return System.currentTimeMillis() / MILLIS_OF_ONE_DAY * MILLIS_OF_ONE_DAY;
    }

    /**
     * 获取 i 天前的 UTC 0点的时间戳
     */
    public static long midnightTimestampBefore(int i) {
        return midnightTimestampToday() - i * MILLIS_OF_ONE_DAY;
    }

    /**
     * 当前时间戳到 UTC 时间 n 点还有多少毫秒，n是入参
     */
    public static long millisecondsUntilUTCHour(int n) {
        long todayUTC_N = midnightTimestampToday() + MILLIS_OF_ONE_HOUR * n;
        long leftMilliseconds = todayUTC_N - System.currentTimeMillis();
        return leftMilliseconds > 0 ? leftMilliseconds : leftMilliseconds + MILLIS_OF_ONE_DAY;
    }
}
