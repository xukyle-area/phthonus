package com.gantenx.phthonus.socket;

@FunctionalInterface
public interface ApiCallback {
    void onResponse(String text);
}
