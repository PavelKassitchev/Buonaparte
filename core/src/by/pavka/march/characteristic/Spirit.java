package by.pavka.march.characteristic;

public class Spirit {
    public double xp;
    public double morale;
    public double fatigue;

    public Spirit(Spirit copy) {
        this.xp = copy.xp;
        this.morale = copy.morale;
        this.fatigue = copy.fatigue;
    }

    public Spirit(double xp, double morale, double fatigue) {
        this.xp = xp;
        this.morale = morale;
        this.fatigue = fatigue;
    }

    public void change(double x, double m, double f) {
        xp += x;
        morale += m;
        fatigue += f;
    }
}
