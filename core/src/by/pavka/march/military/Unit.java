package by.pavka.march.military;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.characteristic.Quality;
import by.pavka.march.characteristic.Spirit;
import by.pavka.march.characteristic.Stock;
import by.pavka.march.characteristic.Strength;

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
        //TODO Delete it
//        strength = new Strength();
//        strength.infantry = 855;
//        strength.length = 150;
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
