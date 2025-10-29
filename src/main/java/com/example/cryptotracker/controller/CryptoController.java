package com.example.cryptotracker.controller;

import com.example.cryptotracker.model.CryptoData;
import com.example.cryptotracker.service.CryptoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crypto")
@CrossOrigin(origins = "*")
public class CryptoController {

    private final CryptoService cryptoService;

    // ✅ Simplified - only one dependency
    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> getCryptoData(@RequestParam String query) {
        String symbol = query.trim().toLowerCase();
        System.out.println("🔍 Searching for: " + symbol);

        try {
            CryptoData data = cryptoService.getCryptoData(symbol);

            if (data == null || data.getCurrentPrice() == 0) {
                return ResponseEntity.status(404).body(Map.of(
                    "error", "Cryptocurrency not found",
                    "message", "No data found for: " + symbol
                ));
            }

            System.out.println("✅ Successfully returned data for: " + symbol);
            return ResponseEntity.ok(data);

        } catch (Exception e) {
            System.out.println("❌ Error fetching data for " + symbol + ": " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to fetch crypto data",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/top")
    public ResponseEntity<?> getTopCoins() {
        try {
            List<CryptoData> topCoins = cryptoService.getTopCoins();
            if (topCoins == null || topCoins.isEmpty()) {
                return ResponseEntity.status(500).body(Map.of("error", "Unable to fetch top coins"));
            }
            
            System.out.println("✅ Returning top " + topCoins.size() + " coins");
            return ResponseEntity.ok(topCoins);
            
        } catch (Exception e) {
            System.out.println("❌ Error fetching top coins: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to fetch top coins", 
                "message", e.getMessage()
            ));
        }
    }
}