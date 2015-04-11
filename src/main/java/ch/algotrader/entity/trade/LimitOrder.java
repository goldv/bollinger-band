package ch.algotrader.entity.trade;

import java.math.BigDecimal;

import ch.algotrader.entity.security.Security;
import ch.algotrader.entity.strategy.Strategy;
import ch.algotrader.enumeration.Side;

/**
 * Base Class for all Order Types
 */
public class LimitOrder extends ch.algotrader.entity.trade.Order {

    public LimitOrder(Side side, long quantity, Security security, Strategy strategy, BigDecimal limit) {
        super(side, quantity, security, strategy);
        this.limit = limit;
    }

    private BigDecimal limit;

    public BigDecimal getLimit() {
        return this.limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "LimitOrder [toString()=" + super.toString() + ", limit=" + this.limit + "]";
    }

}
