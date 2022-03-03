package by.pavka.march.military;

import static by.pavka.march.PlayScreen.HOURS_IN_SECOND;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectFloatMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

import by.pavka.march.characteristic.March;
import by.pavka.march.characteristic.MarchConfig;
import by.pavka.march.characteristic.Quality;
import by.pavka.march.characteristic.Spirit;
import by.pavka.march.characteristic.Stock;
import by.pavka.march.characteristic.Strength;
import by.pavka.march.order.OrderList;

public class Unit extends Force {
    Quality quality;


    public Unit() {
    }

    @Override
    public void fatigue(float f) {
        spirit.fatigue += f;
    }

    @Override
    public void rest(float f) {
        spirit.fatigue -= f;
        if (spirit.fatigue < 0) {
            spirit.fatigue = 0;
        }
    }

//    public Unit(TextureRegion region, Hex hex) {
//        super(region, hex);
//    }

    public Unit(TextureRegion region) {
        super(region, 840, 1000);
        strength.recon = 20;
        speed = 4.0f;
        strength.food = 3;
        strength.foodConsumption = 1;
        strength.capacity = 5;
        //TODO Delete it
//        strength = new Strength();
//        strength.infantry = 855;
//        strength.length = 150;
    }

    public Unit(TextureRegion region, UnitType unitType) {
        super(region);
        strength = unitType.getInitialStrength();
        unmark();
        visualStrength = new Strength(strength);
        currentSections = 1;
        tail = new Array<>();
        visualTail = new Array<>();
        forcePath = new Array<>();
        visualForcePath = new Array<>();
        actualOrders = new OrderList(this);
        visualOrders = new OrderList(this);
        visualEnemies = new ObjectMap<>();
        reconArea = new ObjectSet<>();
        scoutMap = new ObjectFloatMap<>();

        spirit = new Spirit(0, 1, 0);

        visualSpirit = new Spirit(spirit);

        viewStrength = new Strength(strength);
        viewSpirit = new Spirit(spirit);

        marchConfig = new MarchConfig(March.REGULAR);
    }



    @Override
    public float findSpeed() {
        return speed;
    }

    @Override
    public Spirit findSpirit() {
        return spirit;
    }

    @Override
    public void eat(float delta) {
        double f = delta * HOURS_IN_SECOND / 24 * strength.foodConsumption;
        if (strength.food >= f) {
            reduceFoodAscending(f);
        } else {
            reduceFoodAscending(strength.food);
                    //TODO
        }
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
    public void flattenClearAll(double fRatio, double aRatio) {
        strength.food = strength.capacity * fRatio;
        strength.ammo = strength.capacity * aRatio;
    }


    @Override
    public void flatten(double foodRatio, double ammoRatio) {
        strength.food = foodRatio * strength.capacity;
        strength.ammo = ammoRatio * strength.capacity;
    }

    @Override
    public void copyStructure() {
        interStrength = new Strength(strength);
        interSpirit = new Spirit(spirit);
    }

    @Override
    public Force copyForce() {
        Force copy = new Unit();
        Strength interSt = new Strength(strength);
        Spirit interSp = new Spirit(spirit);
        copy.strength = interSt;
        copy.spirit = interSp;
        return copy;
    }

    @Override
    public void visualizeStructure() {
        visualStrength = new Strength(interStrength);
        visualSpirit = new Spirit(interSpirit);
    }

    @Override
    public void visualizeCopy(Force copy) {
        visualStrength = copy.strength;
        visualSpirit = copy.spirit;
    }

    @Override
    public void setTreeViewStructure() {
        viewStrength = new Strength(visualStrength);
        viewSpirit = new Spirit(visualSpirit);
    }

    @Override
    public void addWagonToTrain(Array<WagonTrain> train) {

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
