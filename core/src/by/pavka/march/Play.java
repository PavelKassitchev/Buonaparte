package by.pavka.march;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Play extends Stage implements Screen {
    public static final String MAP = "map/map4.tmx";
    public static TiledMap map = new TmxMapLoader().load(MAP);

    ShapeRenderer shapeRenderer;
    private HexagonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    Image image;
    TextureAtlas textureAtlas;
    SpriteBatch batch;
    Sprite sprite;

    @Override
    public void show() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        shapeRenderer = new ShapeRenderer();
        renderer = new MyInnerRenderer(map);
        camera = (OrthographicCamera) getCamera();
        camera.setToOrtho(false, w, h);
        Gdx.input.setInputProcessor(this);
        batch = new SpriteBatch();
        textureAtlas = new TextureAtlas("unit/friend.txt");
        TextureAtlas.AtlasRegion unit = textureAtlas.findRegion("hostile");
        sprite = new Sprite(unit);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);
        renderer.render();
        draw();
        shapeRenderer.setProjectionMatrix(camera.combined);
        camera.update();
        sprite.setPosition(130, 130);
        sprite.setSize(32, 32);
        batch.begin();
        sprite.draw(batch);
        //batch.setColor(1, 0, 1, 1);
        //batch.draw(textureFrance, 200, 200, 32, 32);
        batch.end();
    }



    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        shapeRenderer.dispose();
    }

    class MyInnerRenderer extends HexagonalTiledMapRenderer {
        public MyInnerRenderer(TiledMap map) {
            super(map);
        }

        @Override
        public void render() {
            super.render();

        }

        @Override
        public void renderObject(MapObject object) {
            float width = 14;
            float height = 14;
            if (object instanceof TextureMapObject) {
                TextureMapObject textureObj = (TextureMapObject) object;

                this.getBatch().draw(textureObj.getTextureRegion(), textureObj.getX(), textureObj.getY(),
                        width, height);
            }
        }

    }
}
