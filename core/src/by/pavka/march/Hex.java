package by.pavka.march;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Hex extends Actor {
//    public static final int HEX_WIDTH = 72;
//    public static final int HEX_HEIGHT = 72;

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
        //setBounds(getRelX(), getRelY(), HEX_WIDTH, HEX_HEIGHT);
        //setBounds(getRelX() - 8, getRelY() - 8, 16, 16);
        setDebug(true);
    }

//    @Override
//    public Actor hit(float x, float y, boolean touchable) {
//        return x * x + y * y < HEX_HEIGHT * HEX_WIDTH ? this : null;
//    }

    private class HexListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            System.out.println("CLICKED " + Hex.this);
        }
    }

    public float getRelX() {
        if (row % 2 == 0) return (16 + col * 16);
        return (8 + col * 16);
    }

    public float getRelY() {
        return (8 + row * 12);
    }

    @Override
    public String toString() {
        return "HEX: row = " + row + " col = " + col + " RelX = " + getRelX() + " RelY = " + getRelY();
    }


}
