package by.pavka.march.military;

import static by.pavka.march.military.UnitType.GUNNER_PER_CANNON;
import static by.pavka.march.military.UnitType.SOLDIER_PER_WAGON;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.characteristic.Spirit;
import by.pavka.march.characteristic.Strength;

public class Formation extends Force {

    General commander;
    Array<Force> subForces;
    Array<General> generals;

//    int infantry;
//    int engineer;
//    int cavalry;
//    int gunner;
//    int cannon;
//    int wagon;
//
//    public Formation attach(Force force) {
//        force.superForce = this;
//        subForces.add(force);
//        if (speed > force.speed) {
//            speed = force.speed;
//        }
//        length += force.length;
//        recon += force.recon;
//        capacity += force.capacity;
//        food += force.food;
//        ammo += force.ammo;
//        foodConsumption += force.foodConsumption;
//        ammoConsumption += force.ammoConsumption;
//        if(force.isUnit()) {
//            Unit unit = (Unit) force;
//            int strength = unit.strength;
//            switch (unit.quality.unitType.type()) {
//                case INF:
//                    infantry += strength;
//                    break;
//                case CAV:
//                    cavalry += strength;
//                    break;
//                case ART:
//                    gunner += strength;
//                    cannon += Math.ceil(strength / GUNNER_PER_CANNON);
//                    break;
//                case ENG:
//                    engineer += strength;
//                    break;
//                case SUP:
//                    wagon += Math.ceil(strength / SOLDIER_PER_WAGON);
//                    break;
//                default:
//            }
//        } else {
//            Formation formation = (Formation) force;
//            infantry += formation.infantry;
//            cavalry += formation.cavalry;
//            engineer += formation.engineer;
//            gunner += formation.gunner;
//            cannon += formation.cannon;
//            wagon += formation.wagon;
//        }
//        return this;
//    }
//
//    public void changeInfantryStrength(int delta, double lengthDelta, double reconDelta,
//                                       double capacityDelta,double foodConsumptionDelta, double ammoConsumptionDelta) {
//        infantry += delta;
//        changeAttributes(lengthDelta, reconDelta, capacityDelta, foodConsumptionDelta, ammoConsumptionDelta);
//        if (superForce != null) {
//            superForce.changeInfantryStrength(delta, lengthDelta, reconDelta,
//                    capacityDelta, ammoConsumptionDelta, foodConsumptionDelta);
//        }
//    }
//
//    public void changeCavalryStrength(int delta, double lengthDelta, double reconDelta,
//                                      double capacityDelta,double foodConsumptionDelta, double ammoConsumptionDelta) {
//        cavalry += delta;
//        changeAttributes(lengthDelta, reconDelta, capacityDelta, foodConsumptionDelta, ammoConsumptionDelta);
//        if (superForce != null) {
//            superForce.changeCavalryStrength(delta, lengthDelta, reconDelta,
//                    capacityDelta, ammoConsumptionDelta, foodConsumptionDelta);
//        }
//    }
//
//    public void changeArtilleryStrength(int delta, double lengthDelta, double reconDelta,
//                                        double capacityDelta,double foodConsumptionDelta, double ammoConsumptionDelta) {
//        gunner += delta;
//        cannon += Math.ceil(delta / GUNNER_PER_CANNON);
//        changeAttributes(lengthDelta, reconDelta, capacityDelta, foodConsumptionDelta, ammoConsumptionDelta);
//        if (superForce != null) {
//            superForce.changeCavalryStrength(delta, lengthDelta, reconDelta,
//                    capacityDelta, ammoConsumptionDelta, foodConsumptionDelta);
//        }
//    }
//
//    public void changeSupplyStrength(int delta, double lengthDelta, double reconDelta,
//                                     double capacityDelta,double foodConsumptionDelta, double ammoConsumptionDelta) {
//        wagon += Math.ceil(delta / UnitType.SOLDIER_PER_WAGON);
//        changeAttributes(lengthDelta, reconDelta, capacityDelta, foodConsumptionDelta, ammoConsumptionDelta);
//        if (superForce != null) {
//            superForce.changeCavalryStrength(delta, lengthDelta, reconDelta,
//                    capacityDelta, ammoConsumptionDelta, foodConsumptionDelta);
//        }
//    }
//
//
//    public void changeEngineerStrength(int delta, double lengthDelta, double reconDelta,
//                                       double capacityDelta, double foodConsumptionDelta, double ammoConsumptionDelta) {
//        engineer += delta;
//        changeAttributes(lengthDelta, reconDelta, capacityDelta, foodConsumptionDelta, ammoConsumptionDelta);
//        if (superForce != null) {
//            superForce.changeEngineerStrength(delta, lengthDelta, reconDelta,
//                    capacityDelta, ammoConsumptionDelta, foodConsumptionDelta);
//        }
//    }
//
//    private void changeAttributes(double lengthDelta, double reconDelta,
//                                  double capacityDelta,double foodConsumptionDelta, double ammoConsumptionDelta) {
//        length += lengthDelta;
//        recon += reconDelta;
//        capacity += capacityDelta;
//        foodConsumption += foodConsumptionDelta;
//        ammoConsumption += ammoConsumptionDelta;
//    }
//
    @Override
    public double findSpeed() {
        double speed = MAX_SPEED;
        for (Force force: subForces) {
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

    public boolean remove(Force force) {
        return subForces.removeValue(force, true);
    }

    public void changeStrength(Strength strength) {
        this.strength.change(strength);
        speed = findSpeed();
        spirit = findSpirit();
        if (superForce != null) {
            superForce.changeStrength(strength);
        }
    }
}
