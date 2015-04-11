package ch.algotrader.entity.trade;

import ch.algotrader.entity.security.Security;
import ch.algotrader.entity.strategy.Strategy;
import ch.algotrader.enumeration.Side;

/**
 * Base Class for all Order Types
 */
public abstract class Order {

    private Side side;
    private long quantity;
    private Security security;
    private Strategy strategy;

    public Order(Side side, long quantity, Security security, Strategy strategy) {
        super();
        this.side = side;
        this.quantity = quantity;
        this.security = security;
        this.strategy = strategy;
    }

    public Side getSide() {
        return this.side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public long getQuantity() {
        return this.quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public Security getSecurity() {
        return this.security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Strategy getStrategy() {
        return this.strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public String toString() {
        return "Order [side=" + this.side + ", quantity=" + this.quantity + ", security=" + this.security + ", strategy=" + this.strategy + "]";
    }

}
