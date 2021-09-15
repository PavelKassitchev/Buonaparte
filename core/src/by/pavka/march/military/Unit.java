package by.pavka.march.military;

import by.pavka.march.characteristic.Spirit;
import by.pavka.march.characteristic.Strength;
import by.pavka.march.configuration.Quality;

public class Unit extends Force {
    Quality quality;
    Formation formerSuper;

    @Override
    public double findSpeed() {
        return speed;
    }

    @Override
    public Spirit findSpirit() {
        return spirit;
    }

    public Strength changeStrength(int soldiers) {
        Strength strength = this.strength.change(soldiers, quality.unitType);
        if (superForce != null) {
            superForce.changeStrength(strength);
        }
        return strength;
    }
}
