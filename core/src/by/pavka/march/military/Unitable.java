package by.pavka.march.military;

import by.pavka.march.characteristic.Strength;

public interface Unitable {
    boolean canAttach(int size, Unitable unitable);
    Strength getInitialStrength();
    float speed();
    String image();
    int getLevel();
    String getTroopType();
    int fightRank();
}
