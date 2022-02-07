package by.pavka.march.order;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.map.Hex;
import by.pavka.march.map.Path;
import by.pavka.march.military.Force;

public class MoveOrder extends Order {

    public final Hex originalDestination;
    public Array<Hex> destinations;
    public boolean additiveOrder;

    public MoveOrder(Array<Hex> destinations, boolean additiveOrder) {
        this.destinations = new Array<>(destinations);
        originalDestination = destinations.get(destinations.size - 1);
        this.additiveOrder = additiveOrder;
    }

    public void setDestinations(Array<Hex> dest) {
        destinations = dest;
    }


    @Override
    public void receive(Force force) {
        if (!canceled && !additiveOrder && force.actualOrders.first() instanceof MoveOrder) {
            System.out.println("AHA!");
            ((MoveOrder)force.actualOrders.first()).setDestinations(destinations);
            ((MoveOrder)force.actualOrders.first()).set(force);
            force.actualOrders.removeMoveOrders();

        } else {
            super.receive(force);
            System.out.println("OHO! " + force.actualOrders.size());
        }
    }

    @Override
    public void set(Force force) {
        if (force == null) {
            return;
        }
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
    public String toString() {
        String d;
        if (!destinations.isEmpty()) {
            d = destinations.get(destinations.size - 1).toString();
        } else {
            d = "";
        }
        return super.toString() + hashCode() + " " + d;
    }
}
