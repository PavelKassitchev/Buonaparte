package by.pavka.march.military;

import by.pavka.march.characteristic.Spirit;
import by.pavka.march.characteristic.Strength;
import by.pavka.march.configuration.Nation;

public abstract class Force {
    public static final String CAV = "cavalry";
    public static final String INF = "infantry";
    public static final String ENG = "engineer";
    public static final String ART = "artillery";
    public static final String SUP = "supply";
    public static final double MAX_SPEED = 36.0;

    Nation nation;
    int level;
    String name;

    Strength strength;
    Spirit spirit;

    double speed;

    double food;
    double ammo;

    Formation superForce;

    public abstract double findSpeed();

    public abstract Spirit findSpirit();

    public boolean detach() {
        if (superForce == null) {
            return false;
        }
        superForce.remove(this);
        superForce.changeStrength(strength);
        superForce = null;
        speed = findSpeed();
        return true;
    }
}
