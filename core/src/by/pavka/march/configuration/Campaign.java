package by.pavka.march.configuration;

public enum Campaign {
    MARENGO("map/small.tmx", "TileLayer1");

    String mapName;
    String layerName;
    Campaign(String name, String layerName) {
        mapName = name;
        this.layerName = layerName;
    }

}
