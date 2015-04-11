package input;

import org.joda.time.LocalDate;

/**
 * Created by vince on 10/04/15.
 */
public class PriceData {

  public final LocalDate date;
  public final double open;
  public final double close;
  public final double high;
  public final double low;

  public PriceData(LocalDate date, double open, double high, double low, double close){
    this.date = date;
    this.open = open;
    this.close = close;
    this.high = high;
    this.low = low;
  }

  public String toString(){
    return "date: " + date + " open: " + open + " high: " + high + " low: " + low + " close: " + close;
  }
}
