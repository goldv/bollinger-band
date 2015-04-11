package ch.algotrader.simulation;
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


import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.algotrader.entity.Position;
import ch.algotrader.entity.security.Security;
import ch.algotrader.entity.strategy.CashBalance;
import ch.algotrader.entity.strategy.Strategy;
import ch.algotrader.entity.trade.LimitOrder;
import ch.algotrader.enumeration.Currency;
import ch.algotrader.enumeration.Side;

/**
 * @author <a href="mailto:aflury@algotrader.ch">Andy Flury</a>
 *
 * @version $Revision$ $Date$
 */
public class SimulatorTest {

    private Simulator simulator;

    @Before
    public void before() {

        this.simulator = new SimulatorImpl();
    }

    @Test
    public void test() {

        String strategyName = "STRAT_1";
        Strategy strategy = new Strategy(strategyName);

        Currency currency = Currency.USD;

        Security security = new Security("STOCK_1", currency);

        this.simulator.createCashBalance(strategyName, currency, new BigDecimal(1000));

        LimitOrder order1 = new LimitOrder(Side.BUY, 10, security, strategy, new BigDecimal(10.0));

        this.simulator.sendOrder(order1);

        Position position = this.simulator.findPositionByStrategyAndSecurity(strategyName, security);
        Assert.assertEquals(10, position.getQuantity());

        CashBalance cashBalance = this.simulator.findCashBalanceByStrategyAndCurrency(strategyName, currency);
        Assert.assertEquals(new BigDecimal("900.00"), cashBalance.getAmount());

        LimitOrder order2 = new LimitOrder(Side.SELL, 10, security, strategy, new BigDecimal(20.0));

        this.simulator.sendOrder(order2);

        position = this.simulator.findPositionByStrategyAndSecurity(strategyName, security);
        Assert.assertEquals(0, position.getQuantity());
        Assert.assertEquals(100, position.getRealizedPL(), 0.01);

        cashBalance = this.simulator.findCashBalanceByStrategyAndCurrency(strategyName, currency);
        Assert.assertEquals(new BigDecimal("1100.00"), cashBalance.getAmount());
    }
}
