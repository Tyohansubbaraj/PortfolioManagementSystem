package com.marketminds.portfoliomanagementsystem.dto;

import java.util.List;

public class SymbolsRequest {
    private List<String> symbols;

    public List<String> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<String> symbols) {
        this.symbols = symbols;
    }
}
