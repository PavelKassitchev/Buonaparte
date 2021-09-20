package by.pavka.march.military;

import static by.pavka.march.characteristic.Stock.AMMO_NORMAL;
import static by.pavka.march.characteristic.Stock.NORMAL_FOOD_STOCK_DAYS;

import by.pavka.march.characteristic.Stock;

public class WagonTrain extends Unit {
    public static final int WAGON_FACTOR = 6;

    @Override
    public double findAmmoNeed() {
        if (superForce == null) {
            return super.findAmmoNeed() * WAGON_FACTOR;
        }
        double ammoRatio = superForce.strength.ammoConsumption  * AMMO_NORMAL;
        System.out.println("Ammo ratio = " + ammoRatio);
        double foodRatio = superForce.strength.foodConsumption * NORMAL_FOOD_STOCK_DAYS;
        System.out.println("Food ratio = " + foodRatio);
        double ratio = strength.capacity / (ammoRatio + foodRatio);
        System.out.println("Ratio = " + ratio + ", need = " + ammoRatio * ratio / AMMO_NORMAL);
        return ammoRatio * ratio / AMMO_NORMAL;
    }

    @Override
    public double findFoodNeed() {
        return (strength.capacity - findAmmoNeed() * AMMO_NORMAL) / NORMAL_FOOD_STOCK_DAYS;
    }
}
