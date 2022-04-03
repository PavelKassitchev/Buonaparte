package by.pavka.march.characteristic;

import static by.pavka.march.military.Force.*;

import by.pavka.march.military.UnitType;

public class Strength {
    public static final Strength EMPTY_STRENGTH = new Strength(0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0);
    public int infantry;
    public int engineer;
    public int cavalry;
    public int artillery;
    public int supply;

    public double fire;
    public double charge;

    //double speed;
    public double length;
    public double recon;

    public double capacity;
    public double foodConsumption;
    public double ammoConsumption;

    public double food;
    public double ammo;

    public Strength() {}

    public Strength(Strength copy) {
        this.infantry = copy.infantry;
        this.engineer = copy.engineer;
        this.cavalry = copy.cavalry;
        this.artillery = copy.artillery;
        this.supply = copy.supply;
        this.fire = copy.fire;
        this.charge = copy.charge;
        this.length = copy.length;
        this.recon = copy.recon;
        this.capacity = copy.capacity;
        this.foodConsumption = copy.foodConsumption;
        this.ammoConsumption = copy.ammoConsumption;
        this.food = copy.food;
        this.ammo = copy.ammo;
    }

    public Strength(int infantry, int engineer, int cavalry, int artillery, int supply, double fire, double charge,
                    double length, double recon, double capacity, double foodConsumption, double ammoConsumption, double food, double ammo) {
        this.infantry = infantry;
        this.engineer = engineer;
        this.cavalry = cavalry;
        this.artillery = artillery;
        this.supply = supply;
        this.fire = fire;
        this.charge = charge;
        this.length = length;
        this.recon = recon;
        this.capacity = capacity;
        this.foodConsumption = foodConsumption;
        this.ammoConsumption = ammoConsumption;
        this.food = food;
        this.ammo = ammo;
    }

    public int soldiers() {
        return infantry + cavalry + engineer + artillery + supply;
    }

    public double freeCapacity() {
        return capacity - food - ammo;
    }

    public Strength reverse() {
        return new Strength(-infantry, -engineer, -cavalry, -artillery, -supply, -fire, -charge,
                -length, -recon, -capacity, -foodConsumption, -ammoConsumption, -food, -ammo);
    }

    public void change(Strength strength) {
        infantry += strength.infantry;
        engineer += strength.engineer;
        cavalry += strength.cavalry;
        artillery += strength.artillery;
        supply += strength.supply;
        fire += strength.fire;
        charge+= strength.charge;
        recon += strength.recon;
        length += strength.length;
        capacity += strength.capacity;
        foodConsumption += strength.foodConsumption;
        ammoConsumption += strength.ammoConsumption;
        food += strength.food;
        ammo += strength.ammo;
    }

    public Strength change(int soldiers, UnitType unitType) {
        Strength strength = new Strength();
        double ratio;
        switch(unitType.getTroopType()) {
            case INF:
                ratio = (soldiers + 0.0) / infantry;
                strength.infantry = soldiers;
                infantry += soldiers;
                break;
            case CAV:
                ratio = (soldiers + 0.0) / cavalry;
                strength.cavalry = soldiers;
                cavalry += soldiers;
                break;
            case ENG:
                ratio = (soldiers + 0.0) / engineer;
                strength.engineer = soldiers;
                engineer += soldiers;
                break;
            case ART:
                ratio = (soldiers + 0.0) / artillery;
                strength.artillery = soldiers;
                artillery += soldiers;
                break;
            case SUP:
                ratio = (soldiers + 0.0) / supply;
                strength.supply = soldiers;
                supply += soldiers;
                break;
            default:
                ratio = 0;
        }
        strength.fire = fire * ratio;
        fire += strength.fire;
        strength.charge = charge * ratio;
        charge += strength.charge;
        strength.length = length * ratio;
        length += strength.length;
        strength.recon = recon * ratio;
        recon += strength.recon;
        strength.capacity = capacity * ratio;
        capacity += strength.capacity;
        strength.foodConsumption = foodConsumption * ratio;
        foodConsumption += strength.foodConsumption;
        strength.ammoConsumption = ammoConsumption * ratio;
        ammoConsumption += strength.ammoConsumption;
        strength.food = food * ratio;
        food += strength.food;
        strength.ammo = ammo * ratio;
        ammo += strength.ammo;

        return strength;
    }

