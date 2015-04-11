package bollinger

import akka.actor.{Props, Actor, ActorRef, FSM}
import org.apache.commons.math.stat.descriptive.{DescriptiveStatistics, SummaryStatistics}
import org.apache.logging.log4j.LogManager
import org.joda.time.LocalDate

sealed trait BollingerState
object CLOSED extends BollingerState
object HIGH_CROSSED extends BollingerState
object LOW_CROSSED extends BollingerState

case class PriceData(date: LocalDate, open: Double, low: Double, high: Double, close: Double)
case class BollingerBand(low: Double, mean: Double, high: Double)
case class BollingerEvent(price: PriceData, event: BollingerState)

/**
 * Created by vince on 11/04/15.
 */
class BollingerBreakoutActor(window: Int, width: Double, listener: BollingerEventHandler) extends FSM[BollingerState, PriceData]{

  val logger = LogManager.getLogger(classOf[BollingerBreakoutActor]);

  val stats = new DescriptiveStatistics();
  stats.setWindowSize(window)

  var bollinger: Option[BollingerBand] = None

  startWith(CLOSED, PriceData(LocalDate.now(), 0, 0, 0, 0) )

  when(CLOSED) {
    case Event(p: PriceData, _) => {

      calculateBollinger(p)

      bollinger.map{ b =>
        if(p.close > b.high) goto(HIGH_CROSSED) using p
        else if(p.close < b.low) goto(LOW_CROSSED) using p
        else stay using p
      } getOrElse stay using p
    }
  }

  when(HIGH_CROSSED){
    case Event(p: PriceData, _) => {
      calculateBollinger(p)

      if(p.close < bollinger.get.mean) goto(CLOSED) using p
      else stay() using p
    }
  }

  when(LOW_CROSSED){
    case Event(p: PriceData, _) => {
      calculateBollinger(p)

      if(p.close > bollinger.get.mean) goto(CLOSED) using p
      else stay() using p
    }
  }

  onTransition{
    case CLOSED -> HIGH_CROSSED => notify(HIGH_CROSSED, stateData)
    case CLOSED -> LOW_CROSSED => notify(LOW_CROSSED, stateData)
    case _ -> CLOSED => notify(CLOSED, stateData)
  }

  def calculateBollinger(p: PriceData) = {
    stats.addValue(p.close)

    if (stats.getN >= window) {
      val stddev = stats.getStandardDeviation
      val mean = stats.getMean

      bollinger = Some(BollingerBand(mean - width * stddev, mean, mean + width * stddev))
    }
  }
  
  def notify(state: BollingerState, price: PriceData) = try{
    listener.handle(state, price)
  } catch {
    case t:Throwable => t.printStackTrace()
  }
}

object BollingerBreakoutActor{

  def props(window: Int, width: Double, listener: BollingerEventHandler) = Props( new BollingerBreakoutActor(window, width, listener) )
}

trait BollingerEventHandler{
  def handle(event: BollingerState, price: PriceData)
}