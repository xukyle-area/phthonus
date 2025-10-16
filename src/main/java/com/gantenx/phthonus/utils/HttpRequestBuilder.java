package com.gantenx.phthonus.utils;

import java.util.HashMap;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * HTTP 请求构建工具类
 */
public class HttpRequestBuilder {

    private final String baseUrl;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;

    public HttpRequestBuilder(String baseUrl) {
        this.baseUrl = baseUrl;
        this.queryParams = new HashMap<>();
        this.headers = new HashMap<>();
    }

    /**
     * 添加查询参数
     */
    public HttpRequestBuilder addParam(String key, String value) {
        if (value != null) {
            this.queryParams.put(key, value);
        }
        return this;
    }

    /**
     * 添加多个查询参数
     */
    public HttpRequestBuilder addParams(Map<String, String> params) {
        if (params != null) {
            this.queryParams.putAll(params);
        }
        return this;
    }

    /**
     * 添加请求头
     */
    public HttpRequestBuilder addHeader(String key, String value) {
        if (value != null) {
            this.headers.put(key, value);
        }
        return this;
    }

    /**
     * 构建 GET 请求
     */
    public Request buildGetRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder();

        // 添加查询参数
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }

        Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.build()).get();

        // 添加请求头
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }

        return requestBuilder.build();
    }

    /**
     * 获取构建的完整 URL（用于调试）
     */
    public String getFullUrl() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        return urlBuilder.build().toString();
    }
}
