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

    public OrderTable(Force force, Skin skin) {
        this.skin = skin;
        this.force = force;
        update();

    }

    public void update() {
        clear();
        for (Order o : force.visualOrders) {
            addActor(new OrderView(force, o, skin));
        }
    }
}
