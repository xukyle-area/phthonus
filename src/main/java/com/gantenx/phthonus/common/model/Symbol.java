package com.gantenx.phthonus.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class Symbol {
    private String base;
    private String quote;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol that = (Symbol) o;
        return Objects.equals(base, that.base) && Objects.equals(quote, that.quote);
    }

    public String getSymbol() {
        return base + quote;
    }

    public String getSymbolWithDot() {
        return base + "." + quote;
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, quote);
    }
}
