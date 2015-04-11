package ch.algotrader.entity.strategy;

import ch.algotrader.enumeration.Currency;
import java.math.BigDecimal;

/**
 * Represents the current cash amount of a particular Strategy in a particular {@link ch.algotrader.enumeration.Currency Currency}.
 */
public class CashBalance {

    private Currency currency;
    private BigDecimal amount;
    private Strategy strategy;

    public CashBalance(Currency currency, Strategy strategy) {
        super();
        this.currency = currency;
        this.strategy = strategy;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Strategy getStrategy() {
        return this.strategy;
    }

    @Override
    public String toString() {
        return "CashBalance [currency=" + this.currency + ", strategy=" + this.strategy + ", amount=" + this.amount + "]";
    }

}
