package management;

import java.sql.Timestamp;
import java.util.Date;

public class Order {
    int order_id;
    Timestamp time;
    double total_price;

    public Order() {

    }
    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    @Override
    public String toString() {
        return "订单id: " + order_id + " " +
                "下单时间: " + time + " " +
                "总价：" + total_price;
    }
}
