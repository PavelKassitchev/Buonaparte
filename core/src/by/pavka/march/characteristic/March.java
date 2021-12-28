package by.pavka.march.characteristic;

public enum March {
    REGULAR(0.5f, 0.75f),
    QUICK(0.75f, 1.125f),
    BATTLE(1.2f, 3.9f);

    public float speedFactor;
    public float fatigueFactor;

    March(float speedFactor, float fatigueFactor) {
        this.speedFactor = speedFactor;
        this.fatigueFactor = fatigueFactor;
    }
}
