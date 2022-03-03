package by.pavka.march.military;

import static by.pavka.march.military.Force.ART;
import static by.pavka.march.military.Force.CAV;
import static by.pavka.march.military.Force.ENG;
import static by.pavka.march.military.Force.INF;
import static by.pavka.march.military.Force.SUP;

import by.pavka.march.characteristic.Strength;

public enum UnitType {

    TROOP(3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    INFANTRY_REGIMENT_HQ(3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    CAVALRY_REGIMENT_HQ(3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    INFANTRY_BRIGADE_HQ(3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    CAVALRY_BRIGADE_HQ(3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    DIVISION_HQ(3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    CAVALRY_DIVISION_HQ(3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    CORPS_HQ(3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    CAVALRY_CORPS_HQ(3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    ARMY_HQ(3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },

    INFANTRY(1, INF) {
        @Override
        public Strength getInitialStrength() {
            return new Strength(840, 0, 0, 0, 0, 1, 1,
                    120, 1, 6, 1, 1, 3, 3);
        }
    },
    LIGHT_INFANTRY(1, INF) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    GRENADIER(1, INF) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    GUARD_INFANTRY(1, INF) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    MILITIA(2, INF) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    MILITIA_COMPANY(2, INF) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    MARCH_COMPANY(2, INF) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },

    ENGINEER(2, ENG) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    ENGINEER_COMPANY(2, ENG) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },

    CAVALRY(1, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    HEAVY_CAVALRY(1, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    LIGHT_CAVALRY(2, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    IRREGULAR_CAVALRY(2, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    GUARD_CAVALRY(1, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },

    ARTILLERY(1, ART) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    LIGHT_ARTILLERY_PLATOON(1, ART) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    HORSE_ARTILLERY(1, ART) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    HEAVY_ARTILLERY(1, ART) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    GUARD_ARTILLERY(1, ART) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },

    SUPPLY(3,SUP) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    };

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

    public abstract Strength getInitialStrength();
}
