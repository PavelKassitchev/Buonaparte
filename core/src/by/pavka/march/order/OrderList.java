package by.pavka.march.order;

import by.pavka.march.map.Hex;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

import by.pavka.march.military.Force;
import by.pavka.march.thread.ReportThread;

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
//        if (order instanceof MoveOrder) {
//            Array<Hex> orderDestinations = ((MoveOrder) order).destinations;
//            for (Hex h : orderDestinations) {
//                force.playScreen.destinations.removeValue(h, true);
//            }
//        }
    }

    public void removeMoveOrders() {
        while (orders.size > 1 && orders.get(1) instanceof MoveOrder) {
            orders.removeIndex(1);
        }
    }

    public void removeHexFromDestinations(Hex hex) {
        if (!orders.isEmpty()) {
            int i = 0;
            Order topOrder = orders.get(i);
            while (topOrder instanceof MoveOrder) {
                ((MoveOrder) topOrder).destinations.removeValue(hex,true);
                i++;
                if (i > orders.size - 1) {
                    break;
                }
                topOrder = orders.get(i);
            }
        }
    }

    public void removeFirst() {
        orders.removeValue(orders.first(), true);
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
