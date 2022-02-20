package by.pavka.march.order;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.map.Hex;
import by.pavka.march.map.Path;
import by.pavka.march.military.Force;
import by.pavka.march.military.Formation;

public class JoinOrder extends MoveOrder {
    public Formation target;
    public Force thisForce;

    public JoinOrder(Formation target) {
        this.target = target;
        destinations = new Array<>();
    }

    public JoinOrder() {
        destinations = new Array<>();
    }

    public void setTargetForce(Formation target) {
        this.target = target;
    }

    public void setThisForce(Force force) {
        thisForce = force;
    }

    public void setDestination() {
        destinations.clear();
        destinations.add(target.hex);

        GraphPath<Hex> hexPath;
        Array<Path> paths = new Array<>();
        Hex start = thisForce.hex;
        for (Hex h : destinations) {
            hexPath = thisForce.playScreen.getHexGraph().findPath(start, h);
            Hex st = null;
            Hex en;
            for (Hex hx : hexPath) {
                en = hx;
                if (st != null) {
                    paths.add(thisForce.playScreen.getHexGraph().getPath(st, en));
                }
                st = hx;
            }
            start = st;
        }
        thisForce.forcePath = paths;
        isSet = true;
    }

    @Override
    public void visualize(Force force) {

        if (force != null) {
            Order o = force.visualOrders.last();
            if (o instanceof MoveOrder) {
                System.out.println("MoveOrder is first visual " + o.hashCode() + " irrevocable: " + o.irrevocable);
                if (additiveOrder) {
                    System.out.println("MoveOrder is additive " + hashCode() + " irrevocable: " + irrevocable
                            + " previous irrevocable: " + o.hashCode() + " " + o.irrevocable);
                    if (!o.irrevocable) {
                        System.out.println("First order is not irrevocable " + o.getClass() + " " + getClass());
                        force.visualOrders.removeOrder(o);
                        o.revoked = true;
                    }
                } else {
                    force.visualOrders.removeMoveOrders();
                }
            }
            System.out.println("Super");
            super.visualize(force);
        }
    }

    @Override
    public void receive(Force force) {
        if (!revoked && !additiveOrder && force.actualOrders.first() instanceof MoveOrder) {
            force.actualOrders.removeMoveOrders();
        }
        super.receive(force);
    }


    @Override
    public void set(Force force) {
        if (force == null) {
            return;
        }
        thisForce = force;
        setDestination();
    }

    @Override
    public boolean execute(Force force, float delta) {
        if (force == null) {
            return false;
        }
//TODO

        if (force.hex != target.hex && force.forcePath != null && !force.forcePath.isEmpty() || !force.tail.isEmpty()) {
            force.move(delta);
            setDestination();
        } else {
            System.out.println("Fulfilling join order in visual hex " + force.visualHex);

            force.actualOrders.fulfillOrder(this);

            target.attach(thisForce, true);
            target.sendReport("attach");
//            force.actualOrders.fulfillOrder(this);
//            force.actualOrders.clear();
            System.out.println("Completing join order in visual hex " + force.visualHex);
        }
        return true;
    }


    @Override
    public void cancel(Force f) {
        Force.sendOrder(f, new RemoveDestinationsOrder(this), 50);
        System.out.println("Sending cancellation " + hashCode());
    }

    @Override
    public String toString() {
        String name = target == null? "" : target.getName();
        return super.toString() + " follow " + name;
    }
}

