package by.pavka.march.order;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.military.Force;

public abstract class Order {
    public boolean irrevocable;
    public boolean revoked;

    public void receive(Force force) {
        if (force != null && !revoked) {
            force.actualOrders.addOrder(this);
            if (force.actualOrders.size() == 1) {
                set(force);
            }
        }
    }

    public void cancel(Force f) {
        //TODO
    }

    public void visualize(Force force) {
        force.visualOrders.addOrder(this);
    }

    public abstract void set(Force force);

    public abstract boolean execute(Force force, float delta);

    public abstract boolean execute(Array<Force> forces);

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
