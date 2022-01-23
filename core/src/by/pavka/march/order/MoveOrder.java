package by.pavka.march.order;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.map.Hex;
import by.pavka.march.map.Path;
import by.pavka.march.military.Force;

public class MoveOrder extends Order {

    private Array<Hex> destinations;

    public MoveOrder(Array<Hex> destinations) {
        this.destinations = destinations;
    }

    public void setDestinations(Array<Hex> dest) {
        destinations = dest;
    }


    @Override
    public void receive(Force force) {
        if (force.actualOrders.first() instanceof MoveOrder && force.actualOrders.size() == 1) {
            System.out.println("AHA!");
            ((MoveOrder)force.actualOrders.first()).setDestinations(destinations);
            ((MoveOrder)force.actualOrders.first()).set(force);

        } else {
            super.receive(force);
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
}
