package com.gantenx.phthonus.socket.client;

@FunctionalInterface
public interface ApiCallback {
    void onResponse(String text);
}
