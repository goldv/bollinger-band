package ch.algotrader.entity;

import ch.algotrader.entity.security.Security;
import ch.algotrader.entity.strategy.Strategy;
import ch.algotrader.enumeration.Currency;
import ch.algotrader.enumeration.TransactionType;
import ch.algotrader.util.RoundUtil;
import java.math.BigDecimal;

/**
 * A transaction stored in the database. Each Fill is recorded as a transaction using this entity. In addition the table transaction also stores transactions like intrest, debit, credit  fees.
 */
public class Transaction {

    /**
    The quantity of the Transaction. For different {@link TransactionType TransactionTypes} quantities are as follows:
    * ul
    * liBUY: pos/li
    * liSELL: neg/li
    * liEXPIRATION: pos/neg/li
    * liTRANSFER : pos/neg/li
    * liCREDIT: 1/li
    * liINTREST_RECEIVED: 1/li
    * liREFUND : 1/li
    * liDIVIDEND : 1/li
    * liDEBIT: -1/li
    * liINTREST_PAID: -1/li
    * liFEES: -1/li
    * /ul
    */
    private long quantity;
    /**
    The price of this Transaction. Is always positive
    */
    private BigDecimal price;
    /**
    The {@link Currency} of this Position.
    */
    private Currency currency;
    /**
    The {@link TransactionType}
    */
    private TransactionType type;
    /**
    A position of a particular security owned by a particular strategy. For each opening transaction a
    * position is created. The position object remains in place even if a corresponding closing
    * transaction is carried out and the quantity of the position becomes 0.
    * p
    * Since some values (e.g. {@code marketValue}) depend on whether the position is long or short,
    * aggregated position values for the same security (of different strategies) cannot be retrieved just
    * by adding position values from the corresponding strategies.
    * p
    * Example:
    * ul
    * liSecurity: VIX Dec 2012/li
    * liCurrent Bid: 16.50/li
    * liCurrent Ask: 16.60/li
    * liStrategy A: quantity +10 - marketValue: 10 * 1000 * 16.50 = 165000/li
    * liStrategy B: quantity -10 - marketValue: 10 * 1000 * 16.60 = -166000/li
    * /ul
    * p
    * The sum of above marketValues would be -1000 which is obviously wrong.
    * p
    * As a consequence the {@code PortfolioDAO} provides lookup-methods that aggregate positions from the
    * same security (of different strategies) in the correct manner.
    */
    private Position position;
    /**
    The base class of all Securities in the system
    */
    private Security security;
    /**
    Represents a running Strategy within the system. In addition the AlgoTrader Server is also
    * represented by an instance of this class.
    */
    private Strategy strategy;

    /**
     * The quantity of the Transaction. For different {@link TransactionType TransactionTypes} quantities are as follows:
     * ul
     * liBUY: pos/li
     * liSELL: neg/li
     * liEXPIRATION: pos/neg/li
     * liTRANSFER : pos/neg/li
     * liCREDIT: 1/li
     * liINTREST_RECEIVED: 1/li
     * liREFUND : 1/li
     * liDIVIDEND : 1/li
     * liDEBIT: -1/li
     * liINTREST_PAID: -1/li
     * liFEES: -1/li
     * /ul
     */
    public long getQuantity() {
        return this.quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    /**
     * The price of this Transaction. Is always positive
     */
    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * The {@link Currency} of this Position.
     */
    public Currency getCurrency() {
        return this.currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * The {@link TransactionType}
     */
    public TransactionType getType() {
        return this.type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    /**
     * A position of a particular security owned by a particular strategy. For each opening transaction a
     * position is created. The position object remains in place even if a corresponding closing
     * transaction is carried out and the quantity of the position becomes 0.
     * p
     * Since some values (e.g. {@code marketValue}) depend on whether the position is long or short,
     * aggregated position values for the same security (of different strategies) cannot be retrieved just
     * by adding position values from the corresponding strategies.
     * p
     * Example:
     * ul
     * liSecurity: VIX Dec 2012/li
     * liCurrent Bid: 16.50/li
     * liCurrent Ask: 16.60/li
     * liStrategy A: quantity +10 - marketValue: 10 * 1000 * 16.50 = 165000/li
     * liStrategy B: quantity -10 - marketValue: 10 * 1000 * 16.60 = -166000/li
     * /ul
     * p
     * The sum of above marketValues would be -1000 which is obviously wrong.
     * p
     * As a consequence the {@code PortfolioDAO} provides lookup-methods that aggregate positions from the
     * same security (of different strategies) in the correct manner.
     */
    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * The base class of all Securities in the system
     */
    public Security getSecurity() {
        return this.security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    /**
     * Represents a running Strategy within the system. In addition the AlgoTrader Server is also
     * represented by an instance of this class.
     */
    public Strategy getStrategy() {
        return this.strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public boolean isTrade() {

        if (TransactionType.BUY.equals(getType()) || TransactionType.SELL.equals(getType())) {
            return true;
        } else {
            return false;
        }
    }

    public BigDecimal getGrossValue() {

        return RoundUtil.getBigDecimal(getNetValueDouble(), 2);
    }

    public double getGrossValueDouble() {

        if (isTrade()) {
            return -getQuantity() * getPrice().doubleValue();
        } else {
            return getQuantity() * getPrice().doubleValue();
        }
    }

    public BigDecimal getNetValue() {

        return RoundUtil.getBigDecimal(getNetValueDouble(), 2);
    }

    public double getNetValueDouble() {

        return getGrossValueDouble();
    }

    @Override
    public String toString() {
        return "Transaction [quantity=" + this.quantity + ", price=" + this.price + ", currency=" + this.currency + ", type=" + this.type + ", security=" + this.security + ", strategy="
                + this.strategy + "]";
    }

}
