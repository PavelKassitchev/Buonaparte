package by.pavka.march.characteristic;

public enum March {
    REGULAR(0.5f, 0.35f),
    QUICK(0.75f, 0.725f),
    BATTLE(1.2f, 3.5f);

    public float speedFactor;
    public float fatigueFactor;

    March(float speedFactor, float fatigueFactor) {
        this.speedFactor = speedFactor;
        this.fatigueFactor = fatigueFactor;
    }
}
