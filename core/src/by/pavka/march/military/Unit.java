package by.pavka.march.military;

import by.pavka.march.configuration.Quality;

public class Unit extends Force {
    Quality quality;
    Formation formerSuper;

    int strength;

    int xp;
    double morale;
    double fatigue;

    double fire;
    double charge;

    @Override
    public boolean isUnit() {
        return true;
    }

    @Override
    public double findSpeed() {
        return speed;
    }

    public void changeStrength(int delta) {
        double ratio = (delta + 0.0) / strength;
        strength += delta;
        double fireDelta = fire * ratio;
        fire += fireDelta;
        double chargeDelta = charge * ratio;
        charge += chargeDelta;
        double lengthDelta = length * ratio;
        length += lengthDelta;
        double reconDelta = recon * ratio;
        recon += reconDelta;
        double capacityDelta = capacity * ratio;
        capacity += capacityDelta;
        double foodConsumptionDelta = foodConsumption * ratio;
        foodConsumption += foodConsumptionDelta;
        double ammoConsumptionDelta = ammoConsumption * ratio;
        ammoConsumption += ammoConsumptionDelta;
        if (superForce != null) {
            switch (quality.unitType.type()) {
                case CAV:
                    superForce.changeCavalryStrength(delta, lengthDelta, reconDelta, capacityDelta,
                            foodConsumptionDelta, ammoConsumptionDelta);
                    break;
                case INF:
                    superForce.changeInfantryStrength(delta, lengthDelta, reconDelta, capacityDelta,
                            foodConsumptionDelta, ammoConsumptionDelta);
                    break;
                case ART:
                    superForce.changeArtilleryStrength(delta, lengthDelta, reconDelta, capacityDelta,
                            foodConsumptionDelta, ammoConsumptionDelta);
                    break;
                case SUP:
                    superForce.changeSupplyStrength(delta, lengthDelta, reconDelta, capacityDelta,
                            foodConsumptionDelta, ammoConsumptionDelta);
                    break;
                case ENG:
                    superForce.changeEngineerStrength(delta, lengthDelta, reconDelta, capacityDelta,
                            foodConsumptionDelta, ammoConsumptionDelta);
                default:
            }
        }
    }
}
