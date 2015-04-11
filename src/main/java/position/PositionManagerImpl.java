package position;

import bollinger.Trade;
import ch.algotrader.entity.Position;
import ch.algotrader.entity.security.Security;
import ch.algotrader.entity.strategy.CashBalance;
import ch.algotrader.entity.strategy.Strategy;
import ch.algotrader.entity.trade.LimitOrder;
import ch.algotrader.enumeration.Currency;
import ch.algotrader.enumeration.Direction;
import ch.algotrader.enumeration.Side;
import ch.algotrader.simulation.Simulator;
import ch.algotrader.simulation.SimulatorImpl;
import ch.algotrader.enumeration.Currency;
import input.PriceData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;

/**
 * Created by vince on 10/04/15.
 */

public class PositionManagerImpl implements PositionManager{

  private static Logger logger = LogManager.getLogger(PositionManagerImpl.class.getName());

  private static final String STRATEGY_NAME = "STRATEGY";
  private final Security security = new Security("EUR/USD", Currency.USD);
  private final Strategy strategy = new Strategy(STRATEGY_NAME);
  private final Simulator simulator;
  private final int leverage;

  public PositionManagerImpl(double amount, int leverage){
    this.simulator = new SimulatorImpl();
    this.leverage = leverage;
    simulator.createCashBalance(STRATEGY_NAME, Currency.USD, new BigDecimal(amount));
  }

  public void handleIndicator(Trade trade, PriceData price){
    switch(trade){
      case LONG: {
        Position position = simulator.findPositionByStrategyAndSecurity(STRATEGY_NAME, security);
        if(position == null || position.getQuantity() == 0){
          placeOrder(Side.BUY, calculateOrderSize(), price.close);
        }
        break;
      }
      case SHORT: {
        Position position = simulator.findPositionByStrategyAndSecurity(STRATEGY_NAME, security);
        if(position == null || position.getQuantity() == 0) {
          placeOrder(Side.SELL, calculateOrderSize(), price.close);
        }
        break;
      }
      case CLOSE_LONG: {
        Position position = simulator.findPositionByStrategyAndSecurity(STRATEGY_NAME, security);
        if( position != null && position.getDirection() == Direction.LONG){
          placeOrder(Side.SELL, Math.abs(position.getQuantity()), price.close);
        }
        break;
      }
      case CLOSE_SHORT: {
        Position position = simulator.findPositionByStrategyAndSecurity(STRATEGY_NAME, security);
        if( position != null && position.getDirection() == Direction.SHORT){
          placeOrder(Side.BUY, Math.abs(position.getQuantity()), price.close);
        }
        break;
      }
    }

  }

  private void placeOrder(Side side, long amount, double price){
    LimitOrder order = new LimitOrder(side, amount, security, strategy, new BigDecimal(price) );
    simulator.sendOrder(order);
  }

  private long calculateOrderSize(){
    CashBalance cb = simulator.findCashBalanceByStrategyAndCurrency(STRATEGY_NAME, Currency.USD);
    return cb.getAmount().longValue() * leverage;
  }
}
