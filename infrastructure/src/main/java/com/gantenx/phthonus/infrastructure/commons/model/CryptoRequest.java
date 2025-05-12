package com.gantenx.phthonus.infrastructure.commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoRequest {
    private Long id;
    private String method;
    private Map<String, Object> params;
    private Long nonce;
}
