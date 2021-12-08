package by.pavka.march.map;

import static by.pavka.march.map.Direction.NORTH;
import static by.pavka.march.map.Direction.NORTHEAST;
import static by.pavka.march.map.Direction.NORTHWEST;
import static by.pavka.march.map.Direction.SOUTH;
import static by.pavka.march.map.Direction.SOUTHEAST;
import static by.pavka.march.map.Direction.SOUTHWEST;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

import by.pavka.march.PlayScreen;

public class HexGraph implements IndexedGraph<Hex> {
    private TiledMap map;
    private PlayScreen screen;
    private HexHeuristic hexHeuristic;
    private Array<Hex> hexes;
    private Array<Path> paths;
    private ObjectMap<Hex, Array<Connection<Hex>>> pathsMap;
    private int lastNodeIndex;
    private int cols;
    private int rows;

    public HexGraph(TiledMap map, PlayScreen screen) {
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
        cols = tileLayer.getWidth();
        rows = tileLayer.getHeight();
        Hex hex;
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                TiledMapTileLayer.Cell cell = tileLayer.getCell(i, j);
                hex = new Hex(map, tileLayer, cell, j, i, screen);
                addHex(hex);
                float x0 = hex.getRelX() - tileLayer.getTileWidth() * 0.375f;
                float y0 = hex.getRelY() - tileLayer.getTileHeight() * 0.5f;
                hex.setBounds(x0, y0, tileLayer.getTileWidth() * 0.75f, tileLayer.getTileHeight());
                screen.addActorToPlayStage(hex);
            }

        }
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                hex = getHex(i, j);
                Array<Hex> hexes = getNeighbours(hex);
                for (Hex h : hexes) {
                    connectHexes(hex, getHex(h.col, h.row));
                }
            }
        }
        for (Path p : paths) {
            screen.addActorToPlayStage(p);
        }
    }

    public Array<Hex> getNeighbours(Hex hex) {
        Array<Hex> neighbours = new Array<Hex>();
        if (hex.col > 0) {
            neighbours.add(getHex(hex.col - 1, hex.row));
        }
        if (hex.col < cols - 1) {
            neighbours.add(getHex(hex.col + 1, hex.row));
        }

        if (hex.row > 0) {
            neighbours.add(getHex(hex.col, hex.row - 1));
        }
        if (hex.row < rows - 1) {
            neighbours.add(getHex(hex.col, hex.row + 1));
        }

        if (hex.row < rows - 1 && hex.col % 2 == 0) {
            if (hex.col > 0) {
                neighbours.add(getHex(hex.col - 1, hex.row + 1));
            }
            if (hex.col < cols - 1) {
                neighbours.add(getHex(hex.col + 1, hex.row + 1));
            }
        }

        if (hex.row > 0 && hex.col % 2 == 1) {
            if (hex.col > 0) {
                neighbours.add(getHex(hex.col - 1, hex.row - 1));
            }
            if (hex.col < cols - 1) {
                neighbours.add(getHex(hex.col + 1, hex.row - 1));
            }
        }
        return neighbours;
    }

    public ObjectSet<Hex> getArea(Hex hex, int radius, ObjectSet<Hex> area) {
        area.add(hex);
        while (radius > 0) {
            radius--;
            Array<Hex> neighbours = getNeighbours(hex);
            area.addAll(neighbours);
            for (Hex h : neighbours) {
                getArea(h, radius, area);
            }
        }
        return area;
    }

    public void addHex(Hex hex) {
        hex.index = lastNodeIndex;
        lastNodeIndex++;
        hexes.add(hex);
    }

    public void connectHexes(Hex fromHex, Hex toHex) {
        Path path = new Path(fromHex, toHex);
        path.direction = findDirection(fromHex, toHex);
        if (!pathsMap.containsKey(fromHex)) {
            pathsMap.put(fromHex, new Array<Connection<Hex>>());
        }
        pathsMap.get(fromHex).add(path);
        paths.add(path);
    }

    private Direction findDirection(Hex from, Hex to) {
        if (from.col == to.col) {
            if (from.row < to.row) {
                return NORTH;
            }
            return SOUTH;
        }
        if (from.col < to.col) {
            if (from.col % 2 == 0) {
                if (from.row == to.row) {
                    return SOUTHEAST;
                }
                return NORTHEAST;
            } else {
                if (from.row == to.row) {
                    return NORTHEAST;
                }
                return SOUTHEAST;
            }
        } else {
            if (from.col % 2 == 0) {
                if (from.row == to.row) {
                    return SOUTHWEST;
                }
                return NORTHWEST;
            } else {
                if (from.row == to.row) {
                    return NORTHWEST;
                }
                return SOUTHWEST;
            }
        }
    }

    public GraphPath<Hex> findPath(Hex startHex, Hex goalHex) {
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
        if (pathsMap.containsKey(fromNode)) {
            return pathsMap.get(fromNode);
        }
        return new Array<>(0);
    }

    public Hex getHex(int x, int y) {
        for (Hex hex : hexes) {
            if (hex.col == x && hex.row == y) return hex;
        }
        return null;
    }

    public Path getPath(Hex start, Hex end) {
        Array<Connection<Hex>> pathsFrom = pathsMap.get(start);
        for (Connection path : pathsFrom) {
            if (path.getToNode() == end) {
                return (Path) path;
            }
        }
        return null;
    }

    public double findTimeToGo(Hex start, Hex finish, double speed) {
        GraphPath<Hex> path = findPath(start, finish);
        double timeToGo = 0;
        for (Hex h : path) {
            if (h == path.get(0) || h == path.get(path.getCount() - 1)) {
                timeToGo += Hex.SIZE / (speed * 2);
            } else {
                timeToGo += Hex.SIZE / speed;
            }
        }
        System.out.println("TIME TO GO: " + timeToGo);
        return timeToGo;
    }
}
