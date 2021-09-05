package by.pavka.march.military;

public enum UnitType {

    INFANTRY(1),
    LIGHT_INFANTRY(1),
    GRENADIER(1),
    GUARD_INFANTRY(1),
    ENGINEER(2),
    MILITIA(2),

    CAVALRY(1),
    HEAVY_CAVALRY(1),
    LIGHT_CAVALRY(2),
    IRREGULAR_CAVALRY(2),
    GUARD_CAVALRY(1),

    ARTILLERY(1),
    HORSE_ARTILLERY(1),
    HEAVY_ARTILLERY(1),
    GUARD_ARTILLERY(1),

    SUPPLY(3);

    private int fightRank;

    UnitType(int rank) {
        fightRank = rank;
    }

    public int fightRank() {
        return fightRank;
    }
}
