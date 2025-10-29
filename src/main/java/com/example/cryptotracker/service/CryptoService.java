
package com.example.cryptotracker.service;

import com.example.cryptotracker.model.CryptoData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CryptoService {

    @Value("${coingecko.api.base-url:https://api.coingecko.com/api/v3}")
    private String apiBaseUrl;

    private final RestTemplate restTemplate;

    // ✅ Cache for individual coin data
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    // ✅ Cache for top 100 coins
    private final List<CryptoData> topCoinsCache = Collections.synchronizedList(new ArrayList<>());

    private static class CacheEntry {
        CryptoData data;
        Instant timestamp;
        CacheEntry(CryptoData data) {
            this.data = data;
            this.timestamp = Instant.now();
        }
    }

    public CryptoService() {
        this.restTemplate = new RestTemplate();
    }

    // ✅ Fetch crypto data (with caching)
    public CryptoData getCryptoData(String query) {
        String key = query.toLowerCase();

        // Serve from cache if fresh (less than 60s old)
        CacheEntry cached = cache.get(key);
        if (cached != null && Instant.now().minusSeconds(60).isBefore(cached.timestamp)) {
            System.out.println("⚡ Serving from cache for: " + query);
            return cached.data;
        }

        try {
            String coinId = getCommonCoinId(query);
            if (coinId == null) {
                coinId = getCoinIdFromSearch(query);
            }

            if (coinId != null) {
                CryptoData data = getCoinDataById(coinId);
                if (data != null) {
                    cache.put(key, new CacheEntry(data));
                    return data;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ CoinGecko fetch error: " + e.getMessage());
        }

        return getDetailedMockCryptoData(query);
    }

    // ✅ Scheduled job to refresh top 100 coins every 5 minutes
    @Scheduled(fixedRate = 300000) // 5 minutes = 300,000 ms
    public void updateTopCoins() {
        try {
            System.out.println("🌐 Fetching top 100 coins from CoinGecko...");

            String url = UriComponentsBuilder.fromHttpUrl(apiBaseUrl + "/coins/markets")
                    .queryParam("vs_currency", "usd")
                    .queryParam("order", "market_cap_desc")
                    .queryParam("per_page", "100")
                    .queryParam("page", "1")
                    .queryParam("sparkline", "false")
                    .queryParam("price_change_percentage", "24h")
                    .build()
                    .toUriString();

            CryptoData[] response = restTemplate.getForObject(url, CryptoData[].class);

            if (response != null && response.length > 0) {
                synchronized (topCoinsCache) {
                    topCoinsCache.clear();
                    topCoinsCache.addAll(Arrays.asList(response));
                }
                System.out.println("✅ Updated top coins cache (" + response.length + " coins)");
            } else {
                System.out.println("⚠️ No coins returned from API.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error updating top coins: " + e.getMessage());
        }
    }

    // ✅ Endpoint helper to get cached top 100 coins
    public List<CryptoData> getTopCoins() {
        synchronized (topCoinsCache) {
            return new ArrayList<>(topCoinsCache);
        }
    }

    // 🔍 Search CoinGecko for coin ID
    private String getCoinIdFromSearch(String query) {
        try {
            String searchUrl = UriComponentsBuilder.fromHttpUrl(apiBaseUrl + "/search")
                    .queryParam("query", query)
                    .build()
                    .toUriString();

            Map<String, Object> response = restTemplate.getForObject(searchUrl, Map.class);

            if (response != null && response.containsKey("coins")) {
                List<Map<String, Object>> coins = (List<Map<String, Object>>) response.get("coins");
                if (!coins.isEmpty()) {
                    return (String) coins.get(0).get("id");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Search API error: " + e.getMessage());
        }
        return null;
    }

    // 🧩 Get coin market data by ID
    private CryptoData getCoinDataById(String coinId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(apiBaseUrl + "/coins/markets")
                    .queryParam("vs_currency", "usd")
                    .queryParam("ids", coinId)
                    .queryParam("order", "market_cap_desc")
                    .queryParam("per_page", "1")
                    .queryParam("page", "1")
                    .queryParam("sparkline", "false")
                    .queryParam("price_change_percentage", "24h")
                    .build()
                    .toUriString();

            CryptoData[] response = restTemplate.getForObject(url, CryptoData[].class);

            if (response != null && response.length > 0) {
                return response[0];
            }
        } catch (Exception e) {
            System.out.println("❌ Data fetch error: " + e.getMessage());
        }
        return null;
    }

    // 🔠 Common coin ID mapping
    public String getCommonCoinId(String symbol) {
        Map<String, String> commonCoins = new HashMap<>();
        commonCoins.put("btc", "bitcoin");
        commonCoins.put("bitcoin", "bitcoin");
        commonCoins.put("eth", "ethereum");
        commonCoins.put("ethereum", "ethereum");
        commonCoins.put("ada", "cardano");
        commonCoins.put("doge", "dogecoin");
        commonCoins.put("sol", "solana");
        commonCoins.put("dot", "polkadot");
        commonCoins.put("xrp", "ripple");
        commonCoins.put("matic", "matic-network");
        commonCoins.put("polygon", "matic-network");
        return commonCoins.get(symbol.toLowerCase());
    }

    // 🧪 Fallback mock data
    private CryptoData getDetailedMockCryptoData(String symbol) {
        CryptoData crypto = new CryptoData();
        String commonId = getCommonCoinId(symbol);

        crypto.setId(commonId != null ? commonId : symbol.toLowerCase());
        crypto.setSymbol(symbol.toLowerCase());
        crypto.setName((commonId != null ? commonId : symbol).toUpperCase());
        crypto.setCurrentPrice(0.00);
        crypto.setMarketCap(0.00);
        crypto.setMarketCapRank(0);
        crypto.setTotalVolume(0.00);
        crypto.setHigh24h(0.00);
        crypto.setLow24h(0.00);
        crypto.setPriceChange24h(0.00);
        crypto.setPriceChangePercentage24h(0.00);
        crypto.setCirculatingSupply(0.00);
        return crypto;
    }
}
