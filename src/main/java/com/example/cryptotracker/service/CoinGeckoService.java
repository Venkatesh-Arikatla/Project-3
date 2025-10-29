package com.example.cryptotracker.service;

import com.example.cryptotracker.client.CoinGeckoClient;
import com.example.cryptotracker.dto.external.CoinGeckoResponse;
import com.example.cryptotracker.dto.response.CryptoResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CoinGeckoService {

    private final CoinGeckoClient coinGeckoClient;

    public CoinGeckoService(CoinGeckoClient coinGeckoClient) {
        this.coinGeckoClient = coinGeckoClient;
    }

    public CryptoResponse getCryptoPrice(String cryptoId) {
        try {
            CoinGeckoResponse response = coinGeckoClient.getCryptoData(cryptoId);
            return convertToCryptoResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch crypto data: " + e.getMessage(), e);
        }
    }

    private CryptoResponse convertToCryptoResponse(CoinGeckoResponse externalResponse) {
        CryptoResponse response = new CryptoResponse();
        response.setId(externalResponse.getId());
        response.setSymbol(externalResponse.getSymbol());
        response.setName(externalResponse.getName());

        // ✅ Add Rank
        response.setRank(externalResponse.getMarketCapRank());

        // Set image (if available)
        if (externalResponse.getImage() != null && externalResponse.getImage().containsKey("large")) {
            response.setImage(externalResponse.getImage().get("large"));
        }

        // Extract market data safely
        if (externalResponse.getMarketData() != null) {
            CoinGeckoResponse.MarketData marketData = externalResponse.getMarketData();

            // ✅ Add Circulating Supply
            response.setCirculatingSupply(marketData.getCirculatingSupply());

            // Current Price (USD)
            if (marketData.getCurrentPrice() != null && marketData.getCurrentPrice().containsKey("usd")) {
                response.setCurrentPrice(marketData.getCurrentPrice().get("usd"));
            }

            // Price Change 24h
            response.setPriceChange24h(marketData.getPriceChange24h());

            // Price Change Percentage 24h
            response.setPriceChangePercentage24h(marketData.getPriceChangePercentage24h());

            // Market Cap (USD)
            if (marketData.getMarketCap() != null && marketData.getMarketCap().containsKey("usd")) {
                response.setMarketCap(marketData.getMarketCap().get("usd").doubleValue());
            }

            // Total Volume (USD)
            if (marketData.getTotalVolume() != null && marketData.getTotalVolume().containsKey("usd")) {
            response.setTotalVolume(marketData.getTotalVolume().get("usd").doubleValue());
            }

            // 24h High (USD)
            if (marketData.getHigh24h() != null && marketData.getHigh24h().containsKey("usd")) {
                response.setHigh24h(marketData.getHigh24h().get("usd"));
            }

            // 24h Low (USD)
            if (marketData.getLow24h() != null && marketData.getLow24h().containsKey("usd")) {
                response.setLow24h(marketData.getLow24h().get("usd"));
            }
        }

        response.setLastUpdated(LocalDateTime.now());
        return response;
    }
}
