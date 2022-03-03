package by.pavka.march.military;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.characteristic.Spirit;
import by.pavka.march.characteristic.Stock;
import by.pavka.march.characteristic.Strength;
import by.pavka.march.configuration.FormationValidator;

public class Formation extends Force {

    General commander;
    HQ hq;
    public Array<Force> subForces;
    public Array<Force> interForces;
    public Array<Force> visualForces;
    public Array<Force> viewForces;
    Array<Force> detachedForces;
    public FormationValidator validator;
    public boolean structureChanged;
    public Formation headForce;

    public Formation(Formation headForce) {
        this.headForce = headForce;
        subForces = new Array<>();
    }

    @Override
    public void fatigue(float f) {
        for (Force force : subForces) {
            force.fatigue(f);
        }
    }

    @Override
    public void rest(float f) {
        for (Force force : subForces) {
            force.rest(f);
        }
    }

    public Formation(Formation headForce, TextureRegion region) {
        super(region, 20000, 6000);
        this.headForce = headForce;
        subForces = new Array<>();
        interForces = new Array<>();
        visualForces = new Array<>();
        viewForces = new Array<>();
        speed = 3.0f;

    }

    public Formation(TextureRegion region) {
        super(region, 20000, 6000);
        subForces = new Array<>();
        interForces = new Array<>();
        visualForces = new Array<>();
        viewForces = new Array<>();
        speed = 3.0f;
//        strength.capacity = 1;
    }

    @Override
    public float findSpeed() {
        float speed = MAX_SPEED;
        if (this.speed < speed) {
            speed = this.speed;
        }
        for (Force force : subForces) {
            if (force.findSpeed() < speed) {
                speed = force.findSpeed();
            }
        }
        return speed;
    }

    @Override
    public Spirit findSpirit() {
        int soldiers = 0;
        double totalXp = 0;
        double totalMorale = 0;
        double totalFatigue = 0;
        for (Force force : subForces) {
            int number = force.strength.soldiers();
            soldiers += number;
            Spirit sp = force.findSpirit();
            totalXp += sp.xp * number;
            totalMorale += sp.morale * number;
            totalFatigue += sp.fatigue * number;
        }
        return new Spirit(totalXp / soldiers, totalMorale / soldiers, totalFatigue / soldiers);
    }

    @Override
    public void eat(float delta) {
        for (Force sub : subForces) {
            sub.eat(delta);
        }
//        flatten();
    }

    public void changeStockAscending(Stock stock) {
        strength.food += strength.food;
        strength.ammo += strength.ammo;
        if (superForce != null) {
            superForce.changeStockAscending(stock);
        }
    }

    @Override
    public Stock changeStockDescending(Stock stock, int mode) {
        //TODO
        return null;
    }

    @Override
    public Stock emptyStock() {
        Stock stock = new Stock(strength.food, strength.ammo);
        strength.food = 0;
        strength.ammo = 0;
        for (Force force : subForces) {
            force.emptyStock();
        }
//        System.out.println(getName() + " food stock emptied = " + stock.food);
        return stock;
    }

    @Override
    public void flattenClearAll(double fRatio, double aRatio) {
        strength.food = fRatio * strength.capacity;
        strength.ammo = aRatio * strength.capacity;
        for (Force sub : subForces) {
            sub.flattenClearAll(fRatio, aRatio);
        }
    }

    @Override
    public void flatten(double foodRatio, double ammoRatio) {
        strength.food = foodRatio * strength.capacity;
        strength.ammo = ammoRatio * strength.capacity;
        for (Force force : subForces) {
            force.flatten(foodRatio, ammoRatio);
        }
    }

    @Override
    public void addWagonToTrain(Array<WagonTrain> train) {
        for (int i = 0; i < subForces.size; i++) {
            subForces.get(i).addWagonToTrain(train);
        }
    }

    @Override
    public int getLevel() {
        switch (hq.quality.unitType) {
            case INFANTRY_REGIMENT_HQ:
            case CAVALRY_REGIMENT_HQ:
                return 1;
            case INFANTRY_BRIGADE_HQ:
            case CAVALRY_BRIGADE_HQ:
                return 2;
            case DIVISION_HQ:
            case CAVALRY_DIVISION_HQ:
                return 3;
            case CORPS_HQ:
            case CAVALRY_CORPS_HQ:
            case TROOP:
                return 4;
            case ARMY_HQ:
                return 5;
            default:
                return 0;
        }
    }

    @Override
    public double findAmmoNeed() {
        double need = 0;
        for (Force force : subForces) {
            need += force.findAmmoNeed();
        }
        return need;
    }

    @Override
    public double findFoodNeed() {
        double need = 0;
        for (Force force : subForces) {
            need += force.findFoodNeed();
        }
        return need;
    }

    public boolean remove(Force force) {
        structureChanged = true;
        return subForces.removeValue(force, true);
    }

    public void add(Force force) {
        subForces.add(force);
    }

    public void changeStrength(Strength strength) {
        this.strength.change(strength);
        //TODO RESTORE or reformat
        speed = findSpeed();
        spirit = findSpirit();
        if (superForce != null) {
            superForce.changeStrength(strength);
        }
    }

    public Formation attach(Force force, boolean physical) {
//        if (validator.canAttach(force)) {
        if (true){
            add(force);
            viewForces.add(force);
//            force.shapeRenderer = null;
            if (physical) {
                force.superForce = this;
//                force.remoteHeadForce = null;
                if (force.hex != null) {
                    force.hex.getForces().removeValue(force, true);
                }
                force.hex = null;
//                force.actualOrders.clear();
                System.out.println("Force is attached");
            }
            Strength s = force.strength;
            changeStrength(s);
            structureChanged = true;
//            if (getName().equals("IV.Cav.Div.")) {
//                System.out.println("Capacity: " + strength.capacity + " Food: " + strength.food);
//            }
        }
        return this;
    }

    @Override
    public void copyStructure() {
        interStrength = new Strength(strength);
        interSpirit = new Spirit(spirit);
        interForces = new Array<>();
        for (Force force : subForces) {
            interForces.add(force);
            force.copyStructure();
        }
    }

    @Override
    public Formation copyForce() {
        Formation copy = new Formation(headForce);
        Strength interSt = new Strength(strength);
        Spirit interSp = new Spirit(spirit);
        copy.strength = interSt;
        copy.spirit = interSp;
        Array<Force> interF = new Array<>();
        copy.subForces = interF;
        for (Force force : subForces) {
            interF.add(force);
            force.copyForce();
        }
        return copy;
    }

    @Override
    public void visualizeStructure() {
        visualStrength = new Strength(interStrength);
        visualSpirit = new Spirit(interSpirit);
        visualForces = new Array<>();
        for (Force force : interForces) {
            visualForces.add(force);
            force.visualizeStructure();
        }
    }

    @Override
    public void visualizeCopy(Force copy) {
        visualStrength = new Strength(copy.strength);
        visualSpirit = new Spirit(copy.spirit);
        visualForces = new Array<>();
        for (Force force : ((Formation)copy).subForces) {
            visualForces.add(force);
            force.visualizeCopy(force);
        }
    }

    @Override
    public void setTreeViewStructure() {
        viewStrength = new Strength(visualStrength);
        viewSpirit = new Spirit(visualSpirit);
        viewForces = new Array<>();
        for (Force force : visualForces) {
            viewForces.add(force);
            force.setTreeViewStructure();
        }
    }
}
