package ch.algotrader.entity.trade;

import java.math.BigDecimal;
import ch.algotrader.enumeration.Side;

public class Fill {

    private Side side;
    private long quantity;
    private BigDecimal price;
    private Order order;

    public Side getSide() {
        return this.side;
    }

    public void setSide(Side sideIn) {
        this.side = sideIn;
    }

    public long getQuantity() {
        return this.quantity;
    }

    public void setQuantity(long quantityIn) {
        this.quantity = quantityIn;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal priceIn) {
        this.price = priceIn;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order orderIn) {
        this.order = orderIn;
    }

    @Override
    public String toString() {
        return "Fill [side=" + this.side + ", quantity=" + this.quantity + ", price=" + this.price + "]";
    }
}
