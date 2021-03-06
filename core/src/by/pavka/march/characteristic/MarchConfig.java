package by.pavka.march.characteristic;

import by.pavka.march.map.Direction;
import by.pavka.march.map.Hex;

public class MarchConfig {
    public static final int SMALLEST_FORCE = 1400;
    public static final int SMALL_FORCE = 4000;
    public static final int MEDIUM_FORCE = 8000;
    public static final int BIG_FORCE = 12000;
    public static final int BIGGEST_FORCE = 16000;
    public static final int HUGE_FORCE = 20000;

    public static final float REST_FACTOR = 0.4f;

    int size;
    int phases;
    int currentPhase;
    Hex head;
    Direction headDirection;
    Hex body;
    Hex tail;
    Direction tailDirection;

    March march;
    float hoursInMove;
    int daysInMove;


    public void setSize(Strength strength) {
        double length = strength.length;
        if (length < SMALLEST_FORCE) {
            size = SMALLEST_FORCE;
            return;
        }
        if (length < SMALL_FORCE) {
            size = SMALL_FORCE;
            return;
        }
        if (length < MEDIUM_FORCE) {
            size = MEDIUM_FORCE;
            return;
        }
        if (length < BIG_FORCE) {
            size = BIG_FORCE;
            return;
        }
        if (length < BIGGEST_FORCE) {
            size = BIGGEST_FORCE;
            return;
        }
        size = HUGE_FORCE;
    }

    public MarchConfig(March march) {
        this.march = march;
    }

    public void setMarch(March march) {
        this.march = march;
    }

    public March getMarch() {
        return march;
    }

    public float fatigueFactor() {
        return march.fatigueFactor;
    }

    public float speedFactor() {
        return march.speedFactor;
    }
}
