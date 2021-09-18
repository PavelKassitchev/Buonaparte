package by.pavka.march.characteristic;

import by.pavka.march.military.UnitType;

public class Quality {
    public final UnitType unitType;

    public final int strength;

    public final double fire;
    public final double charge;

    public final double speed;
    public final double length;
    public final double recon;

    public final double capacity;
    public final double ammoConsuption;
    public final double foodConsumption;

    public Quality(UnitType unitType, int strength, double fire, double charge,
                   double speed, double length, double recon, double capacity, double ammo, double food) {
        this.unitType = unitType;
        this.strength = strength;
        this.fire = fire;
        this.charge = charge;
        this.speed = speed;
        this.length = length;
        this.recon = recon;
        this.capacity = capacity;
        ammoConsuption = ammo;
        foodConsumption = food;
    }
}
