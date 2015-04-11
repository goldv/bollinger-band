import bollinger.BollingerBreakoutService;
import input.InputReader;
import module.AppConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by vince on 10/04/15.
 */
public class Main {

  public static void main(String... args) throws Exception{

    ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    BollingerBreakoutService strategy = ctx.getBean(BollingerBreakoutService.class);

    InputReader input = new InputReader("/EUR.USD.csv");

    input.getPriceInput().forEach( strategy::tick );
  }
}
