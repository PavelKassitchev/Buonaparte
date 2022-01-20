package by.pavka.march.order;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.map.Hex;
import by.pavka.march.map.Path;
import by.pavka.march.military.Force;

public class MoveOrder implements Order {

    private Array<Hex> destinations;

    public MoveOrder(Array<Hex> destinations) {
        this.destinations = destinations;
    }

    @Override
    public boolean execute(Force force) {
        if (force == null) {
            return false;
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
        return true;
    }

    @Override
    public boolean execute(Array<Force> forces) {
        if (forces == null || forces.isEmpty()) {
            return false;
        }
        for (Force force : forces) {
            execute(force);
        }
        return true;
    }
}
