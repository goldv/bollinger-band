package bollinger;

import input.PriceData;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import java.util.LinkedList;
import java.util.Optional;

/**
 * Created by vince on 10/04/15.
 */
public class BollingerProcessor {

  private final int window;
  private final BollingerPriceDataProcessor processor;
  private final BollingerTradeProcessor tradeProcessor;

  private double close_average;
  private double close_stddev;

  private LinkedList<PriceData> dataWindow = new LinkedList<>();

  public BollingerProcessor(int window, BollingerPriceDataProcessor processor, BollingerTradeProcessor tradeProcessor){
    this.window = window;
    this.processor = processor;
    this.tradeProcessor = tradeProcessor;
  }

  public void processPriceData(PriceData priceData){
    int currentWindowLength = dataWindow.size();

    if(currentWindowLength == window){
      dataWindow.removeLast();
      dataWindow.addFirst(priceData);
    } else {
      dataWindow.addFirst(priceData);
    }

    if( currentWindowLength == window) {
      recalculate();

      Optional<Trade> trade = processor.process(priceData, getBollingerBand());
      trade.ifPresent( t -> tradeProcessor.process(t, priceData) );

    }
  }

  private void recalculate(){
    DescriptiveStatistics stats = new DescriptiveStatistics();

    // Add the data from the array
    for( PriceData point : dataWindow ) {
      stats.addValue(point.close);

    }

    close_average = stats.getMean();
    close_stddev = stats.getStandardDeviation();

  }

  private BollingerBand getBollingerBand(){
    double twoStdDev = 2.5 * close_stddev;
    return new BollingerBand(close_average + twoStdDev, close_average - twoStdDev, close_average);
  }

  public static class BollingerBand{
    public final double high;
    public final double low;
    public final double average;

    public BollingerBand(double high, double low, double average){
      this.high = high;
      this.low = low;
      this.average = average;
    }

    public String toString(){
      return "average: " + average + " low: " + low + " high: " + high;
    }
  }

}
