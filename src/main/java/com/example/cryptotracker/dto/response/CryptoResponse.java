package com.example.cryptotracker.dto.response;

import java.time.LocalDateTime;

public class CryptoResponse {
    private String id;
    private String symbol;
    private String name;
    private String image;
    private Double currentPrice;
    private Double priceChange24h;
    private Double priceChangePercentage24h;
    private Double marketCap;          // 🔥 Changed from Long → Double
    private Double totalVolume;        // 🔥 Changed from Long → Double
    private Double high24h;
    private Double low24h;
    private Integer rank;              // ✅ NEW
    private Double circulatingSupply;  // ✅ NEW
    private LocalDateTime lastUpdated;

    // ----- Getters and Setters -----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(Double currentPrice) { this.currentPrice = currentPrice; }

    public Double getPriceChange24h() { return priceChange24h; }
    public void setPriceChange24h(Double priceChange24h) { this.priceChange24h = priceChange24h; }

    public Double getPriceChangePercentage24h() { return priceChangePercentage24h; }
    public void setPriceChangePercentage24h(Double priceChangePercentage24h) { this.priceChangePercentage24h = priceChangePercentage24h; }

    public Double getMarketCap() { return marketCap; }         // 🔥 Updated type
    public void setMarketCap(Double marketCap) { this.marketCap = marketCap; }

    public Double getTotalVolume() { return totalVolume; }     // 🔥 Updated type
    public void setTotalVolume(Double totalVolume) { this.totalVolume = totalVolume; }

    public Double getHigh24h() { return high24h; }
    public void setHigh24h(Double high24h) { this.high24h = high24h; }

    public Double getLow24h() { return low24h; }
    public void setLow24h(Double low24h) { this.low24h = low24h; }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }

    public Double getCirculatingSupply() { return circulatingSupply; }
    public void setCirculatingSupply(Double circulatingSupply) { this.circulatingSupply = circulatingSupply; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public static class PriceHistory {
        private Long timestamp;
        private Double price;

        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
    }
}
