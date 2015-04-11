package ch.algotrader.entity;

import org.apache.logging.log4j.core.config.Property;

import ch.algotrader.entity.security.Security;
import ch.algotrader.entity.strategy.Strategy;
import ch.algotrader.enumeration.Direction;

/**
 * Base class of an Entity that can hold {@link Property Properties}.
 */
public class Position {

    /**
     * The current quantity of this Position.
    */
    private long quantity;
    /**
     * the cost associated with the current holdings of this Position. Based on the average cost method.
    */
    private double cost;
    /**
     * the realized Profit-and-Loss of this Position
    */
    private double realizedPL;
    /**
     * Represents a running Strategy within the system. In addition the AlgoTrader Server is also
    * represented by an instance of this class.
    */
    private Strategy strategy;
    /**
     * The base class of all Securities in the system
    */
    private Security security;

    public Position(Strategy strategy, Security security) {
        super();
        this.strategy = strategy;
        this.security = security;
    }

    /**
     * The current quantity of this Position.
     */
    public long getQuantity() {
        return this.quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    /**
     * the cost associated with the current holdings of this Position. Based on the average cost method.
     */
    public double getCost() {
        return this.cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * the realized Profit-and-Loss of this Position
     */
    public double getRealizedPL() {
        return this.realizedPL;
    }

    public void setRealizedPL(double realizedPL) {
        this.realizedPL = realizedPL;
    }

    /**
     * Represents a running Strategy within the system. In addition the AlgoTrader Server is also
     * represented by an instance of this class.
     */
    public Strategy getStrategy() {
        return this.strategy;
    }

    /**
     * The base class of all Securities in the system
     */
    public Security getSecurity() {
        return this.security;
    }

    public double getAveragePrice() {

        return getCost() / getQuantity();
    }

    public Direction getDirection() {

        if (getQuantity() < 0) {
            return Direction.SHORT;
        } else if (getQuantity() > 0) {
            return Direction.LONG;
        } else {
            return Direction.FLAT;
        }
    }

    @Override
    public String toString() {
        return "Position [strategy=" + this.strategy + ", security=" + this.security + ", quantity=" + this.quantity + ", cost=" + this.cost + ", realizedPL=" + this.realizedPL + "]";
    }

}
