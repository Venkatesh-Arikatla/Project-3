package com.example.cryptotracker.controller;

import com.example.cryptotracker.dto.response.CryptoResponse;
import com.example.cryptotracker.model.CryptoData;
import com.example.cryptotracker.service.CoinGeckoService;
import com.example.cryptotracker.service.CryptoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crypto")
@CrossOrigin(origins = "*") // Allow frontend access
public class CryptoController {

    private final CoinGeckoService coinGeckoService;
    private final CryptoService cryptoService;

    public CryptoController(CoinGeckoService coinGeckoService, CryptoService cryptoService) {
        this.coinGeckoService = coinGeckoService;
        this.cryptoService = cryptoService;
    }

    /**
     * ✅ Endpoint 1: Search crypto by name or symbol
     * Example: /api/crypto/search?query=BTC
     */
    @GetMapping("/search")
    public ResponseEntity<?> getCryptoData(@RequestParam String query) {
        String symbol = query.trim().toLowerCase();

        System.out.println("🔍 Searching for: " + symbol);

        // Step 1: Map symbol → CoinGecko ID (e.g., BTC → bitcoin)
        String mappedId = cryptoService.getCommonCoinId(symbol);
        String cryptoId = (mappedId != null) ? mappedId : symbol;

        System.out.println("✅ Using CoinGecko ID: " + cryptoId);

        try {
            // Step 2: Try via CoinGeckoService
            CryptoResponse response = coinGeckoService.getCryptoPrice(cryptoId);

            if (response == null || response.getCurrentPrice() == null) {
                System.out.println("⚠️ No data via CoinGeckoService, fallback to CryptoService");
                CryptoData fallback = cryptoService.getCryptoData(symbol);

                if (fallback == null || fallback.getCurrentPrice() == 0) {
                    return ResponseEntity.status(404).body(Map.of("error", "Cryptocurrency not found"));
                }
                return ResponseEntity.ok(fallback);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ CoinGecko fetch error: " + e.getMessage());

            CryptoData fallback = cryptoService.getCryptoData(symbol);
            if (fallback == null) {
                return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch crypto data"));
            }
            return ResponseEntity.ok(fallback);
        }
    }

    /**
     * ✅ Endpoint 2: Get top 100 cryptocurrencies (cached, refreshed every few minutes)
     * Example: /api/crypto/top
     */
    @GetMapping("/top")
    public ResponseEntity<?> getTopCoins() {
        try {
            List<CryptoData> topCoins = cryptoService.getTopCoins();
            if (topCoins == null || topCoins.isEmpty()) {
                return ResponseEntity.status(500).body(Map.of("error", "Unable to fetch top coins"));
            }
            return ResponseEntity.ok(topCoins);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch top coins: " + e.getMessage()));
        }
    }
}
