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

import by.pavka.march.PlayScreen;
import by.pavka.march.military.Force;
import by.pavka.march.order.JoinOrder;
import by.pavka.march.order.MoveOrder;
import by.pavka.march.order.Order;

public class Hex extends Group {

    public final static float SIZE = 3.5f;
    public int row;
    public int col;
    public int index;
    public TiledMapTileLayer.Cell cell;
    public PlayScreen playScreen;
    private TiledMap tiledMap;
    private TiledMapTileLayer tiledLayer;
    private TiledMapTileLayer markLayer;
    StaticTiledMapTile tile;
    private Array<Force> forces;

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
        forces = new Array<>();
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

    public Array<Force> getForces() {
        return forces;
    }


    public void addForce(Force force) {
        forces.add(force);
    }


    public void removeForce(Force force) {
        forces.removeValue(force, true);
    }

    public boolean hasForces() {
        for (Actor a : getChildren()) {
            if (a instanceof Force) {
                return true;
            }
        }
        return false;
    }

    public Array<Force> enemiesOf(Force force) {
        if (!forces.isEmpty()) {
            Array<Force> enemies = new Array<>();
            for (Force f : getForces()) {
                if (f.nation != force.nation) {
                    enemies.add(f);
                }
            }
            return enemies;
        }
        return null;
    }

    @Override
    public String toString() {
        return " col: " + col + " row: " + row;
    }


    class HexListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (!playScreen.longPressed) {
                playScreen.destroyForceWindow();
                playScreen.destroyTreeWindow();
                playScreen.destroySelectBox();
                playScreen.setDetailedUi(Hex.this);
                playScreen.additiveOrder = false;
//                playScreen.forceWindow.remove();
            } else if (playScreen.selectedHex != null || !playScreen.selectedForces.isEmpty() ||
                    playScreen.selectedPaths != null) {
                navigate();
            }
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (button == Input.Buttons.RIGHT && (playScreen.selectedHex != null
                    || playScreen.selectedPaths != null || !playScreen.selectedForces.isEmpty())) {
                navigate();
            }
            return super.touchDown(event, x, y, pointer, button);
        }

        private void navigate() {
            if (!playScreen.destinations.isEmpty()) {
                playScreen.additiveOrder = true;
            }
            playScreen.destinations.add(Hex.this);
            GraphPath<Hex> hexPath;
            Array<Path> paths = new Array<>();
            if (playScreen.selectedPaths == null && playScreen.selectedHex != null) {
                hexPath = playScreen.getHexGraph().findPath(playScreen.selectedHex, Hex.this);
            } else if (playScreen.selectedPaths == null && !playScreen.selectedForces.isEmpty()) {
                hexPath = playScreen.getHexGraph().findPath(playScreen.selectedForces.get(0).visualHex, Hex.this);
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


            Array<Force> forces = playScreen.selectedForces;

            if (playScreen.treeWindow == null) {
                for (Force f : forces) {
                    Force.sendOrder(f, new MoveOrder(playScreen.destinations, playScreen.additiveOrder));
                }
            } else {
                //TODO
                Array<Hex> dest = new Array<Hex>();
                dest.add(Hex.this);


                Force frc = playScreen.treeWindow.getActiveForce();
                Order order = playScreen.treeWindow.getForceTreeTab(frc).futureOrderTable.getLastOrder();
                if (order instanceof JoinOrder && !Hex.this.getForces().isEmpty()) {
                    JoinOrder jOrder = (JoinOrder) order;
//                    jOrder.setTargetForce((Formation)Hex.this.getForces().first());


//                    Array<Force> allies = frc.getAllies(Hex.this);
//                    SelectBox selectForce = new SelectBox<Force>(frc.playScreen.game.getSkin());
//                    selectForce.setItems(allies);
//                    playScreen.addActorToPlayStage(selectForce);
//                    selectForce.setBounds(Hex.this.getX(), Hex.this.getY(), 120, 20);
                    playScreen.activateSelectBox(frc, Hex.this, jOrder);

                } else {
                    ((MoveOrder)playScreen.treeWindow.getForceTreeTab(frc).futureOrderTable.getLastOrder())
                            .setDestinations(playScreen.destinations);
                    playScreen.treeWindow.setVisible(true);
                }
                playScreen.treeWindow.getForceTreeTab(frc).futureOrderTable.refreshLabels();


                /*

                for (Force f : forces) {
                    Order order = playScreen.treeWindow.getForceTreeTab(f).futureOrderTable.getLastOrder();
                    if (order instanceof JoinOrder && !Hex.this.getForces().isEmpty()) {
                        JoinOrder jOrder = (JoinOrder) order;
                        jOrder.setTargetForce((Formation)Hex.this.getForces().first());
                    } else {
                        ((MoveOrder)playScreen.treeWindow.getForceTreeTab(f).futureOrderTable.getLastOrder())
                                .setDestinations(playScreen.destinations);
                    }

//                    ((MoveOrder)playScreen.treeWindow.getForceTreeTab(f).futureOrderTable.getLastOrder())
//                            .setDestinations(playScreen.destinations);
                    playScreen.treeWindow.getForceTreeTab(f).futureOrderTable.refreshLabels();
                }

                */
//                playScreen.treeWindow.setVisible(true);
            }

        }

    }

}

