package by.pavka.march.characteristic;

import static by.pavka.march.military.Force.*;

import by.pavka.march.military.UnitType;

public class Strength {
    int infantry;
    int engineer;
    int cavalry;
    int artillery;
    int supply;

    double fire;
    double charge;

    //double speed;
    double length;
    double recon;

    double capacity;
    double foodConsumption;
    double ammoConsumption;

    public int soldiers() {
        return infantry + cavalry + engineer + artillery + supply;
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

        return strength;
    }
}
