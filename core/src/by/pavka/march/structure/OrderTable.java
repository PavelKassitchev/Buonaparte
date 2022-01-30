package by.pavka.march.structure;

import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.military.Force;

public class OrderTable extends VerticalGroup {
    Force force;
    Array<OrderView> orderViews = new Array<>();

    public OrderTable(Force force) {
        this.force = force;

    }
}
