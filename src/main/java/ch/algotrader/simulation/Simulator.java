package ch.algotrader.simulation;

import java.math.BigDecimal;
import java.util.Collection;

import ch.algotrader.entity.Position;
import ch.algotrader.entity.security.Security;
import ch.algotrader.entity.strategy.CashBalance;
import ch.algotrader.entity.trade.LimitOrder;
import ch.algotrader.enumeration.Currency;

public interface Simulator {

    void clear();

    void createCashBalance(String strategyName, Currency currency, BigDecimal amount);

    void sendOrder(LimitOrder order);

    Collection<Position> findAllPositions();

    Position findPositionByStrategyAndSecurity(String strategyName, Security security);

    Collection<Position> findPositionsByStrategy(String strategyName);

    Collection<Position> findPositionsBySecurity(Security security);

    CashBalance findCashBalanceByStrategyAndCurrency(String strategyName, Currency currency);

}
