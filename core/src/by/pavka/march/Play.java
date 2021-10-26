package by.pavka.march;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;

public class Play extends Stage implements Screen {
    public static final float NORMAL_SPEED = 1.0f;
    public static final float LOW_SPEED = NORMAL_SPEED / 2;
    public static final float HIGH_SPEED = NORMAL_SPEED * 2;
    public static final float LOWEST_SPEED = NORMAL_SPEED / 4;
    public static final float HIGHEST_SPEED = NORMAL_SPEED * 4;
    public static final String MAP = "map/map4.tmx";
    public TiledMap map = new TmxMapLoader().load(MAP);
    public MapLayer objectLayer = map.getLayers().get("ObjectLayer");

    Game game;
    private TextureAtlas atlas;
    protected Skin skin;

    ShapeRenderer shapeRenderer;
    private HexagonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    //Viewport viewport;

    Vector3 position;

    Image image;
    TextureAtlas textureAtlas;
    SpriteBatch batch;
    Sprite sprite;
    TextureMapObject obj;

    float speed;
    Timer timer;
    Timer.Task task;

    public Play(Game game) {
        this.game = game;
        atlas = new TextureAtlas("uiskin.atlas");
        skin = new Skin(Gdx.files.internal("uiskin.json"), atlas);
        renderer = new MyInnerRenderer(map);
        for (MapLayer layer : map.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tiledLayer = (TiledMapTileLayer)layer;
                createActorsForLayer(tiledLayer);
            }
        }
    }

    private void createActorsForLayer(TiledMapTileLayer tiledLayer) {
        System.out.println(tiledLayer.getWidth() + " " + tiledLayer.getTileWidth() + " " + tiledLayer.getTileHeight());
        for (int x = 0; x < tiledLayer.getWidth(); x++) {
            for (int y = 0; y < tiledLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
                Hex actor = new Hex(map, tiledLayer, cell, y, x);
                float x0;
                float y0;
                if (x % 2 == 0) {
                    y0 = tiledLayer.getTileHeight() * (y + 0.5f);
                } else {
                    y0 = tiledLayer.getTileHeight() * y;
                }
                x0 = (x + 0.2f) * tiledLayer.getTileWidth() * 0.75f;
                actor.setBounds(x0, y0, tiledLayer.getTileWidth() * 0.75f, tiledLayer.getTileHeight());
                addActor(actor);
            }
        }
    }



    @Override
    public void show() {
        setDebugAll(true);
        Table table = new Table();
        table.setFillParent(true);
        TextButton textButton = new TextButton("Save", skin);
        table.center().top();
        table.add(textButton).prefHeight(64).prefWidth(82);
        addActor(table);
        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (camera.zoom == 1) {
                    camera.zoom = 2.65f;
                } else {
                    camera.zoom = 1;
                }
                camera.update();
                //camera.unproject(position);
                renderer.setView(camera);
            }
        });
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        shapeRenderer = new ShapeRenderer();
        //renderer = new MyInnerRenderer(map);
        camera = (OrthographicCamera) getCamera();
        //viewport = new FitViewport(w, h, camera);
        //viewport = new ExtendViewport(w, h, camera);
        camera.setToOrtho(false, w, h);
        //camera.position.set(0, 0,0);
        camera.update();
        Gdx.input.setInputProcessor(this);
        batch = new SpriteBatch();
        textureAtlas = new TextureAtlas("unit/friend.txt");
        TextureAtlas.AtlasRegion unit = textureAtlas.findRegion("hostile");
        TextureAtlas.AtlasRegion unit1 = textureAtlas.findRegion("fr_art");
        sprite = new Sprite(unit);
        obj = new TextureMapObject(unit1);
//        task = new ActionTask ();
//        timer = new Timer();
        speed = NORMAL_SPEED;
        position = new Vector3(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        //startGame(speed);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 1, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        obj.setX(138);
        obj.setY(208);
        camera.update();
        objectLayer.getObjects().add(obj);
        renderer.setView(camera);
        renderer.render();
        draw();
//        shapeRenderer.setProjectionMatrix(camera.combined);
//        camera.update();
        sprite.setPosition(320, 240);
        sprite.setSize(32, 32);
        batch.begin();
        sprite.draw(batch);
        batch.end();
    }


    @Override
    public void resize(int width, int height) {

        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.position.set(width / 2, height / 2, 0);
        camera.position.set(position);
        camera.update();

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
        Gdx.input.setInputProcessor(null);
        map.dispose();
        renderer.dispose();
        shapeRenderer.dispose();
        timer.clear();
    }

    @Override
    public boolean keyDown(int keyCode) {
        switch (keyCode) {
            case Input.Keys.NUM_1:
                speed = LOWEST_SPEED;
                break;
            case Input.Keys.NUM_2:
                speed = LOW_SPEED;
                break;
            default:
                speed = NORMAL_SPEED;
                break;
        }
        //startGame(speed);
        if (keyCode == Input.Keys.LEFT) {
            camera.translate(-32 * camera.zoom, 0);
        }
        if (keyCode == Input.Keys.RIGHT) {
            camera.translate(32 * camera.zoom, 0);
        }
        if (keyCode == Input.Keys.UP)
            camera.translate(0, 32 * camera.zoom, 0);
        if (keyCode == Input.Keys.DOWN) {
            camera.translate(0, -32 * camera.zoom, 0);
        }
        //position = camera.position;
        renderer.setView(camera);
        return true;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        System.out.println("Touch: " + screenX + " : " + screenY);
        return super.touchDown(screenX, screenY, pointer, button);
    }


    @Override
    public void act() {
        super.act();
        System.out.println("Running");
    }
}

//    private void startGame(float speed) {
//        timer.clear();
//        timer.scheduleTask(task, 1, 3 / speed);
//        timer.start();
//    }

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
        float width = 32;
        float height = 32;
        if (object instanceof TextureMapObject) {
            TextureMapObject textureObj = (TextureMapObject) object;

            this.getBatch().draw(textureObj.getTextureRegion(),
                    textureObj.getX() - width / 2, textureObj.getY() - height / 2,
                    width, height);
        }
    }

}

//    private class ActionTask extends Timer.Task {
//        @Override
//        public void run() {
//            Play.this.act();
//        }
//    }

