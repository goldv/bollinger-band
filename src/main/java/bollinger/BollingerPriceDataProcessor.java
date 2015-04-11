package bollinger;

import input.PriceData;

import java.util.Optional;

/**
 * Created by vince on 10/04/15.
 */
public interface BollingerPriceDataProcessor {

  public Optional<Trade> process(PriceData price, BollingerProcessor.BollingerBand bb);
}
