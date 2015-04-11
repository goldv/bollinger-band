import bollinger.BollingerPriceDataProcessor;
import bollinger.BollingerProcessor;
import bollinger.BollingerTradeProcessor;
import bollinger.Trade;
import input.InputReader;
import input.PriceData;
import position.PositionManager;
import position.PositionManagerImpl;
import java.util.List;
import java.util.Optional;

/**
 * Created by vince on 10/04/15.
 */
public class Main {

  public static void main(String... args) throws Exception{

    InputReader ir = new InputReader("/Users/vince/source/algo-trader/src/main/resources/EUR.USD.csv");

    List<PriceData> data = ir.getPriceInput();

    final PositionManager pmanager = new PositionManagerImpl(1000000.00,2);

    BollingerTradeProcessor tp = (Trade trade, PriceData priceData) -> {
      pmanager.handleIndicator(trade, priceData);
    };

    BollingerPriceDataProcessor bp = (PriceData price, BollingerProcessor.BollingerBand bb) -> {
      if(price.close > bb.high) return Optional.of(Trade.LONG);
      else if(price.close < bb.low) return Optional.of(Trade.SHORT);
      else if(price.close > bb.average) return Optional.of(Trade.CLOSE_SHORT);
      else if(price.close < bb.average) return Optional.of(Trade.CLOSE_LONG);
      else return Optional.empty();

    };

    BollingerProcessor processor = new BollingerProcessor(30,bp,tp);

    data.forEach(processor::processPriceData);

  }
}
