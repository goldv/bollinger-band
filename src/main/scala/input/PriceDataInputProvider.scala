package input

import java.text.SimpleDateFormat

import bollinger.PriceData
import org.joda.time.LocalDate
import scala.io.Source
import scala.util.Try

/**
 * Created by vince on 11/04/15.
 */
trait PriceDataInputProvider {
  def getPriceData: Iterator[PriceData]
}

class DefaultPriceDataInputProvider(file: String) extends PriceDataInputProvider{

  val format = new SimpleDateFormat("yyyy.MM.dd")

  def getPriceData: Iterator[PriceData] = {
    val source = Source.fromURL(getClass.getResource(s"/$file"))
    source.getLines().drop(1).flatMap { line =>
      buildPriceData(line.split(","))
    }
  }

  def buildPriceData(row: Array[String]): Option[PriceData] = {
    val pd = for{
      date <- Try( format.parse(row(0)) )
      open <- Try( row(1).toDouble )
      low <- Try( row(2).toDouble )
      high <- Try( row(3).toDouble )
      close <- Try( row(4).toDouble )
    } yield PriceData(LocalDate.fromDateFields(date), open, low, high, close)

    pd.toOption
  }
}


