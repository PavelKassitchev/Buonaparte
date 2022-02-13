package by.pavka.march.order;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.map.Hex;

public enum OrderType {
    EMPTY_ORDER {
        @Override
        public Order getOrder() {
            return null;
        }

        @Override
        public String toString() {
            return "NONE";
        }
    },
    MOVE_ORDER {
        @Override
        public Order getOrder() {
            return new MoveOrder(new Array<Hex>(), true);
        }
    },
    DETACH_FORCE_ORDER {
        @Override
        public Order getOrder() {
            return new DetachForceOrder();
        }
    };

    public abstract Order getOrder();

//    @Override
//    public String toString() {
//        return getOrder().toString();
//    }
}
