package by.pavka.march.military;

import static by.pavka.march.characteristic.Stock.NORMAL_FOOD_STOCK_DAYS;

import by.pavka.march.characteristic.Spirit;
import by.pavka.march.characteristic.Stock;
import by.pavka.march.characteristic.Strength;
import by.pavka.march.configuration.Nation;

public abstract class Force {
    public static final String CAV = "cavalry";
    public static final String INF = "infantry";
    public static final String ENG = "engineer";
    public static final String ART = "artillery";
    public static final String SUP = "supply";
    public static final double MAX_SPEED = 36.0;

    Nation nation;
    String name;

    Strength strength;
    Spirit spirit;

    double speed;

    Formation superForce;

    public abstract double findSpeed();

    public abstract Spirit findSpirit();

    public abstract Stock changeStockDescending(Stock stock, int mode);

    public abstract Stock emptyStock();

    public Stock flattenEmptiedStock(Stock stock, int mode) {
        double foodRequest = findFoodNeed() * NORMAL_FOOD_STOCK_DAYS;
        double ammoRequest = findAmmoNeed()  * mode;
        double ratio = strength.capacity / (ammoRequest + foodRequest);
        double foodToLoad = foodRequest * ratio;
        double ammoToLoad = ammoRequest * ratio;
        if (ammoToLoad > stock.ammo) {
            ammoToLoad = stock.ammo;
            foodToLoad = strength.capacity - ammoToLoad;
        }
        if (foodToLoad > stock.food) {
            foodToLoad = stock.food;
            if (ammoToLoad < stock.ammo) {
                if (stock.ammo > strength.capacity - foodToLoad) {
                    ammoToLoad = strength.capacity - foodToLoad;
                } else {
                    ammoToLoad = stock.ammo;
                }
            }
        }
        strength.food = foodToLoad;
        strength.ammo = ammoToLoad;
        Stock remaining = new Stock(stock.food - foodToLoad, stock.ammo - ammoToLoad);
        double foodPortion = foodToLoad / findFoodNeed();
        double ammoPortion = ammoToLoad / findAmmoNeed();
        flatten(foodPortion, ammoPortion);
        return remaining;
    }

    public abstract void flatten(double foodPortion, double ammoPortion);

    public abstract double findAmmoNeed();

    public abstract double findFoodNeed();

    public abstract int getLevel();

    public boolean detach() {
        if (superForce == null) {
            return false;
        }
        superForce.remove(this);
        superForce.changeStrength(strength.reverse());
        superForce = null;
        speed = findSpeed();
        spirit = findSpirit();
        return true;
    }

}
