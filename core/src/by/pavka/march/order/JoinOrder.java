package by.pavka.march.order;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.map.Hex;
import by.pavka.march.military.Force;

public class JoinOrder extends MoveOrder {
    public Force target;

    public JoinOrder(Force target) {
        this.target = target;
        Array<Hex> destinations = new Array<>();
        destinations.add(target.hex);
        setDestinations(destinations);
    }
}
