package by.pavka.march.military;

import static by.pavka.march.characteristic.Stock.NORMAL_FOOD_STOCK_DAYS;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.characteristic.Spirit;
import by.pavka.march.characteristic.Stock;
import by.pavka.march.characteristic.Strength;
import by.pavka.march.configuration.Nation;

public abstract class Force extends Image {
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
        double foodRequest = strength.foodConsumption * NORMAL_FOOD_STOCK_DAYS;
        double ammoRequest = strength.ammoConsumption  * mode;
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
        double foodRatio = foodToLoad / strength.capacity;
        double ammoRatio = ammoToLoad / strength.capacity;
        flatten(foodRatio, ammoRatio);
        return remaining;
    }

    public abstract void flatten(double foodRatio, double ammoRatio);

    public Stock absorbStock(Stock stock, int mode) {
        Stock distribution = emptyStock();
        distribution = distribution.plus(stock);
        return flattenEmptiedStock(distribution, mode);
    }

    public Array<WagonTrain> separateWagons() {
        Array<WagonTrain> train = new Array<>();
        addWagonToTrain(train);
        return train;
    }

    public abstract void addWagonToTrain(Array<WagonTrain> train);

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
