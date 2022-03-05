package by.pavka.march.military;

import static by.pavka.march.military.Force.CAV;

import by.pavka.march.characteristic.Strength;

public enum HQType implements Unitable {
    TROOP(3, 3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    INFANTRY_REGIMENT_HQ(1, 3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength(0, 0, 50, 0, 0, 0.02, 0.1,
                    20, 1, 0.85, 0.2, 0.01, 0.8, 0.05);
        }

        @Override
        public String image() {
            return "fr_inf";
        }
    },
    CAVALRY_REGIMENT_HQ(1, 3, CAV) {
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
            return true;
        }
    },
    ARTILLERY_REGIMENT_HQ(1, 3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    INFANTRY_BRIGADE_HQ(2, 3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    CAVALRY_BRIGADE_HQ(2, 3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    DIVISION_HQ(3, 3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    CAVALRY_DIVISION_HQ(3, 3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    CORPS_HQ(4, 3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    CAVALRY_CORPS_HQ(4, 3, CAV) {
        @Override
        public Strength getInitialStrength() {
            return new Strength();
        }
    },
    ARMY_HQ(5, 3, CAV) {
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
    private int fightRank;
    private String type;

    HQType(int level, int rank, String type) {
        this.level = level;
        fightRank = rank;
        this.type = type;
    }

    public int fightRank() {
        return fightRank;
    }
    public String type() {
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
