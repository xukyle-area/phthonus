package com.gantenx.phthonus.infrastructure.commons.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TickerEntity {
    private BigDecimal ask;
    private BigDecimal bid;
    private BigDecimal last;
    private BigDecimal max24h;
    private BigDecimal min24h;
    private BigDecimal change24h;
    private BigDecimal changePercent24h;
    private BigDecimal vol24h;
}
