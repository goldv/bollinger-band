/***********************************************************************************
 * AlgoTrader Enterprise Trading Framework
 *
 * Copyright (C) 2014 AlgoTrader GmbH - All rights reserved
 *
 * All information contained herein is, and remains the property of AlgoTrader GmbH.
 * The intellectual and technical concepts contained herein are proprietary to
 * AlgoTrader GmbH. Modification, translation, reverse engineering, decompilation,
 * disassembly or reproduction of this material is strictly forbidden unless prior
 * written permission is obtained from AlgoTrader GmbH
 *
 * Fur detailed terms and conditions consult the file LICENSE.txt or contact
 *
 * AlgoTrader GmbH
 * Badenerstrasse 16
 * 8004 Zurich
 ***********************************************************************************/
package ch.algotrader.simulation;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.algotrader.entity.Position;
import ch.algotrader.entity.Transaction;
import ch.algotrader.entity.security.Security;
import ch.algotrader.entity.strategy.CashBalance;
import ch.algotrader.entity.strategy.Strategy;
import ch.algotrader.entity.trade.Fill;
import ch.algotrader.entity.trade.LimitOrder;
import ch.algotrader.entity.trade.Order;
import ch.algotrader.enumeration.Currency;
import ch.algotrader.enumeration.Side;
import ch.algotrader.enumeration.TransactionType;
import ch.algotrader.util.PositionUtil;
import ch.algotrader.util.collection.Pair;

/**
 * Utility that can be used by strategies during in-memory simulations. It
 * provides similar functionality to the standard position, cash balance and
 * transaction management but is calculated in memory and not persisted to the
 * database for performance reasons.
 *
 * @author <a href="mailto:aflury@algotrader.ch">Andy Flury</a>
 *
 * @version $Revision$ $Date$
 */
public class SimulatorImpl implements Simulator {

    private static Logger logger = LogManager.getLogger(SimulatorImpl.class.getName());

    private final Map<Pair<String, Currency>, CashBalance> cashBalances;
    private final Map<Pair<String, Security>, Position> positionsByStrategyAndSecurity;
    private final MultiMap<String, Position> positionsByStrategy;
    private final MultiMap<Security, Position> positionsBySecurity;

    public SimulatorImpl() {

        this.cashBalances = new HashMap<Pair<String, Currency>, CashBalance>();
        this.positionsByStrategyAndSecurity = new HashMap<Pair<String, Security>, Position>();
        this.positionsByStrategy = new MultiHashMap<String, Position>();
        this.positionsBySecurity = new MultiHashMap<Security, Position>();
    }


    /* (non-Javadoc)
     * @see ch.algotrader.simulation.Simulator#clear()
     */
    public void clear() {
        this.positionsByStrategyAndSecurity.clear();
        this.positionsByStrategy.clear();
        this.positionsBySecurity.clear();
        this.cashBalances.clear();
    }

    /* (non-Javadoc)
     * @see ch.algotrader.simulation.Simulator#createCashBalance(java.lang.String, ch.algotrader.enumeration.Currency, java.math.BigDecimal)
     */
    public void createCashBalance(String strategyName, Currency currency, BigDecimal amount) {

        if (findCashBalanceByStrategyAndCurrency(strategyName, currency) != null) {
            throw new IllegalStateException("cashBalance already exists");
        }

        Strategy strategy = new Strategy(strategyName);

        CashBalance cashBalance = new CashBalance(currency, strategy);
        cashBalance.setAmount(amount);

        createCashBalance(cashBalance);

        logger.debug("created cashBalance: " + cashBalance);
    }

    /* (non-Javadoc)
     * @see ch.algotrader.simulation.Simulator#sendOrder(ch.algotrader.entity.trade.LimitOrder)
     */
    public void sendOrder(LimitOrder order) {

        // validate strategy and security
        Validate.notNull(order.getStrategy(), "missing strategy for order " + order);
        Validate.notNull(order.getSecurity(), "missing security for order " + order);

        // create one fill per order
        Fill fill = new Fill();
        fill.setSide(order.getSide());
        fill.setQuantity(order.getQuantity());
        fill.setPrice(order.getLimit());
        fill.setOrder(order);

        // create the transaction
        createTransaction(fill);
    }

