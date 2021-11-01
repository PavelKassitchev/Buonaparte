package by.pavka.march;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Hex extends Actor {

    public int row;
    public int col;
    public PlayScreen playScreen;
    private TiledMap tiledMap;
    private TiledMapTileLayer tiledLayer;
    private TiledMapTileLayer.Cell cell;

    public Hex(TiledMap tiledMap, TiledMapTileLayer tiledLayer, TiledMapTileLayer.Cell cell, int row, int col, PlayScreen screen) {
        this.tiledMap = tiledMap;
        this.tiledLayer = tiledLayer;
        //this.cell = cell;
        addListener(new HexListener());
        this.row = row;
        this.col = col;
        this.cell = tiledLayer.getCell(col, row);
        playScreen = screen;
        setDebug(true);
    }

    public void unselect() {
        int c = playScreen.selectedHex.col;
        int r = playScreen.selectedHex.row;
        ((TiledMapTileLayer) tiledMap.getLayers().get("TileLayer2")).setCell(c, r, null);
    }

    class HexListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            System.out.println("CLICKED " + Hex.this);
            TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("TileLayer2");
            if (playScreen.selectedHex != null) {
                int c = playScreen.selectedHex.col;
                int r = playScreen.selectedHex.row;
                layer.setCell(c, r, null);
                playScreen.selectedHex = null;
                if (playScreen.detailedUi) {
                    selectHex(layer);
                }
            } else {
                playScreen.setDetailedUi();
                selectHex(layer);
            }
        }

        private void selectHex(TiledMapTileLayer layer) {
            TiledMapTileLayer.Cell upCell = new TiledMapTileLayer.Cell();
            StaticTiledMapTile tile = (StaticTiledMapTile) tiledMap.getTileSets().getTile(5);
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

