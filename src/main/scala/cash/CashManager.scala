package cash

import bollinger._
import ch.algotrader.entity.security.Security
import ch.algotrader.entity.strategy.Strategy
import ch.algotrader.entity.trade.LimitOrder
import ch.algotrader.enumeration.{Currency, Direction, Side}
import ch.algotrader.simulation.SimulatorImpl
import org.apache.logging.log4j.LogManager

/**
 * Created by vince on 11/04/15.
 */
class CashManager(amount: Double, leverage: Int, strategy: Strategy, currency: Currency, security: Security) extends BollingerEventHandler{

  val logger = LogManager.getLogger(classOf[BollingerBreakoutActor])

  val simulator = new SimulatorImpl();
  simulator.createCashBalance(strategy.getName, currency, new java.math.BigDecimal(amount))

  def handle(state: BollingerState, price: PriceData) = state match{
    case HIGH_CROSSED => placeOrder(Side.BUY, calculateOrderSize, price.close, validateFlatAndCashPositive)
    case LOW_CROSSED => placeOrder(Side.SELL, calculateOrderSize, price.close, validateFlatAndCashPositive)
    case CLOSED => closePosition(price)
  }

  def placeOrder(side: Side, amount: Long, price: Double, validate: () => Either[String, Boolean] = () => Right(true)) = validate() match{
    case Right(_) => {
      val order = new LimitOrder(side, amount, security, strategy, new java.math.BigDecimal(price) )
      simulator.sendOrder(order)
    }
    case Left(err) => logger.warn(s"order validation failed due to $err")
  }

  def validateFlatAndCashPositive() = {
    if( position.map(_.getQuantity).getOrElse(0) != 0 ) Left(s"a position of ${position.map(_.getQuantity)} already exists")
    else if ( simulator.findCashBalanceByStrategyAndCurrency(strategy.getName, Currency.USD).getAmount.longValue() == 0) Left(s"no cash balance")
    else Right(true)
  }


  def closePosition(price: PriceData) = {
    position.foreach{ p =>
      p.getDirection match{
        case Direction.LONG => placeOrder(Side.SELL, p.getQuantity, price.close)
        case Direction.SHORT => placeOrder(Side.BUY, Math.abs(p.getQuantity), price.close)
        case Direction.FLAT => logger.warn(s"Attempt to close a flat position, possibly a missed signal")
      }
    }
  }

  def calculateOrderSize = {
    val cb = simulator.findCashBalanceByStrategyAndCurrency(strategy.getName, Currency.USD);
    cb.getAmount.longValue() * leverage
  }


  def position = Option(simulator.findPositionByStrategyAndSecurity(strategy.getName, security))
}