    private Transaction createTransaction(Fill fill) {

        Order order = fill.getOrder();
        Security security = order.getSecurity();
        Strategy strategy = order.getStrategy();

        TransactionType transactionType = Side.BUY.equals(fill.getSide()) ? TransactionType.BUY : TransactionType.SELL;
        long quantity = Side.BUY.equals(fill.getSide()) ? fill.getQuantity() : -fill.getQuantity();

        Transaction transaction = new Transaction();
        transaction.setQuantity(quantity);
        transaction.setPrice(fill.getPrice());
        transaction.setType(transactionType);
        transaction.setSecurity(security);
        transaction.setStrategy(strategy);
        transaction.setCurrency(security.getCurrency());

        persistTransaction(transaction);

        return transaction;
    }

    /**
     * @param reason
     * @copy ch.algotrader.service.TransactionServiceImpl.handlePersistTransaction(Transaction)
     */
    private Position persistTransaction(Transaction transaction) {

        // create a new position if necessary
        Position position = findPositionByStrategyAndSecurity(transaction.getStrategy().getName(), transaction.getSecurity());
        if (position == null) {

            position = PositionUtil.processFirstTransaction(transaction);

            createPosition(position);

            // associate reverse-relations (after position has received an id)
            transaction.setPosition(position);

        } else {

            // process the transaction (adjust quantity, cost and realizedPL)
            PositionUtil.processTransaction(position, transaction);

            // associate the position
            transaction.setPosition(position);
        }

        // add the amount to the corresponding cashBalance
        processTransaction(transaction);

        logger.debug("executed transaction: " + transaction);

        return position;
    }


    /**
     * @copy ch.algotrader.service.CashBalanceServiceImpl.handleProcessTransaction(Transaction)
     */
    private void processTransaction(Transaction transaction) {

        processAmount(transaction.getStrategy(), transaction.getCurrency(), transaction.getNetValue());
    }

    /**
     * @copy ch.algotrader.service.CashBalanceServiceImpl.handleProcessAmount(String, CurrencyAmountVO)
     */
    private void processAmount(Strategy strategy, Currency currency, BigDecimal amount) {

        CashBalance cashBalance = findCashBalanceByStrategyAndCurrency(strategy.getName(), currency);

        // create the cashBalance, if it does not exist yet
        if (cashBalance == null) {

            cashBalance = new CashBalance(currency, strategy);

            // associate currency, amount and strategy
            cashBalance.setAmount(amount);

            createCashBalance(cashBalance);

        } else {

            cashBalance.setAmount(cashBalance.getAmount().add(amount));
        }
    }

    private void createPosition(Position position) {

        String name = position.getStrategy().getName();
        Security security = position.getSecurity();

        this.positionsByStrategyAndSecurity.put(new Pair<String, Security>(name,security), position);
        this.positionsByStrategy.put(name, position);
        this.positionsBySecurity.put(security, position);
    }

    private void createCashBalance(CashBalance cashBalance) {
        this.cashBalances.put(new Pair<String, Currency>(cashBalance.getStrategy().getName(), cashBalance.getCurrency()), cashBalance);
    }

    /* (non-Javadoc)
     * @see ch.algotrader.simulation.Simulator#findAllPositions()
     */
    public Collection<Position> findAllPositions() {
        return this.positionsByStrategy.values();
    }

    /* (non-Javadoc)
     * @see ch.algotrader.simulation.Simulator#findPositionByStrategyAndSecurity(java.lang.String, ch.algotrader.entity.security.Security)
     */
    public Position findPositionByStrategyAndSecurity(String strategyName, Security security) {
        return this.positionsByStrategyAndSecurity.get(new Pair<String, Security>(strategyName, security));
    }

    /* (non-Javadoc)
     * @see ch.algotrader.simulation.Simulator#findPositionsByStrategy(java.lang.String)
     */
    public Collection<Position> findPositionsByStrategy(String strategyName) {
        return this.positionsByStrategy.get(strategyName);
    }

    /* (non-Javadoc)
     * @see ch.algotrader.simulation.Simulator#findPositionsBySecurity(ch.algotrader.entity.security.Security)
     */
    public Collection<Position> findPositionsBySecurity(Security security) {
        return this.positionsBySecurity.get(security);
    }

    /* (non-Javadoc)
     * @see ch.algotrader.simulation.Simulator#findCashBalanceByStrategyAndCurrency(java.lang.String, ch.algotrader.enumeration.Currency)
     */
    public CashBalance findCashBalanceByStrategyAndCurrency(String strategyName, Currency currency) {
        return this.cashBalances.get(new Pair<String, Currency>(strategyName, currency));
    }
}
