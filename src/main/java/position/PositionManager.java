package position;

import bollinger.Trade;
import input.PriceData;

/**
 * Created by vince on 10/04/15.
 */
public interface PositionManager {

  public void handleIndicator(Trade trade, PriceData price);
}
