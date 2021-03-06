package by.pavka.march.order;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.military.Force;
import by.pavka.march.military.Formation;

public class DetachForceOrder extends Order {
    public DetachOrder detachOrder;
    private Force detachedForce;
//    private boolean executable;

    public DetachForceOrder(DetachOrder order) {
        detachOrder = order;
        detachedForce = detachOrder.detachingForce;
    }

    public DetachForceOrder(Force detachedForce) {
        this.detachedForce = detachedForce;
//        this.executable = executable;
    }

    public DetachForceOrder() {}

    public void setForce (Force force) {
        detachedForce = force;
    }

    @Override
    public boolean isExecutable(Force force) {
        return true;
    }

    @Override
    public void set(Force force) {
        execute(force, 0);
    }

    @Override
    public boolean execute(Force force, float delta) {
        if (detachedForce != null) {
            if (detachedForce.nation == null) {
                Formation group = (Formation) detachedForce;
                Force sub = group.subForces.get(0);
                group.nation = sub.nation;
                group.remoteHeadForce = sub.findHyperForce().remoteHeadForce == null?
                        (Formation)(sub.findHyperForce()) : sub.findHyperForce().remoteHeadForce;

                for (Force f : group.subForces) {
                    f.detach(true, false);
                    f.superForce = group;
                    f.remoteHeadForce = null;
                    group.shapeRenderer = f.shapeRenderer;
                    f.shapeRenderer = null;
                }

                group.setRealHex(sub.hex);
                group.sendReport("");
                System.out.println("Group size is " + group.subForces.size);
            }
            else {
                detachedForce.detach(true, true);
            }
            force.actualOrders.fulfillOrder(this);
            return true;
        }
        force.actualOrders.fulfillOrder(this);
        return false;
    }

    @Override
    public boolean execute(Array<Force> forces) {
        return false;
    }

    @Override
    public void cancel(Force f) {
        Force.sendOrder(detachedForce, new JoinOrder((Formation)f, true));
        System.out.println("Canceling detach " + detachedForce);
    }

    @Override
    public String toString() {
        if (detachOrder != null) {
            return super.toString() + detachOrder.detachingForce.getName();
        } else {
            return super.toString();
        }
    }
}
