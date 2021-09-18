package by.pavka.march.military;

import static by.pavka.march.military.Force.*;

public enum UnitType {

    TROOP(3, CAV),
    INFANTRY_REGIMENT_HQ(3, CAV),
    CAVALRY_REGIMENT_HQ(3, CAV),
    INFANTRY_BRIGADE_HQ(3, CAV),
    CAVALRY_BRIGADE_HQ(3, CAV),
    DIVISION_HQ(3, CAV),
    CAVALRY_DIVISION_HQ(3, CAV),
    CORPS_HQ(3, CAV),
    CAVALRY_CORPS_HQ(3, CAV),
    ARMY_HQ(3, CAV),

    INFANTRY(1, INF),
    LIGHT_INFANTRY(1, INF),
    GRENADIER(1, INF),
    GUARD_INFANTRY(1, INF),
    MILITIA(2, INF),
    MILITIA_COMPANY(2, INF),
    MARCH_COMPANY(2, INF),

    ENGINEER(2, ENG),
    ENGINEER_COMPANY(2, ENG),

    CAVALRY(1, CAV),
    HEAVY_CAVALRY(1, CAV),
    LIGHT_CAVALRY(2, CAV),
    IRREGULAR_CAVALRY(2, CAV),
    GUARD_CAVALRY(1, CAV),

    ARTILLERY(1, ART),
    LIGHT_ARTILLERY_PLATOON(1, ART),
    HORSE_ARTILLERY(1, ART),
    HEAVY_ARTILLERY(1, ART),
    GUARD_ARTILLERY(1, ART),

    SUPPLY(3,SUP);

    public static final double GUNNER_PER_CANNON = 15.0;
    public static final double WAGONER_PER_WAGON = 2.0;

    private int fightRank;
    private String type;

    UnitType(int rank, String type) {
        fightRank = rank;
        this.type = type;
    }

    public int fightRank() {
        return fightRank;
    }
    public String type() {
        return type;
    }
}
