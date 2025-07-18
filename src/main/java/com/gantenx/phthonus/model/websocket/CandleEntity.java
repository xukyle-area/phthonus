package com.gantenx.phthonus.model.websocket;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CandleEntity {

    private String id;

    private long startTime;

    private BigDecimal close;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal open;

    private BigDecimal volume;
}
