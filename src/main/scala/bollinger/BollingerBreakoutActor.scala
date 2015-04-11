package bollinger

import akka.actor.Actor
import akka.actor.{ ActorRef, FSM }
import org.apache.commons.math.stat.descriptive.{DescriptiveStatistics, SummaryStatistics}
import org.apache.logging.log4j.LogManager
import org.joda.time.LocalDate

sealed trait State
object CLOSED extends State
object HIGH_CROSSED extends State
object LOW_CROSSED extends State

case class PriceData(date: LocalDate, open: Double, low: Double, high: Double, close: Double)
case class BollingerBand(low: Double, mean: Double, high: Double)
case class BollingerEvent(price: PriceData, event: State)

/**
 * Created by vince on 11/04/15.
 */
class BollingerBreakoutActor(window: Int, width: Double, listener: BollingerEventHandler) extends FSM[State, PriceData]{

  val logger = LogManager.getLogger(classOf[BollingerBreakoutActor]);

  val stats = new DescriptiveStatistics();
  stats.setWindowSize(window)

  var bollinger: Option[BollingerBand] = None

  startWith(CLOSED, PriceData(LocalDate.now(), 0, 0, 0, 0) )

  when(CLOSED) {
    case Event(p: PriceData, _) => {

      stats.addValue(p.close)
      calculateBollinger

      bollinger.map{ b =>
        if(p.close > b.high) goto(HIGH_CROSSED) using p
        else if(p.close < b.low) goto(LOW_CROSSED) using p
        else stay using p
      } getOrElse stay using p
    }
  }

  when(HIGH_CROSSED){
    case Event(p: PriceData, _) => {
      if(p.close < bollinger.get.mean) goto(CLOSED) using p
      else stay() using p
    }
  }

  when(LOW_CROSSED){
    case Event(p: PriceData, _) => {
      if(p.close > bollinger.get.mean) goto(CLOSED) using p
      else stay() using p
    }
  }

  onTransition{
    case CLOSED -> HIGH_CROSSED => listener.handle(HIGH_CROSSED, stateData)
    case CLOSED -> LOW_CROSSED => listener.handle(LOW_CROSSED, stateData)
    case _ -> CLOSED => listener.handle(CLOSED, stateData)
  }

  def calculateBollinger = {
    if(stats.getN >= window){
      val stddev = stats.getStandardDeviation
      val mean = stats.getMean

      bollinger = Some( BollingerBand(mean - width * stddev, mean, mean + width * stddev) )
    }
  }

}

trait BollingerEventHandler{
  def handle(event: State, price: PriceData)
}