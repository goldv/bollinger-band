package ch.algotrader.entity.security;

import ch.algotrader.enumeration.Currency;

/**
 * The base class of all Securities in the system
 */
public class Security {

    private String symbol;
    private Currency currency;

    public Security(String symbol, Currency currency) {
        super();
        this.symbol = symbol;
        this.currency = currency;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    @Override
    public String toString() {
        return "Security [symbol=" + this.symbol + ", currency=" + this.currency + "]";
    }

}
