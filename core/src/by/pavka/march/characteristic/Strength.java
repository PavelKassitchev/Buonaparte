package by.pavka.march.characteristic;

import static by.pavka.march.military.Force.*;

import by.pavka.march.military.UnitType;

public class Strength {
    public int infantry;
    int engineer;
    int cavalry;
    int artillery;
    int supply;

    double fire;
    double charge;

    //double speed;
    public double length;
    double recon;

    public double capacity;
    public double foodConsumption;
    public double ammoConsumption;

    public double food;
    public double ammo;

    public Strength() {}

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
        switch(unitType.type()) {
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
}
