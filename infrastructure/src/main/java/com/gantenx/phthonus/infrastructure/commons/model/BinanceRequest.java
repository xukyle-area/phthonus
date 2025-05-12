package com.gantenx.phthonus.infrastructure.commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BinanceRequest {

    private String method;
    private String[] params;
    private long id;

}
