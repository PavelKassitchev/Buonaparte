package by.pavka.march.military;

import com.badlogic.gdx.utils.Array;

public class HQ extends Force implements Fighter {
    public static final int TROOP = 0;
    public static final int REGIMENT = 1;
    public static final int BRIGADE = 2;
    public static final int DIVISION = 3;
    public static final int CORPS = 4;
    public static final int ARMY = 5;

    int level;
    Array<Force> subForces;
    Array<General> generals;

    @Override
    public int getFightRank() {
        return Fighter.LOW_PRIORITY;
    }
}
