package by.pavka.march.military;

import static by.pavka.march.military.Force.ART;
import static by.pavka.march.military.Force.CAV;
import static by.pavka.march.military.Force.ENG;
import static by.pavka.march.military.Force.INF;
import static by.pavka.march.military.Force.SUP;

import by.pavka.march.characteristic.Strength;

public enum UnitType implements Unitable {

    INFANTRY(1, INF) {
        @Override
        public Strength getInitialStrength() {
            return new Strength(840, 0, 0, 0, 0, 1, 1,
                    120, 1, 5, 1, 0.5, 3, 2);
        }

        @Override
        public String image() {
            return "fr_inf";
        }

        @Override
        public float speed() {
            return 4.0f;
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
            return new Strength(0, 0, 200, 0, 0, 0.08, 0.4,
                    100, 4, 3.15, 0.8, 0.04, 3, 0.15);
        }

        @Override
        public String image() {
            return "fr_cav";
        }

        @Override
        public float speed() {
            return 6.0f;
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
            return new Strength(0, 0, 0, 120, 0, 3, 0.01,
                    80, 0.2, 5, 0.4, 1.5, 1.4, 3.6);
        }

        @Override
        public String image() {
            return "fr_art";
        }

        @Override
        public float speed() {
            return 3.9f;
        }
    },
    LIGHT_ARTILLERY_PLATOON(1, ART) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    HORSE_ARTILLERY(1, CAV) {
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
    public String getTroopType() {
        return type;
    }

    public String image() {
        return null;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    public float speed() {
        return 0;
    }


    @Override
    public abstract Strength getInitialStrength();

    @Override
    public boolean canAttach(int size, Unitable unitable) {
        return false;
    }


}
