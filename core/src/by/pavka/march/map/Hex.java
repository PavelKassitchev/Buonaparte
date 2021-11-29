package by.pavka.march.map;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;

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

    class HexListener extends ClickListener {
//        @Override
//        public void clicked(InputEvent event, float x, float y) {
//            if (!playScreen.longPressed) {
//                if (playScreen.selectedHex != null) {
//                    playScreen.unselectHex();
//                    playScreen.unselectForce();
//                } else {
//                    playScreen.setDetailedUi();
//                    playScreen.unselectForce();
//                }
//                selectHex(markLayer);
//                playScreen.selectedPaths = null;
//            } else if (playScreen.selectedHex != null || playScreen.selectedForce != null) {
//                playScreen.longPressed = false;
//                navigate();
//            }
//        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (!playScreen.longPressed) {
                playScreen.setDetailedUi();
                playScreen.selectedPaths = null;
                playScreen.uncheckHex();
                playScreen.uncheckForce();
                setHexInfo();
                setForceInfo();
                if (Hex.this.getChildren().size == 1) {
                    Force f = (Force) Hex.this.getChild(0);
                    checkForce(f);
                } else {
                    checkHex();
                }
            } else if (playScreen.selectedHex != null || playScreen.selectedForce != null || playScreen.selectedPaths != null) {
//                playScreen.longPressed = false;
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
                Force.sendMoveOrder(playScreen.selectedForce, Hex.this);
//                playScreen.unselectHex();
            }
            System.out.println("Full length = " + playScreen.selectedPaths.size);
            for (Path p : playScreen.selectedPaths) {
                System.out.println(p.fromHex + "   " + p.toHex);
            }
//            playScreen.uncheckHex();
//            playScreen.uncheckForce();
        }

        public void setHexInfo() {
            playScreen.hexButton.setText("Mov.cost = " + Hex.this.cell.getTile().getProperties().get("cost").toString());
        }

        public void checkHex() {
            Hex.this.mark();
            playScreen.selectedHex = Hex.this;
            playScreen.hexButton.setChecked(true);
            playScreen.hexButton.setDisabled(true);
        }

        public void setForceInfo() {
            if (Hex.this.hasChildren()) {
                SnapshotArray<Actor> forces = Hex.this.getChildren();
                int forceNumber = 0;
                int soldierNumber = 0;
                for (Actor a : forces) {
                    forceNumber++;
                    soldierNumber += ((Force) a).strength.soldiers();
                }
                String info = String.format("%d Forces\n%d Soldiers", forceNumber, soldierNumber);
                playScreen.forceButton.setText(info);
            }
        }

        public void checkForce(Force force) {
            playScreen.selectedForce = force;
            force.setAlpha(1);
            force.showPath = true;
            playScreen.forceButton.setChecked(true);
            playScreen.hexButton.setChecked(false);
        }

        private void selectHex(TiledMapTileLayer layer) {
            TiledMapTileLayer.Cell upCell = new TiledMapTileLayer.Cell();
//            StaticTiledMapTile tile = (StaticTiledMapTile) tiledMap.getTileSets().getTile(20);
            upCell.setTile(tile);
            layer.setCell(col, row, upCell);
            playScreen.selectedHex = Hex.this;
            playScreen.hexButton.setText("Mov.cost = " + Hex.this.cell.getTile().getProperties().get("cost").toString());
            playScreen.hexButton.setChecked(true);

            if (Hex.this.getChildren().size == 1) {
                Force f = (Force) Hex.this.getChild(0);
                selectForce(f);
                playScreen.forceButton.setText("Force " + f.strength.soldiers());
                playScreen.hexButton.setChecked(false);
                playScreen.forceButton.setChecked(true);
            } else {
                playScreen.forceButton.setChecked(false);
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

