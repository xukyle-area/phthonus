package com.gantenx.phthonus.socket.service;

import com.gantenx.phthonus.common.ApiWebClientFactory;
import com.gantenx.phthonus.common.SymbolUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

@Slf4j
public class CurrencyService {
    private final static Set<String> currencies = new HashSet<>();
    private final static Map<String, Long> contractMap = new HashMap<>();
    private final static Map<CurrencyPair, Long> currencyPairMap = new HashMap<>();

    static {
        String url = "https://api-bos-aws2.test.exodushk.com/internal/contracts/?start=0&limit=1000";
        Request request = new Request.Builder().get().url(url).build();
        Call call = ApiWebClientFactory.getSharedClient().newCall(request);
        JSONArray contracts = new JSONArray();
        try {
            Response response = call.execute();
            assert response.body() != null;
            contracts = new JSONObject(response.body().string()).getJSONObject("data").getJSONArray("items");
        } catch (IOException e) {
            log.error("fetch contracts from api exception", e);
        }
        for (int i = 0; i < contracts.length(); i++) {
            JSONObject contract = contracts.getJSONObject(i);
            String symbol = contract.getString("symbol");
            long id = contract.getLong("id");
            String[] parts = SymbolUtils.toCurrencies(symbol);
            if (parts.length == 2) {
                String base = parts[0];
                String quote = parts[1];
                CurrencyPair currencyPair = new CurrencyPair();
                currencyPair.setQuote(quote);
                currencyPair.setBase(base);
                currencyPairMap.put(currencyPair, id);
                contractMap.put(SymbolUtils.removeDot(symbol), id);
                currencies.add(contract.getString("currency"));
            } else {
                log.error("symbol format error, symbol:{}", symbol);
            }
        }
    }

    public static Set<String> getCurrencies() {
        return currencies;
    }

    public static Map<String, Long> getContractMap() {
        return contractMap;
    }

    public static Map<CurrencyPair, Long> getCurrencyPairMap() {
        return currencyPairMap;
    }

    public static Long getIdBySymbol(String symbol) {
        return contractMap.get(symbol);
    }

    @Data
    public static class CurrencyPair {
        private String base;
        private String quote;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CurrencyPair that = (CurrencyPair) o;
            return Objects.equals(base, that.base) && Objects.equals(quote, that.quote);
        }

        @Override
        public int hashCode() {
            return Objects.hash(base, quote);
        }
    }

}
