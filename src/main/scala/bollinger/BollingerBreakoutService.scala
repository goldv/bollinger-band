package bollinger

import akka.actor._

/**
 * Created by vince on 11/04/15.
 */
trait BollingerBreakoutService {

  def tick(price: PriceData): Unit

}

class DefaultBollingerBreakoutService(window: Int, width: Double, listener: BollingerEventHandler) extends BollingerBreakoutService{

  val system = ActorSystem("strategy-system")
  val actor = system.actorOf( BollingerBreakoutActor.props(window, width, listener))

  def tick(price: PriceData) = actor ! price

}
