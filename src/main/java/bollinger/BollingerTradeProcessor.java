package bollinger;

import input.PriceData;

/**
 * Created by vince on 10/04/15.
 */
public interface BollingerTradeProcessor {

  public void process(Trade trade, PriceData price);
}
