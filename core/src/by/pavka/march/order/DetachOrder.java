package by.pavka.march.order;

import by.pavka.march.military.Force;

public class DetachOrder implements Order {
    @Override
    public boolean execute(Force force) {
        if (force == null) {
            return false;
        }
         force.detach(true);
        return true;
    }
}
