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

public class Hex extends Group {

    public final static int SIZE = 6;
    public int row;
    public int col;
    public int index;
    public TiledMapTileLayer.Cell cell;
    public PlayScreen playScreen;
    private TiledMap tiledMap;
    private TiledMapTileLayer tiledLayer;

    public Hex(TiledMap tiledMap, TiledMapTileLayer tiledLayer, TiledMapTileLayer.Cell cell, int row, int col, PlayScreen screen) {
        this.tiledMap = tiledMap;
        this.tiledLayer = tiledLayer;
        addListener(new HexListener());
        this.row = row;
        this.col = col;
        this.cell = tiledLayer.getCell(col, row);
        playScreen = screen;
//        setDebug(true);
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

    class HexListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            System.out.println("CLICKED " + Hex.this);

            TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("TileLayer2");
            if (!playScreen.longPressed && playScreen.selectedHex != null) {
                int c = playScreen.selectedHex.col;
                int r = playScreen.selectedHex.row;
                layer.setCell(c, r, null);
                playScreen.selectedHex = null;
                if (playScreen.detailedUi) {
                    selectHex(layer);
                }
                playScreen.selectedPaths = null;
            } else if (playScreen.selectedHex == null){
                playScreen.setDetailedUi();
                selectHex(layer);
                playScreen.selectedPaths = null;
            } else {
                playScreen.longPressed = false;
                System.out.println("RIGHT BUTTON! Hex " + Hex.this.index);
                GraphPath<Hex> hexPath = playScreen.getHexGraph().findPath(playScreen.selectedHex, Hex.this);
                System.out.println(hexPath.getCount());
                Array<Path> paths = new Array<>();
                Hex start = null;
                Hex end;
                for (Hex h : hexPath) {
                    System.out.println("Hex col = " + h.col + " row = " + h.row);
                    end = h;
                    if (start != null) {
                        paths.add(playScreen.getHexGraph().getPath(start, end));
                    }
                    start = h;
                }
                playScreen.selectedPaths = paths;
            }


        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (button == Input.Buttons.RIGHT && playScreen.selectedHex != null) {
                System.out.println("RIGHT BUTTON! Hex " + Hex.this.index);
                GraphPath<Hex> hexPath = playScreen.getHexGraph().findPath(playScreen.selectedHex, Hex.this);
                System.out.println(hexPath.getCount());
                Array<Path> paths = new Array<>();
                Hex start = null;
                Hex end;
                for (Hex h : hexPath) {
                    System.out.println("Hex col = " + h.col + " row = " + h.row);
                    end = h;
                    if (start != null) {
                        paths.add(playScreen.getHexGraph().getPath(start, end));
                    }
                    start = h;
                }
                playScreen.selectedPaths = paths;
            }
            return super.touchDown(event, x, y, pointer, button);
        }

        private void selectHex(TiledMapTileLayer layer) {
            TiledMapTileLayer.Cell upCell = new TiledMapTileLayer.Cell();
            StaticTiledMapTile tile = (StaticTiledMapTile) tiledMap.getTileSets().getTile(20);
            upCell.setTile(tile);
            layer.setCell(col, row, upCell);
            playScreen.selectedHex = Hex.this;
        }
    }

    @Override
    public String toString() {
        return "HEX: row = " + row + " col = " + col + " cell: " + cell;
    }
}

