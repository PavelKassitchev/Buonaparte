package by.pavka.march.order;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.map.Hex;
import by.pavka.march.map.Path;
import by.pavka.march.military.Force;
import by.pavka.march.military.Formation;

public class GainOrder extends MoveOrder {
    public Hex originalDestination;
    public Array<Hex> destinations;
    public boolean additiveOrder;
    public boolean isSet;
    public Formation force;

    public GainOrder(Array<Hex> destinations, boolean additiveOrder) {
        this.destinations = new Array<>(destinations);
        if (!destinations.isEmpty()) {
            originalDestination = destinations.get(destinations.size - 1);
        }
        this.additiveOrder = additiveOrder;
    }

    public GainOrder() {

    }

    public void setDestinations(Array<Hex> dest) {
        destinations = new Array<>(dest);
        if (!destinations.isEmpty()) {
            originalDestination = destinations.get(destinations.size - 1);
        }
    }

    @Override
    public void visualize(Force force) {
        Order o = force.visualOrders.last();
        if (o instanceof MoveOrder) {
            if (additiveOrder) {
                if (!o.irrevocable) {
                    force.visualOrders.removeOrder(o);
                    o.revoked = true;
                }
            } else {
                force.visualOrders.removeMoveOrders();
            }
        }
        super.visualize(force);
    }

    @Override
    public boolean isExecutable(Force force) {
        if (force.hex != null) {
            return true;
        }
        return false;
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
        this.force = (Formation)force;
        GraphPath<Hex> hexPath;
        Array<Path> paths = new Array<>();
        Hex start = force.hex;
        for (Hex h : destinations) {
            hexPath = force.playScreen.getHexGraph().findPath(start, h);
            Hex st = null;
            Hex en;
            for (Hex hx : hexPath) {
                en = hx;
                if (st != null) {
                    paths.add(force.playScreen.getHexGraph().getPath(st, en));
                }
                st = hx;
            }
            start = st;
        }
        force.forcePath = paths;
        isSet = true;
    }

    @Override
    public boolean execute(Force force, float delta) {
        if (force == null) {
            return false;
        }
//        GraphPath<Hex> hexPath;
//        Array<Path> paths = new Array<>();
//        Hex start = force.hex;
//        for (Hex h : destinations) {
//            hexPath = force.playScreen.getHexGraph().findPath(start, h);
//            Hex st = null;
//            Hex en;
//            for (Hex hx : hexPath) {
//                en = hx;
//                if (st != null) {
//                    paths.add(force.playScreen.getHexGraph().getPath(st, en));
//                }
//                st = hx;
//            }
//            start = st;
//        }
//        force.forcePath = paths;
        if (force.forcePath != null && !force.forcePath.isEmpty() || !force.tail.isEmpty()) {
            force.move(delta);
        } else {
//            if (force.hex.hasForces()) {
            for (Force f : force.hex.getForces()) {
                this.force.attach(f, true);
            }
//            }
            force.actualOrders.fulfillOrder(this);
        }
        return true;
    }

    @Override
    public boolean execute(Array<Force> forces) {
        if (forces == null || forces.isEmpty()) {
            return false;
        }
        for (Force force : forces) {
            execute(force, 0);
        }
        return true;
    }

    @Override
    public void cancel(Force f) {
        Force.sendOrder(f, new RemoveDestinationsOrder(this), 50);
        System.out.println("Sending cancellation " + hashCode());
    }

}
