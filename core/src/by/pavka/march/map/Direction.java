package by.pavka.march.map;

public enum Direction {
    NORTH {
        @Override
        public Direction opposite() {
            return SOUTH;
        }
    },
    NORTHEAST {
        @Override
        public Direction opposite() {
            return SOUTHWEST;
        }
    },
    SOUTHEAST {
        @Override
        public Direction opposite() {
            return NORTHWEST;
        }
    },
    SOUTH {
        @Override
        public Direction opposite() {
            return NORTH;
        }
    },
    SOUTHWEST {
        @Override
        public Direction opposite() {
            return NORTHEAST;
        }
    },
    NORTHWEST {
        @Override
        public Direction opposite() {
            return SOUTHEAST;
        }
    },
    CENTER;

    public Direction opposite() {
        return CENTER;
    }
}
