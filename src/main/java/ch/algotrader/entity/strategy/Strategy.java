package ch.algotrader.entity.strategy;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.core.config.Property;

/**
 * Base class of an Entity that can hold {@link Property Properties}.
 */
public class Strategy {

    private String name;
    private Set<CashBalance> cashBalances = new HashSet<CashBalance>(0);

    public Strategy(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Set<CashBalance> getCashBalances() {
        return this.cashBalances;
    }

    public void setCashBalances(Set<CashBalance> cashBalances) {
        this.cashBalances = cashBalances;
    }

    public boolean addCashBalances(CashBalance element) {
        return this.cashBalances.add(element);
    }

    public boolean removeCashBalances(CashBalance element) {
        return this.cashBalances.remove(element);
    }

    @Override
    public String toString() {
        return "Strategy [name=" + this.name + "]";
    }

}
