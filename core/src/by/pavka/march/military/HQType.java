package by.pavka.march.military;

import static by.pavka.march.military.Force.ART;
import static by.pavka.march.military.Force.CAV;
import static by.pavka.march.military.Force.INF;

import by.pavka.march.characteristic.Strength;

public enum HQType implements Unitable {
    TROOP(3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    // fire 0.02
    INFANTRY_REGIMENT_HQ(1, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength(0, 0, 50, 0, 0, 0.02, 0.1,
                    20, 1, 0.85, 0.2, 0.01, 0.8, 0.05);
        }

        @Override
        public String image() {
            return "fr_inf";
        }

        @Override
        public boolean canAttach(int size, Unitable unitable) {
            if (unitable.getLevel() > 0 || size > 4 || unitable.getTroopType() != INF) {
                return false;
            }
            return true;
        }
    },
    // fire 0.02
    CAVALRY_REGIMENT_HQ(1, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength(0, 0, 50, 0, 0, 0.02, 0.1,
                    20, 1, 0.85, 0.2, 0.01, 0.8, 0.05);
        }

        @Override
        public String image() {
            return "fr_cav";
        }

        @Override
        public boolean canAttach(int size, Unitable unitable) {
            if (unitable.getLevel() > 0 || size > 4 || unitable.getTroopType() != CAV) {
                return false;
            }
            return true;
        }
    },
    ARTILLERY_REGIMENT_HQ(1, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength(0, 0, 50, 0, 0, 0.02, 0.1,
                    20, 1, 0.85, 0.2, 0.01, 0.8, 0.05);
        }
        @Override
        public String image() {
            return "fr_art";
        }

        @Override
        public boolean canAttach(int size, Unitable unitable) {
            if (unitable.getLevel() > 0 || size > 6 || unitable.getTroopType() != ART) {
                return false;
            }
            return true;
        }
    },
    // fire 0.02
    INFANTRY_BRIGADE_HQ(2, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength(0, 0, 50, 0, 0, 0.02, 0.1,
                    20, 2, 0.85, 0.2, 0.01, 0.8, 0.05);
        }

        @Override
        public String image() {
            return "fr_inf";
        }

        @Override
        public boolean canAttach(int size, Unitable unitable) {
            if (unitable.getLevel() > 1 || size > 4) {
                return false;
            }
            return true;
        }
    },
    // fire 0.02
    CAVALRY_BRIGADE_HQ(2, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength(0, 0, 50, 0, 0, 0.02, 0.1,
                    20, 2, 0.85, 0.2, 0.01, 0.8, 0.05);
        }

        @Override
        public String image() {
            return "fr_cav";
        }

        @Override
        public boolean canAttach(int size, Unitable unitable) {
            if (unitable.getTroopType() != CAV || unitable.getLevel() > 1 || size > 3) {
                return false;
            }
            return true;
        }
    },
    // fire 0.02
    DIVISION_HQ(3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength(0, 0, 50, 0, 0, 0.02, 0.1,
                    20, 3, 0.85, 0.2, 0.01, 0.8, 0.05);
        }

        @Override
        public String image() {
            return "fr_inf";
        }

        @Override
        public boolean canAttach(int size, Unitable unitable) {
            if (unitable.getLevel() > 2 || size > 6) {
                return false;
            }
            return true;
        }
    },
    CAVALRY_DIVISION_HQ(3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    CORPS_HQ(4, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    CAVALRY_CORPS_HQ(4, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    ARMY_HQ(5, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }

        @Override
        public boolean canAttach(int size, Unitable unitable) {
            return true;
        }
    };

    private int level;
//    private int fightRank;
    private String type;

    HQType(int level, String type) {
        this.level = level;
//        fightRank = rank;
        this.type = type;
    }

    public int fightRank() {
        return 4 + level / 2;
    }

    @Override
    public String getTroopType() {
        return type;
    }

    public String image() {
        return null;
    }

    @Override
    public int getLevel() {
        return level;
    }

    public float speed() {
        return 6.5f;
    }


    @Override
    public abstract Strength getInitialStrength();

    @Override
    public boolean canAttach(int size, Unitable unitable) {
        return true;
    }
}
