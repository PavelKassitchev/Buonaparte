package by.pavka.march.order;

import static by.pavka.march.characteristic.March.REGULAR;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.map.Hex;
import by.pavka.march.military.Force;

public class RemoveDestinationsOrder extends CancellationOrder {
    public MoveOrder parentOrder;
    Array<Hex> destinations;

    public RemoveDestinationsOrder(MoveOrder parentOrder) {
        this.parentOrder = parentOrder;
        destinations = parentOrder.destinations;
    }

    @Override
    public void receive(Force force) {
        System.out.println("Cancellation received");
        force.visualOrders.removeDestinations(parentOrder);
        force.actualOrders.removeDestinations(parentOrder);
        force.setMarch(REGULAR);
        System.out.println("Destinations removed: " + parentOrder.destinations.size);
        if (parentOrder.isSet) {
            System.out.println("Cancelling order is set");
            force.forcePath.clear();
            force.actualOrders.fulfillOrder(parentOrder, false);
        } else {
            System.out.println("Cancelling order is NOT set");
            force.actualOrders.removeOrder(parentOrder);
        }

    }
}
