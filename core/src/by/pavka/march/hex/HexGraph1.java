package by.pavka.march.hex;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import by.pavka.march.PlayScreen;

public class HexGraph1 implements IndexedGraph<Hex> {
    private TiledMap map;
    private PlayScreen screen;
    private HexHeuristic hexHeuristic;
    private Array<Hex> hexes;
    private Array<Path> paths;
    private ObjectMap<Hex, Array<Connection<Hex>>> pathsMap;
    private int lastNodeIndex;

    public HexGraph1(TiledMap map, PlayScreen screen) {
        this.map = map;
        this.screen = screen;
        hexHeuristic = new HexHeuristic();
        hexes = new Array<>();
        paths = new Array<>();
        pathsMap = new ObjectMap<>();
        init();
    }

    private void init() {
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get(0);
        int cols = tileLayer.getWidth();
        int rows = tileLayer.getHeight();
        Hex hex;
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                TiledMapTileLayer.Cell cell = tileLayer.getCell(i, j);
                hex = new Hex(map, tileLayer, cell, j, i, screen);
                addHex(hex);
                float x0;
                float y0;
                if (i % 2 == 0) {
                    y0 = tileLayer.getTileHeight() * (j + 0.5f);
                } else {
                    y0 = tileLayer.getTileHeight() * j;
                }
                x0 = (i + 0.2f) * tileLayer.getTileWidth() * 0.75f;
                hex.setBounds(x0, y0, tileLayer.getTileWidth() * 0.75f, tileLayer.getTileHeight());
                screen.addActorToPlayStage(hex);
            }

        }
    }

    public void addHex(Hex hex){
        hex.index = lastNodeIndex;
        lastNodeIndex++;

        hexes.add(hex);
    }

    public void connectHexes(Hex fromHex, Hex toHex){
        Path path = new Path(fromHex, toHex);
        //if (fromHex == null || toHex == null) return;
        if(!pathsMap.containsKey(fromHex)){
            pathsMap.put(fromHex, new Array<Connection<Hex>>());
        }
        pathsMap.get(fromHex).add(path);
        paths.add(path);
    }

    public GraphPath<Hex> findPath(Hex startHex, Hex goalHex){
        GraphPath<Hex> hexPath = new DefaultGraphPath<Hex>();

        new IndexedAStarPathFinder<Hex>(this).searchNodePath(startHex, goalHex, hexHeuristic, hexPath);

        return hexPath;
    }

    @Override
    public int getIndex(Hex node) {
        return node.index;
    }

    @Override
    public int getNodeCount() {
        return lastNodeIndex;
    }

    @Override
    public Array<Connection<Hex>> getConnections(Hex fromNode) {
        if(pathsMap.containsKey(fromNode)){
            return pathsMap.get(fromNode);
        }

        return new Array<Connection<Hex>>(0);
    }

    public Hex getHex(int x, int y) {
        for (Hex hex: hexes) {
            if (hex.col == x && hex.row == y) return hex;
        }
        return null;
    }

    public Path getPath(Hex start, Hex end) {
        Array<Connection<Hex>> pathsFrom = pathsMap.get(start);
        for (Connection path: pathsFrom) {
            if (path.getToNode() == end) {
                return (Path)path;
            }
        }
        return null;
    }
}
