package by.pavka.march.military;

public class General extends Force implements Fighter{
    public static final int BRIGADIER = 1;
    public static final int DIVISIONAL_GENERAL = 2;
    public static final int MARCHALL = 3;

    int rank;

    @Override
    public int getFightRank() {
        return Fighter.LOW_PRIORITY;
    }
}
