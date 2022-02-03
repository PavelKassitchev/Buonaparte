package by.pavka.march.structure;

import by.pavka.march.order.Order;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.military.Force;

public class OrderTable extends VerticalGroup {
    Force force;
    Skin skin;
    Array<OrderView> orderViews = new Array<>();

    public OrderTable(Force force, Skin skin) {
        this.skin = skin;
        this.force = force;
        for (Order o : force.visualOrders) {
//            addActor(new OrderView(o.toString(), skin));
            addActor(new OrderView(force, skin));
        }

    }

    public void update() {
        for (Order o : force.visualOrders) {
//            addActor(new OrderView(o.toString(), skin));
            addActor(new OrderView(force, skin));
            System.out.println(force.visualOrders.size() + " " + force.visualOrders.first());
        }
    }
}
