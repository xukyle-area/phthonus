package com.gantenx.phthonus.model.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HashkeyEvent {

    private String symbol;
    private String symbolName;
    private String topic;
    private Params params;
    private Data[] data;
    private boolean f;
    private long sendTime;
    private String channelId;
    private boolean shared;
    private String id;

    @lombok.Data
    public static class Params {
        private String realtimeInterval;
        private String binary;
    }

    @lombok.Data
    public static class Data {
        @JsonProperty("t")
        private long timestamp;
        @JsonProperty("s")
        private String symbol;
        @JsonProperty("sn")
        private String symbolName;
        @JsonProperty("c")
        private String close;
        @JsonProperty("h")
        private String high;
        @JsonProperty("l")
        private String low;
        @JsonProperty("o")
        private String open;
        @JsonProperty("v")
        private String volume;
        @JsonProperty("qv")
        private String quoteVolume;
        @JsonProperty("m")
        private String change;
        @JsonProperty("e")
        private int exchange;
    }
}