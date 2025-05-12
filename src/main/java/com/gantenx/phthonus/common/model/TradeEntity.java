package com.gantenx.phthonus.common.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeEntity {

    private long contractId;

    private long id;

    private BigDecimal price;

    private String side;

    private long time;

    private BigDecimal volume;
}
