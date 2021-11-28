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

    public Hex(TiledMap tiledMap, TiledMapTileLayer tiledLayer, TiledMapTileLayer.Cell cell, int row, int col, PlayScreen screen) {
        this.tiledMap = tiledMap;
        this.tiledLayer = tiledLayer;
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

    class HexListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("TileLayer2");
            if (!playScreen.longPressed) {
                if (playScreen.selectedHex != null) {
                    playScreen.unselectHex();
                    playScreen.unselectForce();
                } else {
                    playScreen.setDetailedUi();
                    playScreen.unselectForce();
                }
                selectHex(layer);
                playScreen.selectedPaths = null;
                playScreen.setHexInfo();
            } else if (playScreen.selectedHex != null) {
                playScreen.longPressed = false;
                navigate();
            }
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (button == Input.Buttons.RIGHT && playScreen.selectedHex != null) {
                navigate();
            }
            return super.touchDown(event, x, y, pointer, button);
        }

        private void navigate() {
            GraphPath<Hex> hexPath = playScreen.getHexGraph().findPath(playScreen.selectedHex, Hex.this);
            Array<Path> paths = new Array<>();
            Hex start = null;
            Hex end;
            for (Hex h : hexPath) {
                end = h;
                if (start != null) {
                    paths.add(playScreen.getHexGraph().getPath(start, end));
                }
                start = h;
            }
            playScreen.selectedPaths = paths;

//            if (playScreen.selectedForce != null) {
//                Array<Path> forcePath = new Array(paths);
//                playScreen.selectedForce.forcePath = forcePath;
//                playScreen.unselectHex();
//            }

            if (playScreen.selectedForce != null) {
                Force.sendMoveOrder(playScreen.selectedForce, Hex.this);
//                GraphPath<Hex> fPath = playScreen.getHexGraph().findPath(playScreen.selectedForce.hex, Hex.this);
//                Array<Path> fPaths = new Array<>();
//                Hex st = null;
//                Hex en;
//                for (Hex h : fPath) {
//                    en = h;
//                    if (st != null) {
//                        fPaths.add(playScreen.getHexGraph().getPath(st, en));
//                    }
//                    st = h;
//                }
//                playScreen.selectedForce.forcePath = fPaths;
                playScreen.unselectHex();
            }
        }

        private void selectHex(TiledMapTileLayer layer) {
            TiledMapTileLayer.Cell upCell = new TiledMapTileLayer.Cell();
            StaticTiledMapTile tile = (StaticTiledMapTile) tiledMap.getTileSets().getTile(20);
            upCell.setTile(tile);
            layer.setCell(col, row, upCell);
            playScreen.selectedHex = Hex.this;
            System.out.println(Hex.this.getChildren().size);
            if (Hex.this.getChildren().size == 1) {
                Force f = (Force) Hex.this.getChild(0);
                selectForce(f);
            }
            System.out.println("SELECTED FORCE = " + playScreen.selectedForce);
        }

        private void selectForce(Force force) {
            playScreen.selectedForce = force;
            force.setAlpha(1);
        }
    }

    @Override
    public String toString() {
        return "HEX: row = " + row + " col = " + col + " cell: " + cell;
    }

}

