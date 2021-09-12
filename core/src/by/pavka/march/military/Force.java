package by.pavka.march.military;

import by.pavka.march.configuration.Nation;

public abstract class Force {
    public static final String CAV = "cavalry";
    public static final String INF = "infantry";
    public static final String ENG = "engineer";
    public static final String ART = "artillery";
    public static final String SUP = "supply";
    public static final double MAX_SPEED = 36.0;

    Nation nation;
    String name;

    double speed;
    double length;
    double recon;

    double capacity;
    double food;
    double ammo;
    double foodConsumption;
    double ammoConsumption;

    Formation superForce;

    public boolean isUnit() {
        return false;
    }

    public abstract double findSpeed();

    public boolean detach() {
        if (superForce == null) {
            return false;
        }
        superForce.subForces.removeValue(this, true);

        if (isUnit()) {
            Unit unit = (Unit) this;
            unit.formerSuper = superForce;
            switch (unit.quality.unitType.type()) {
                case CAV:
                    superForce.changeCavalryStrength(-unit.strength, -unit.length, -unit.recon, -unit.capacity,
                            -unit.foodConsumption, -unit.ammoConsumption);
                    break;
                case INF:
                    superForce.changeInfantryStrength(-unit.strength, -unit.length, -unit.recon, -unit.capacity,
                            -unit.foodConsumption, -unit.ammoConsumption);
                    break;
                case ART:
                    superForce.changeArtilleryStrength(-unit.strength, -unit.length, -unit.recon, -unit.capacity,
                            -unit.foodConsumption, -unit.ammoConsumption);
                    break;
                case SUP:
                    superForce.changeSupplyStrength(-unit.strength, -unit.length, -unit.recon, -unit.capacity,
                            -unit.foodConsumption, -unit.ammoConsumption);
                    break;
                case ENG:
                    superForce.changeEngineerStrength(-unit.strength, -unit.length, -unit.recon, -unit.capacity,
                            -unit.foodConsumption, -unit.ammoConsumption);
                default:
            }
        } else {
            Formation formation = (Formation) this;
            superForce.changeInfantryStrength(-formation.infantry, 0, 0, 0,
                    0, 0);
            superForce.changeCavalryStrength(-formation.cavalry, 0, 0, 0,
                    0, 0);
            superForce.changeEngineerStrength(-formation.engineer, 0, 0, 0,
                    0, 0);
            superForce.changeArtilleryStrength(-formation.gunner, 0, 0, 0,
                    0, 0);
            superForce.changeSupplyStrength(-formation.wagon, -formation.length, -formation.recon, -formation.capacity,
                    -formation.foodConsumption, -formation.ammoConsumption);

        }
        superForce.speed = superForce.findSpeed();
        superForce = null;

        return true;
    }

}
