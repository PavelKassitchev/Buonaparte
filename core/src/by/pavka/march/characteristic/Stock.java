package by.pavka.march.characteristic;

public class Stock {
    public static final int MODE_DEFAULT = 0;
    public static final int AMMO_LOWEST = 8;
    public static final int AMMO_LOW = 12;
    public static final int AMMO_NORMAL = 16;
    public static final int AMMO_HIGH = 22;
    public static final int AMMO_HIGHEST = 28;
    public double food;
    public double ammo;

    public Stock(double food, double ammo) {
        this.food = food;
        this.ammo = ammo;
    }

    public Stock() {
        this(0, 0);
    }

    public Stock(Stock initial) {
        this(initial.food, initial.ammo);
    }

    public boolean isEmpty() {
        return food <= 0 && ammo <= 0;
    }

    public Stock minus(Stock stock) {
        return new Stock(food - stock.food, ammo - stock.ammo);
    }

    public Stock plus(Stock stock) {
        return new Stock(food + stock.food, ammo + stock.ammo);
    }
}
