package by.pavka.march.order;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.military.Force;
import by.pavka.march.thread.ReportThread;

import java.util.Iterator;

public class OrderList implements Iterable<Order> {
    private Array<Order> orders = new Array<>();
    private Force force;

    public OrderList(Force force) {
        this.force = force;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void addFirstOrder(Order order) {
        orders.insert(0, order);
    }

    public void removeOrder(Order order) {
        orders.removeValue(order, true);
    }

    public void fulfillOrder(Order order) {
        removeOrder(order);
        new ReportThread(force, order).start();
        if (!orders.isEmpty()) {
            first().set(force);
        }
    }

    public int size() {
        return orders.size;
    }

    public Order first() {
        if (orders.isEmpty()) {
            return null;
        }
        return orders.first();
    }

    @Override
    public Iterator<Order> iterator() {
        return orders.iterator();
    }
}
