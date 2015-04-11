package module;

import bollinger.BollingerBreakoutService;
import bollinger.DefaultBollingerBreakoutService;
import cash.CashManager;
import ch.algotrader.entity.security.Security;
import ch.algotrader.entity.strategy.Strategy;
import ch.algotrader.enumeration.Currency;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by vince on 11/04/15.
 */
@Configuration
public class AppConfig {

    @Bean
    public BollingerBreakoutService getBreakOutService(){
        return new DefaultBollingerBreakoutService(30, 2.5, getCashManager());
    }

    @Bean
    public CashManager getCashManager(){
        return new CashManager(1000000.0,2,new Strategy("STRATEGY"), Currency.USD, new Security("EUR/USD",Currency.USD) );
    }

}
