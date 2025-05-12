package com.gantenx.phthonus.infrastructure.dao;

import com.gantenx.phthonus.infrastructure.commons.model.DayHistoryQuote;
import com.gantenx.phthonus.infrastructure.commons.model.RealTimeQuote;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuoteWriter {

    public QuoteWriter() {
    }

    public void updateRealTimeQuote(RealTimeQuote realTimeQuote) {
        log.info("{}", realTimeQuote);
    }

    public void updateDayHistoryQuote(DayHistoryQuote dayHistoryQuote) {
        log.info("{}", dayHistoryQuote);
    }
}
