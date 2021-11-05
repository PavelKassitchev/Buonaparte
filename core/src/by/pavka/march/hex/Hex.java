package by.pavka.march.hex;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

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
            StaticTiledMapTile tile = (StaticTiledMapTile) tiledMap.getTileSets().getTile(2);
//            StaticTiledMapTile tile = (StaticTiledMapTile) tiledMap.getTileSets().getTile(19);
            System.out.println("REDHEX = " + tile);
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

