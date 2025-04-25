package com.gantenx.phthonus.socket;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public final class ApiWebClientFactory {

    private ApiWebClientFactory() {}

    private static final OkHttpClient sharedClient;

    static {
        final Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(500);
        dispatcher.setMaxRequests(500);
        sharedClient = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .pingInterval(20, TimeUnit.SECONDS)
                .build();
    }

    public static OkHttpClient getSharedClient() {
        return sharedClient;
    }


}
