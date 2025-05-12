package com.gantenx.phthonus.infrastructure.commons.model;

import com.gantenx.phthonus.infrastructure.commons.enums.Market;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
@AllArgsConstructor
public class RealTimeQuote {
    private String symbol;
    private long timestamp;
    @NotNull
    private Market market;
    @NotNull
    private String last;
    private String ask;
    private String bid;
}