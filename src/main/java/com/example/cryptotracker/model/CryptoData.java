package com.example.cryptotracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoData {
    private String id;
    private String symbol;
    private String name;
    private String image;
    
    @JsonProperty("current_price")
    private double currentPrice;
    
    @JsonProperty("market_cap")
    private double marketCap;
    
    @JsonProperty("rank")  // CHANGED FROM "market_cap_rank" TO "rank"
    private int marketCapRank;
    
    @JsonProperty("total_volume")
    private double totalVolume;
    
    @JsonProperty("high_24h")
    private double high24h;
    
    @JsonProperty("low_24h")
    private double low24h;
    
    @JsonProperty("price_change_24h")
    private double priceChange24h;
    
    @JsonProperty("price_change_percentage_24h")
    private double priceChangePercentage24h;
    
    @JsonProperty("circulating_supply")
    private double circulatingSupply;

    // Constructors
    public CryptoData() {}

    // Getters and Setters (all of them)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public double getMarketCap() { return marketCap; }
    public void setMarketCap(double marketCap) { this.marketCap = marketCap; }

    public int getMarketCapRank() { return marketCapRank; }
    public void setMarketCapRank(int marketCapRank) { this.marketCapRank = marketCapRank; }

    public double getTotalVolume() { return totalVolume; }
    public void setTotalVolume(double totalVolume) { this.totalVolume = totalVolume; }

    public double getHigh24h() { return high24h; }
    public void setHigh24h(double high24h) { this.high24h = high24h; }

    public double getLow24h() { return low24h; }
    public void setLow24h(double low24h) { this.low24h = low24h; }

    public double getPriceChange24h() { return priceChange24h; }
    public void setPriceChange24h(double priceChange24h) { this.priceChange24h = priceChange24h; }

    public double getPriceChangePercentage24h() { return priceChangePercentage24h; }
    public void setPriceChangePercentage24h(double priceChangePercentage24h) { this.priceChangePercentage24h = priceChangePercentage24h; }

    public double getCirculatingSupply() { return circulatingSupply; }
    public void setCirculatingSupply(double circulatingSupply) { this.circulatingSupply = circulatingSupply; }
}