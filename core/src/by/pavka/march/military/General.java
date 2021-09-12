package by.pavka.march.military;

import by.pavka.march.configuration.Ability;

public class General {
    public static final int BRIGADIER = 1;
    public static final int DIVISIONAL_GENERAL = 2;
    public static final int MARCHALL = 3;

    int rank;
    Ability ability;
    String name;

    public boolean assignTo(Formation formation) {
        //TODO:
        return true;
    }

    public boolean resign() {
        //TODO:
        return true;
    }
}
