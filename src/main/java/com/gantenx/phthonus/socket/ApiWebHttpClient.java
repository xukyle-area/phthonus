package com.gantenx.phthonus.socket;

import okhttp3.OkHttpClient;

import java.io.Closeable;

public abstract class ApiWebHttpClient implements Closeable {

    protected final OkHttpClient client = ApiWebClientFactory.getSharedClient();
}
