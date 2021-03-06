package by.pavka.march.order;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.military.Force;

public abstract class Order {
    public static final int SAFE = -1;
    public final static int DEFAULT = 0;
    public final static int OFFENSIVE = 1;

    public boolean irrevocable;
    public boolean revoked;

    public abstract boolean isExecutable(Force force);

    public void receive(Force force) {
        if (force != null && !revoked) {
            force.actualOrders.addOrder(this);
            System.out.println(hashCode() + " received inside Order class");
            if (force.actualOrders.size() == 1) {
                set(force);
            }
        }
    }

//    public void cancel(Force f) {
//        //TODO
//    }

    public void visualize(Force force) {
        if (!revoked) {
            force.visualOrders.addOrder(this);
        }
    }

    public abstract void set(Force force);

    public abstract boolean execute(Force force, float delta);

    public abstract boolean execute(Array<Force> forces);

    public abstract void cancel(Force f);

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
