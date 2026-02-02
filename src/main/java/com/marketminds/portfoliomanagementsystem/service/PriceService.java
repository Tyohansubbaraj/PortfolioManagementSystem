package com.marketminds.portfoliomanagementsystem.service;

import com.marketminds.portfoliomanagementsystem.dto.SymbolsRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class PriceService {

    private final WebClient webClient;

    public PriceService(WebClient priceWebClient) {
        this.webClient = priceWebClient;
    }

    public Map<String, BigDecimal> fetchCurrentPrices(List<String> symbols) {

        SymbolsRequest request = new SymbolsRequest();
        request.setSymbols(symbols);

        return webClient.post()
                .uri("/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, BigDecimal>>() {})
                .block();
    }
}
