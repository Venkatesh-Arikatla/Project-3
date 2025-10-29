package com.example.cryptotracker.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinGeckoResponse {

    private String id;
    private String symbol;
    private String name;

    // ✅ Add market cap rank (top-level)
    @JsonProperty("market_cap_rank")
    private Integer marketCapRank;

    // ✅ Image map (small, large, etc.)
    private Map<String, String> image;

    // ✅ Market data object
    @JsonProperty("market_data")
    private MarketData marketData;

    // ----- Getters & Setters -----
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMarketCapRank() {
        return marketCapRank;
    }

    public void setMarketCapRank(Integer marketCapRank) {
        this.marketCapRank = marketCapRank;
    }

    public Map<String, String> getImage() {
        return image;
    }

    public void setImage(Map<String, String> image) {
        this.image = image;
    }

    public MarketData getMarketData() {
        return marketData;
    }

    public void setMarketData(MarketData marketData) {
        this.marketData = marketData;
    }

    // ----- Nested MarketData class -----
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MarketData {

        @JsonProperty("current_price")
        private Map<String, Double> currentPrice;

        @JsonProperty("market_cap")
        private Map<String, Double> marketCap;

        @JsonProperty("total_volume")
        private Map<String, Double> totalVolume;

        @JsonProperty("high_24h")
        private Map<String, Double> high24h;

        @JsonProperty("low_24h")
        private Map<String, Double> low24h;

        @JsonProperty("price_change_24h")
        private Double priceChange24h;

        @JsonProperty("price_change_percentage_24h")
        private Double priceChangePercentage24h;

        // ✅ Add circulating supply
        @JsonProperty("circulating_supply")
        private Double circulatingSupply;

        // ----- Getters & Setters -----
        public Map<String, Double> getCurrentPrice() {
            return currentPrice;
        }

        public void setCurrentPrice(Map<String, Double> currentPrice) {
            this.currentPrice = currentPrice;
        }

        public Map<String, Double> getMarketCap() {
            return marketCap;
        }

        public void setMarketCap(Map<String, Double> marketCap) {
            this.marketCap = marketCap;
        }

        public Map<String, Double> getTotalVolume() {
            return totalVolume;
        }

        public void setTotalVolume(Map<String, Double> totalVolume) {
            this.totalVolume = totalVolume;
        }

        public Map<String, Double> getHigh24h() {
            return high24h;
        }

        public void setHigh24h(Map<String, Double> high24h) {
            this.high24h = high24h;
        }

        public Map<String, Double> getLow24h() {
            return low24h;
        }

        public void setLow24h(Map<String, Double> low24h) {
            this.low24h = low24h;
        }

        public Double getPriceChange24h() {
            return priceChange24h;
        }

        public void setPriceChange24h(Double priceChange24h) {
            this.priceChange24h = priceChange24h;
        }

        public Double getPriceChangePercentage24h() {
            return priceChangePercentage24h;
        }

        public void setPriceChangePercentage24h(Double priceChangePercentage24h) {
            this.priceChangePercentage24h = priceChangePercentage24h;
        }

        public Double getCirculatingSupply() {
            return circulatingSupply;
        }

        public void setCirculatingSupply(Double circulatingSupply) {
            this.circulatingSupply = circulatingSupply;
        }
    }
}
