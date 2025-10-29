package com.example.cryptotracker.client;

import com.example.cryptotracker.dto.external.CoinGeckoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class CoinGeckoClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${coingecko.api.base-url:https://api.coingecko.com/api/v3}")
    private String baseUrl;
    
    public CoinGeckoClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public CoinGeckoResponse getCryptoData(String cryptoId) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/coins/{id}")
                .queryParam("localization", "false")
                .queryParam("tickers", "false")
                .queryParam("market_data", "true")
                .queryParam("community_data", "false")
                .queryParam("developer_data", "false")
                .queryParam("sparkline", "false")
                .buildAndExpand(cryptoId)
                .toUriString();
        
        return restTemplate.getForObject(url, CoinGeckoResponse.class);
    }
}