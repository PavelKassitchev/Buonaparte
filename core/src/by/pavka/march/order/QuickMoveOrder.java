package by.pavka.march.order;

import static by.pavka.march.characteristic.March.QUICK;
import static by.pavka.march.characteristic.March.REGULAR;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.map.Hex;
import by.pavka.march.military.Force;

public class QuickMoveOrder extends MoveOrder {

    public QuickMoveOrder() {
        super();
    }

    public QuickMoveOrder(Array<Hex> destinations, boolean additiveOrder) {
        super(destinations, additiveOrder);
    }

    @Override
    public void set(Force force) {
        super.set(force);
        force.setMarch(QUICK);
    }

    @Override
    public boolean execute(Force force, float delta) {
        if (force == null) {
            return false;
        }
        if (force.forcePath != null && !force.forcePath.isEmpty() || !force.tail.isEmpty()) {
            force.move(delta);
        } else {
            force.actualOrders.fulfillOrder(this);
            force.setMarch(REGULAR);
        }
        return true;
    }
}
