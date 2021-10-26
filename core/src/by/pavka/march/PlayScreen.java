package by.pavka.march;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PlayScreen extends Stage implements Screen {
    public static final String MAP = "map/map4.tmx";

    private Game game;
    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    private TiledMap map;
    private HexagonalTiledMapRenderer hexagonalTiledMapRenderer;
    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;

    private Table group;

    public PlayScreen(Game game) {
        super(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        this.game = game;
        map = new TmxMapLoader().load(MAP);
        atlas = new TextureAtlas("skin/golden-ui-skin.atlas");
        skin = new Skin(Gdx.files.internal("skin/golden-ui-skin.json"), atlas);
        hexagonalTiledMapRenderer = new BattlefieldRenderer(map, 1);
        stage = new PlayStage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), map);
        camera = (OrthographicCamera) stage.getCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        addListener(new PlayDragListener());
        uiCamera = (OrthographicCamera) getCamera();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        group = new Table();
        addActor(group);
        TextButton back = new TextButton("Back", skin);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        group.add(back);
        TextButton zoom = new TextButton("Zoom", skin);
        zoom.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (camera.zoom == 1) {
                    camera.zoom = 2.65f;
                } else {
                    camera.zoom = 1;
                }
                camera.update();
                hexagonalTiledMapRenderer.setView(camera);
            }
        });
        group.add(zoom);
        group.setDebug(true, true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 1, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        hexagonalTiledMapRenderer.render();
        camera.update();
        uiCamera.update();
        stage.draw();
        draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
        hexagonalTiledMapRenderer.setView(camera);
        getViewport().update(width, height, true);
        group.setBounds(0, getHeight() * 0.9f, getWidth(), getHeight() * .1f);
        group.left();

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        atlas.dispose();
        map.dispose();
        hexagonalTiledMapRenderer.dispose();
    }

    @Override
    public boolean keyDown(int keyCode) {
        return stage.keyDown(keyCode);
    }

    private class PlayStage extends Stage {
        private TiledMap map;

        public PlayStage(Viewport viewport, TiledMap map) {
            super(viewport);
            this.map = map;
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
        public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.LEFT) {
                camera.translate(-32 * camera.zoom, 0);
            }
            if (keycode == Input.Keys.RIGHT) {
                camera.translate(32 * camera.zoom, 0);
            }
            if (keycode == Input.Keys.UP)
                camera.translate(0, 32 * camera.zoom, 0);
            if (keycode == Input.Keys.DOWN) {
                camera.translate(0, -32 * camera.zoom, 0);
            }
            camera.update();
            hexagonalTiledMapRenderer.setView(camera);
            return true;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            System.out.println("Touch: " + screenX + " : " + screenY);
            return super.touchDown(screenX, screenY, pointer, button);
        }
    }

    private class PlayDragListener extends DragListener {
        float x;
        float y;

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            this.x = x;
            this.y = y;
            return true;
        }

        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
            camera.translate(camera.zoom * (-Gdx.input.getDeltaX()), camera.zoom * Gdx.input.getDeltaY());
            camera.update();
            hexagonalTiledMapRenderer.setView(camera);
            camera.update();
            this.x = x;
            this.y = y;
        }
    }

    private class BattlefieldRenderer extends HexagonalTiledMapRenderer {
        public BattlefieldRenderer(TiledMap map, float scale) {
            super(map, scale);
        }
    }
}
