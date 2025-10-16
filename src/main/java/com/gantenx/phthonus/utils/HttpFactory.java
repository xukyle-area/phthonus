package com.gantenx.phthonus.utils;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

@Getter
public final class HttpFactory {

    private static volatile HttpFactory instance;

    private final OkHttpClient sharedClient;

    private HttpFactory() {
        final Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(500);
        dispatcher.setMaxRequests(500);
        sharedClient = new OkHttpClient.Builder().dispatcher(dispatcher).pingInterval(20, TimeUnit.SECONDS).build();
    }

    public static HttpFactory getInstance() {
        if (instance == null) {
            synchronized (HttpFactory.class) {
                if (instance == null) {
                    instance = new HttpFactory();
                }
            }
        }
        return instance;
    }

    /**
     * 构建带查询参数的 URL
     * @param baseUrl 基础 URL
     * @param params 查询参数
     * @return 完整的 HttpUrl
     */
    public static HttpUrl buildUrlWithParams(String baseUrl, Map<String, String> params) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder();
        
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        
        return builder.build();
    }

    /**
     * 构建带单个查询参数的 URL
     * @param baseUrl 基础 URL
     * @param key 参数名
     * @param value 参数值
     * @return 完整的 HttpUrl
     */
    public static HttpUrl buildUrlWithParam(String baseUrl, String key, String value) {
        return HttpUrl.parse(baseUrl)
            .newBuilder()
            .addQueryParameter(key, value)
            .build();
    }
}
