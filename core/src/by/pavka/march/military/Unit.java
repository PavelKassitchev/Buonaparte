package by.pavka.march.military;

import static by.pavka.march.characteristic.Stock.NORMAL_FOOD_STOCK_DAYS;

import by.pavka.march.characteristic.Spirit;
import by.pavka.march.characteristic.Stock;
import by.pavka.march.characteristic.Strength;
import by.pavka.march.characteristic.Quality;

public class Unit extends Force {
    Quality quality;
    Formation formerSuper;

    @Override
    public double findSpeed() {
        return speed;
    }

    @Override
    public Spirit findSpirit() {
        return spirit;
    }

    @Override
    public Stock changeStockDescending(Stock stock, int mode) {
        return strength.changeStock(stock, mode);
    }

    @Override
    public Stock emptyStock() {
        double food = strength.food;
        double ammo = strength.ammo;
        strength.food = 0;
        strength.ammo = 0;
        return new Stock(food, ammo);
    }

    @Override
    public void flatten(double foodPortion, double ammoPortion) {
        strength.food = foodPortion * findFoodNeed();
        strength.ammo = ammoPortion * findAmmoNeed();
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public double findAmmoNeed() {
        return strength.ammoConsumption;
    }

    @Override
    public double findFoodNeed() {
        return strength.foodConsumption;
    }

    public Stock changeStockAscending(Stock stock, int mode) {
        Stock initial = new Stock(stock);
        Stock remaining = strength.changeStock(stock, mode);
        if (superForce != null) {
            superForce.changeStockAscending(initial.minus(remaining));
        }
        return remaining;
    }

    public Strength changeStrength(int soldiers) {
        Strength strength = this.strength.change(soldiers, quality.unitType);
        if (superForce != null) {
            superForce.changeStrength(strength);
        }
        return strength;
    }

}
