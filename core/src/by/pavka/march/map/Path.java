package by.pavka.march.map;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.Objects;

public class Path extends Image implements Connection<Hex> {

    public Hex fromHex;
    public Hex toHex;
    float cost;
    Direction direction;

    public Path(Hex fromHex, Hex toHex){
        this.fromHex = fromHex;
        this.toHex = toHex;
        cost = (Float.parseFloat(fromHex.cell.getTile().getProperties().get("cost").toString())
                + Float.parseFloat(toHex.cell.getTile().getProperties().get("cost").toString())) / 2;
    }


//    public static int getDaysToGo(Array<Path> paths, double speed) {
//        double distance = 0;
//        for (Path path: paths) {
//            distance += Hex.SIZE * (Float)path.getFromNode().cell.getTile().getProperties().get("cost");
//        }
//        if (distance == 0) return 0;
//
//        return (int)Math.ceil(distance / (speed * 2));
//    }

//    public static boolean isHexInside(Array<Path> paths, Hex hex) {
//        if (paths == null || paths.size == 0) return false;
//        for (Path path: paths) {
//            if(path.fromHex == hex || path.toHex == hex) return true;
//        }
//        return false;
//    }
//    public double getDays(double speed) {
//        return Hex.SIZE * (Float)getFromNode().cell.getTile().getProperties().get("cost") / speed;
//    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public Hex getFromNode() {
        return fromHex;
    }

    @Override
    public Hex getToNode() {
        return toHex;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 1f, 0, 0.9f);
        shapeRenderer.rectLine(fromHex.getRelX(), fromHex.getRelY(), toHex.getRelX(), toHex.getRelY(), 5);
        shapeRenderer.circle(fromHex.getRelX(), fromHex.getRelY(), 6);
//        shapeRenderer.triangle(toHex.getRelX(), toHex.getRelY() + 6, toHex.getRelX(), toHex.getRelY() - 6, toHex.getRelX() + 6, toHex.getRelY());
        shapeRenderer.end();
    }

    public void render(ShapeRenderer shapeRenderer, float r, float g, float b) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(r, g, b, 0.9f);
        shapeRenderer.rectLine(fromHex.getRelX(), fromHex.getRelY(), toHex.getRelX(), toHex.getRelY(), 5);
        shapeRenderer.circle(fromHex.getRelX(), fromHex.getRelY(), 6);
//        shapeRenderer.triangle(toHex.getRelX(), toHex.getRelY() + 6, toHex.getRelX(), toHex.getRelY() - 6, toHex.getRelX() + 6, toHex.getRelY());
        shapeRenderer.end();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return fromHex.equals(path.fromHex) && toHex.equals(path.toHex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromHex, toHex);
    }

    public Path reverse() {
        return new Path(toHex, fromHex);
    }
}