    public Stock changeStock(Stock stock, int mode) {
        if (freeCapacity() < stock.food + stock.ammo) {
            switch (mode) {
                case Stock.MODE_DEFAULT:
                    double ratio = freeCapacity() / (stock.food + stock.ammo);
                    food += ratio * stock.food;
                    ammo = ratio * stock.ammo;
                    return new Stock(stock.food * (1 - ratio), stock.ammo * (1 - ratio));
            }
        }

        double foodResult = food + stock.food;
        if (foodResult <= 0) {
            food = 0;
        } else {
            food = foodResult;
            foodResult = 0;
        }
        double ammoResult = ammo + stock.ammo;
        if (ammoResult <= 0) {
            ammo = 0;
        } else {
            ammo = ammoResult;
            ammoResult = 0;
        }
        return new Stock(foodResult, ammoResult);
    }

    public Strength sufferLosses(double factor) {
//        int initialSoldiers = soldiers();
//        double initialFire = fire;
        factor *= 0.5 + Math.random();
//        System.out.println("Inside Strength factor becomes " + factor);
        if (factor > 0.99) {
            factor = 0.99;
        }
        int losses = (int) (soldiers() * factor);
//        System.out.println("Inside Strength raw losses = " + losses + " without rounding " + soldiers() * factor);
        if (losses < 1) {
            double rand = Math.random();
//            System.out.println("Random = " + rand);
            if (rand < soldiers() * factor) {
                losses = 1;
            }
        }
        double effectiveFactor = losses / (soldiers() + 0.0);
        int infLosses = infantry == 0 ? 0 : losses;
        infantry -= infLosses;
        int engLosses = engineer == 0 ? 0 : losses;
        engineer -= engLosses;
        int cavLosses = cavalry == 0 ? 0 : losses;
        cavalry -= cavLosses;
        int artLosses = artillery == 0 ? 0 : losses;
        artillery -= artLosses;
        int supLosses = supply == 0 ? 0 : losses;
        supply -= supLosses;
        double fireLosses = fire * effectiveFactor;
        fire -= fireLosses;
        double chargeLosses = charge * effectiveFactor;
        charge -= chargeLosses;
        double lengthLosses = length * effectiveFactor;
        length -= lengthLosses;
        double reconLosses = recon * effectiveFactor;
        recon -= reconLosses;
        double capacityLosses = capacity * effectiveFactor;
        capacity -= capacityLosses;
        double foodConsumptionLosses = foodConsumption * effectiveFactor;
        foodConsumption -= foodConsumptionLosses;
        double ammoConsumptionLosses = ammoConsumption * effectiveFactor;
        ammoConsumption -= ammoConsumptionLosses;
        double foodLosses = food * effectiveFactor;
        food -= foodLosses;
        double ammoLosses = ammo * effectiveFactor;
        ammo -= ammoLosses;

//        System.out.println("Inside Strength initial Soldiers = " + initialSoldiers +
//                " initial Fire = " + initialFire + " end soldiers = " + soldiers() + " end fire = " + fire +
//                " inRatio " + initialFire / initialSoldiers + " outRatio " + fire / soldiers());
        return new Strength(infLosses, engLosses, cavLosses, artLosses, supLosses, fireLosses, chargeLosses, lengthLosses,
                reconLosses, capacityLosses, foodConsumptionLosses, ammoConsumptionLosses, foodLosses, ammoLosses);
    }

    public Strength plus(Strength str) {
        int infantry = this.infantry + str.infantry;
        int engineer = this.engineer + str.engineer;
        int cavalry = this.cavalry + str.cavalry;
        int artillery = this.artillery + str.artillery;
        int supply = this.supply + str.supply;
        double fire = this.fire + str.fire;
        double charge = this.charge + str.charge;
        double length = this.length + str.length;
        double recon = this.recon + str.recon;
        double capacity = this.capacity + str.capacity;
        double foodConsumption = this.foodConsumption + str.foodConsumption;
        double ammoConsumption = this.ammoConsumption + str.ammoConsumption;
        double food = this.food + str.food;
        double ammo = this.ammo + str.ammo;
        return new Strength(infantry, engineer, cavalry, artillery, supply, fire, charge, length, recon,
                capacity, foodConsumption, ammoConsumption, food, ammo);
    }

    @Override
    public String toString() {
        return "Soldiers " + soldiers() + " fire " + fire;
    }
}
