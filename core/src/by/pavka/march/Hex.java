package by.pavka.march;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Hex extends Actor {

    private TiledMap tiledMap;
    private TiledMapTileLayer tiledLayer;
    private TiledMapTileLayer.Cell cell;
    public int row;
    public int col;

    public Hex(TiledMap tiledMap, TiledMapTileLayer tiledLayer, TiledMapTileLayer.Cell cell, int row, int col) {
        this.tiledMap = tiledMap;
        this.tiledLayer = tiledLayer;
        //this.cell = cell;
        addListener(new HexListener());
        this.row = row;
        this.col = col;
        this.cell = tiledLayer.getCell(col, row);
        setDebug(true);
    }

    class HexListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            System.out.println("CLICKED " + Hex.this);
        }
    }

    @Override
    public String toString() {
        return "HEX: row = " + row + " col = " + col;
    }


}
