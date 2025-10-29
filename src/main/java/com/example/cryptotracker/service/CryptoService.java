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
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
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
                System.out.println("🔍 Searching for: " + query);
                System.out.println("✅ Using CoinGecko ID: " + coinId);
                
                CryptoData data = getCoinDataById(coinId);
                if (data != null) {
                    // Debug: Print what we received
                    System.out.println("📊 Received data - Price: $" + data.getCurrentPrice() + 
                                     ", Market Cap: $" + data.getMarketCap() +
                                     ", 24h Change: " + data.getPriceChangePercentage24h() + "%");
                    
                    cache.put(key, new CacheEntry(data));
                    return data;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ CoinGecko fetch error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("⚠️ Falling back to mock data for: " + query);
        return getDetailedMockCryptoData(query);
    }

    // ✅ Get coin market data by ID - WITH BETTER DEBUGGING
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

            System.out.println("🌐 Calling API: " + url);

            // Get raw response first to see what the API returns
            String rawResponse = restTemplate.getForObject(url, String.class);
            System.out.println("📨 Raw API Response: " + rawResponse);

            // Then parse as CryptoData[]
            CryptoData[] response = restTemplate.getForObject(url, CryptoData[].class);

            if (response != null && response.length > 0) {
                CryptoData data = response[0];
                System.out.println("✅ API returned data for: " + coinId);
                System.out.println("💰 Price: $" + data.getCurrentPrice());
                System.out.println("📈 Market Cap Rank: " + data.getMarketCapRank());
                System.out.println("📊 Full Data: " + data.toString());
                return data;
            } else {
                System.out.println("⚠️ No data returned from API for: " + coinId);
            }
        } catch (Exception e) {
            System.out.println("❌ API call failed for " + coinId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ✅ Complete coin ID mapping
    private String getCommonCoinId(String symbol) {
        Map<String, String> commonCoins = Map.ofEntries(
            Map.entry("btc", "bitcoin"),
            Map.entry("bitcoin", "bitcoin"),
            Map.entry("eth", "ethereum"),
            Map.entry("ethereum", "ethereum"),
            Map.entry("ada", "cardano"),
            Map.entry("cardano", "cardano"),
            Map.entry("doge", "dogecoin"),
            Map.entry("dogecoin", "dogecoin"),
            Map.entry("sol", "solana"),
            Map.entry("solana", "solana"),
            Map.entry("dot", "polkadot"),
            Map.entry("polkadot", "polkadot"),
            Map.entry("xrp", "ripple"),
            Map.entry("ripple", "ripple"),
            Map.entry("bnb", "binancecoin"),
            Map.entry("binancecoin", "binancecoin"),
            Map.entry("usdt", "tether"),
            Map.entry("tether", "tether"),
            Map.entry("usdc", "usd-coin"),
            Map.entry("usd-coin", "usd-coin"),
            Map.entry("bch", "bitcoin-cash"),
            Map.entry("bitcoin-cash", "bitcoin-cash"),
            Map.entry("matic", "polygon"),
            Map.entry("polygon", "polygon"),
            Map.entry("link", "chainlink"),
            Map.entry("chainlink", "chainlink"),
            Map.entry("ltc", "litecoin"),
            Map.entry("litecoin", "litecoin")
        );
        return commonCoins.get(symbol.toLowerCase());
    }

    // ✅ Scheduled job to refresh top 100 coins
    @Scheduled(fixedRate = 300000)
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

    // ✅ Get top coins
    public List<CryptoData> getTopCoins() {
        synchronized (topCoinsCache) {
            return new ArrayList<>(topCoinsCache);
        }
    }

    // ✅ Search for coin ID
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
                    String coinId = (String) coins.get(0).get("id");
                    System.out.println("🔍 Search found: " + query + " → " + coinId);
                    return coinId;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Search API error: " + e.getMessage());
        }
        return null;
    }

    // ✅ Mock data fallback
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