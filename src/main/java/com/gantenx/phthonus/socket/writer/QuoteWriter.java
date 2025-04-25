package com.gantenx.phthonus.socket.writer;

import com.gantenx.phthonus.socket.MARKET;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class QuoteWriter {

    public QuoteWriter() {
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class RealTimeQuote {
        private long timestamp;
        private long contractId;
        @NotNull
        private MARKET market;
        @NotNull
        private String last;
        private String ask;
        private String bid;
    }

    public void updateRealTimeQuote(RealTimeQuote realTimeQuote) {
        log.info("{}", realTimeQuote);
    }

    @Data
    @AllArgsConstructor
    public static class DayHistoryQuote {
        private long timestamp;
        private long contractId;
        @NotNull
        private MARKET market;
        @NotNull
        private String preClose;
    }

    public void updateDayHistoryQuote(DayHistoryQuote dayHistoryQuote) {
        log.info("{}", dayHistoryQuote);
    }
}
