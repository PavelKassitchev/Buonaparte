package by.pavka.march.structure;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.military.Force;
import by.pavka.march.order.Order;

public class OrderTable extends VerticalGroup {
    Force force;
    Skin skin;
    Array<OrderView> orderViews = new Array<>();
    boolean isFutureOrders;


    public OrderTable(Force force, Skin skin, boolean isFutureOrders) {
        this.skin = skin;
        this.force = force;
        this.isFutureOrders = isFutureOrders;
        System.out.println("Inside Order Table Constructor force = " + force + " is Future " + isFutureOrders);
        if (!isFutureOrders) {
            update();
        } else {

        }
    }

    public OrderTable(Force force, Skin skin) {
        this(force, skin, false);
    }


    public void update() {
        clear();
        for (Order o : force.visualOrders) {
            addActor(new OrderView(force, o, skin, false));
        }
    }

    public void addOrder(Order order) {
        OrderView ow = new OrderView(force, order, skin, true);
        System.out.println("adding order");
        addActor(ow);
//        force.futureOrders.addOrder(order);
        orderViews.add(ow);
    }

    public void refreshLabels() {
        for (OrderView ow : orderViews) {
            ow.updateLabel();
        }

    }

    public Order getLastOrder() {
        OrderView last = orderViews.get(orderViews.size - 1);
        return last.order;
    }

    public boolean isEmpty() {
        return orderViews.isEmpty();
    }

}
