package by.pavka.march.map;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.PlayScreen;
import by.pavka.march.military.Force;

public class Hex extends Group {

    public final static int SIZE = 6;
    public int row;
    public int col;
    public int index;
    public TiledMapTileLayer.Cell cell;
    public PlayScreen playScreen;
    private TiledMap tiledMap;
    private TiledMapTileLayer tiledLayer;
    private TiledMapTileLayer markLayer;
    StaticTiledMapTile tile;

    public Hex(TiledMap tiledMap, TiledMapTileLayer tiledLayer, TiledMapTileLayer.Cell cell, int row, int col, PlayScreen screen) {
        this.tiledMap = tiledMap;
        this.tiledLayer = tiledLayer;
        markLayer = (TiledMapTileLayer) tiledMap.getLayers().get("TileLayer2");
        tile = (StaticTiledMapTile) tiledMap.getTileSets().getTile(20);
        addListener(new HexListener());
        this.row = row;
        this.col = col;
        this.cell = tiledLayer.getCell(col, row);
        playScreen = screen;
        setDebug(true);
    }

    public float getRelX() {
        return (col + 0.2f) * tiledLayer.getTileWidth() * 0.75f + tiledLayer.getTileWidth() * 0.375f;
    }

    public float getRelY() {
        if (col % 2 == 0) {
            return tiledLayer.getTileHeight() * (row + 1f);
        } else {
            return tiledLayer.getTileHeight() * (row + 0.5f);
        }
    }

    public void mark() {
        TiledMapTileLayer.Cell upCell = new TiledMapTileLayer.Cell();
        upCell.setTile(tile);
        markLayer.setCell(col, row, upCell);
    }

    public void unmark() {
        markLayer.setCell(col, row, null);
    }

    class HexListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (!playScreen.longPressed) {
                playScreen.setDetailedUi(Hex.this);
            } else if (playScreen.selectedHex != null || playScreen.selectedForce != null || playScreen.selectedPaths != null) {
                navigate();
            }
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (button == Input.Buttons.RIGHT && (playScreen.selectedHex != null || playScreen.selectedForce != null
            || playScreen.selectedPaths != null)) {
                navigate();
            }
            return super.touchDown(event, x, y, pointer, button);
        }

        private void navigate() {
            playScreen.destinations.add(Hex.this);
            GraphPath<Hex> hexPath;
            Array<Path> paths = new Array<>();
            if (playScreen.selectedPaths == null && playScreen.selectedHex != null) {
                hexPath = playScreen.getHexGraph().findPath(playScreen.selectedHex, Hex.this);
            } else if (playScreen.selectedPaths == null && playScreen.selectedForce != null) {
                hexPath = playScreen.getHexGraph().findPath(playScreen.selectedForce.visualHex, Hex.this);
            } else {
                Hex begin = playScreen.selectedPaths.peek().toHex;
                hexPath = playScreen.getHexGraph().findPath(begin, Hex.this);
                paths = new Array<>(playScreen.selectedPaths);
            }
            Hex start = null;
            Hex end;
            for (Hex h : hexPath) {
                end = h;
                if (start != null) {
                    paths.add(playScreen.getHexGraph().getPath(start, end));
                }
                start = h;
                playScreen.selectedPaths = paths;
            }
            if (playScreen.selectedForce != null) {
                Force.sendMoveOrder(playScreen.selectedForce, playScreen.destinations);
            }
            System.out.println("Full length = " + playScreen.selectedPaths.size);
            for (Path p : playScreen.selectedPaths) {
                System.out.println(p.fromHex + "   " + p.toHex);
            }
        }
    }
}

