package by.pavka.march.order;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.map.Hex;
import by.pavka.march.military.Force;

public class RemoveDestinationsOrder extends Order {
    public MoveOrder parentOrder;
    Array<Hex> destinations;

    public RemoveDestinationsOrder(MoveOrder parentOrder) {
        this.parentOrder = parentOrder;
        destinations = parentOrder.destinations;
    }

    @Override
    public void receive(Force force) {
        System.out.println("Cancellation received");
//        force.visualOrders.removeDestinations(parentOrder);
        force.actualOrders.removeDestinations(parentOrder);
        System.out.println("Destinations removed: " + parentOrder.destinations.size);
        if (parentOrder.isSet) {
            System.out.println("Cancelling order is set");
            force.forcePath.clear();
            force.actualOrders.fulfillOrder(parentOrder);
        } else {
            System.out.println("Cancelling order is NOT set");
            force.actualOrders.removeOrder(parentOrder);
        }

    }

    @Override
    public void visualize(Force force) {

    }

    @Override
    public void set(Force force) {

    }

    @Override
    public boolean execute(Force force, float delta) {
        return false;
    }

    @Override
    public boolean execute(Array<Force> forces) {
        return false;
    }
}
