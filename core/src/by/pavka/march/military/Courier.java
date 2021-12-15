package by.pavka.march.military;

import by.pavka.march.PlayScreen;
import by.pavka.march.map.Hex;
import by.pavka.march.map.HexGraph;

public class Courier {
    public static final double MAX_SPEED = 30;
    public static PlayScreen playScreen;
    public static HexGraph hexGraph;

    public static double courierDelay(Hex start, Hex finish) {
//        return playScreen.getHexGraph().findTimeToGo(start, finish, MAX_SPEED);
        return hexGraph.findTimeToGo(start, finish, MAX_SPEED);
    }
}
