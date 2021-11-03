package by.pavka.march;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import by.pavka.march.hex.Hex;
import by.pavka.march.hex.HexGraph1;

public class PlayScreen implements Screen {
    public static final String MAP = "map/twolayers3.tmx";

    Game game;
    PlayStage playStage;
    Skin skin;
    OrthographicCamera camera;
    HexagonalTiledMapRenderer hexagonalTiledMapRenderer;
    Stage uiStage;
    public Hex selectedHex;
    public boolean detailedUi;

    private TextureAtlas atlas;
    private TiledMap map;
    private OrthographicCamera uiCamera;
    private InputMultiplexer inputMultiplexer;

    private Table group;
    private boolean dragged;

    private Texture texture = new Texture("unit/friend.png");
    private Sprite sprite;
    private TextureRegion tRegion;
    Batch batch;

    public PlayScreen(Game game) {
        this.game = game;
        map = new TmxMapLoader().load(MAP);
        atlas = new TextureAtlas("skin/golden-ui-skin.atlas");
        skin = new Skin(Gdx.files.internal("skin/golden-ui-skin.json"), atlas);
        hexagonalTiledMapRenderer = new BattlefieldRenderer(map, 1);
        playStage = new PlayStage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), map);
        setGraphToPlayStage();
        uiStage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())) {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return !super.touchDown(screenX, screenY, pointer, button);
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                super.touchUp(screenX, screenY, pointer, button);
                if (dragged) {
                    dragged = false;
                    return true;
                }
                return false;
            }
        };


        camera = (OrthographicCamera) playStage.getCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiStage.addListener(new PlayDragListener());
        uiCamera = (OrthographicCamera) uiStage.getCamera();
        inputMultiplexer = new InputMultiplexer(uiStage, playStage);
    }

    public void setDetailedUi() {
        if (group != null) {
            group.remove();
        }
        group = new Table();
        uiStage.addActor(group);
        TextButton back = new TextButton("Terrain", skin);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        group.add(back);
        TextButton zoom = new TextButton("Units", skin);
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
        TextButton cancel = new TextButton("Cancel", skin);
        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                detailedUi = false;
                group.remove();
                show();
            }
        });
        group.add(cancel);
        group.setBounds(0, uiStage.getHeight() * 0.9f, uiStage.getWidth(), uiStage.getHeight() * .1f);
        group.left();
        detailedUi = true;
    }

    public void setGeneralUi() {
        if (group != null) {
            group.remove();
        }
        group = new Table();
        uiStage.addActor(group);
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
        group.setBounds(0, uiStage.getHeight() * 0.9f, uiStage.getWidth(), uiStage.getHeight() * .1f);
        group.left();
        detailedUi = false;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        if (!detailedUi) {
            setGeneralUi();
        } else {
            setDetailedUi();
        }
        group.setDebug(true, true);

///////
        TextureAtlas textureAtlas = new TextureAtlas("unit/friend.txt");
        TextureAtlas.AtlasRegion region = textureAtlas.findRegion("fr_art");
        tRegion = (TextureRegion) region;
        sprite = atlas.createSprite("fr_art");
        batch = playStage.getBatch();
        Image image = new Image(tRegion);
        //image.setPosition(72, 62);
        image.setSize(44, 44);
        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("Image clicked");
            }
        });
        //stage.addActor(image);
//        playStage.imageHex.addActor(image);
//        image.setPosition(5, 14);
/////
    }

    @Override
    public void resize(int width, int height) {
        playStage.getViewport().update(width, height, false);
        hexagonalTiledMapRenderer.setView(camera);
        uiStage.getViewport().update(width, height, true);
        group.setBounds(0, uiStage.getHeight() * 0.9f, uiStage.getWidth(), uiStage.getHeight() * .1f);
        group.left();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 1, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        hexagonalTiledMapRenderer.render();
        camera.update();
        uiCamera.update();
        playStage.draw();
        uiStage.draw();

//        batch.begin();
//        batch.draw(tRegion, 10, 10, 10, 10);
//        batch.end();
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
        playStage.dispose();
        skin.dispose();
        atlas.dispose();
        map.dispose();
        hexagonalTiledMapRenderer.dispose();
    }

    public void setGraphToPlayStage() {
        playStage.setGraph();
    }

    public void addActorToPlayStage(Actor actor) {
        playStage.addActor(actor);
    }

    private class PlayStage extends Stage {
        private TiledMap map;
        private HexGraph1 hexGraph;
        Hex imageHex;

        public PlayStage(Viewport viewport, TiledMap map) {
            super(viewport);
            this.map = map;
            TiledMapTileLayer tiledLayer = (TiledMapTileLayer) map.getLayers().get("TileLayer1");
//            createActorsForLayer(tiledLayer);
//            MapLayer layer = map.getLayers().get("ObjectLayer");

//            hexGraph = new HexGraph1(map, PlayScreen.this);
        }

        private void setGraph() {
            hexGraph = new HexGraph1(map, PlayScreen.this);
        }

        private void createActorsForLayer(TiledMapTileLayer tiledLayer) {
            System.out.println("Method createActors");
            for (int x = 0; x < tiledLayer.getWidth(); x++) {
                for (int y = 0; y < tiledLayer.getHeight(); y++) {
                    TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
                    System.out.println("CELL = " + cell);
                    Hex actor = new Hex(map, tiledLayer, cell, y, x, PlayScreen.this);

                    if (x ==1 && y == 1) {
                        imageHex = actor;
                    }

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
            dragged = true;
            //uiStage.dragged = true;
        }
    }

    private class BattlefieldRenderer extends HexagonalTiledMapRenderer {
        public BattlefieldRenderer(TiledMap map, float scale) {
            super(map, scale);
        }

//        @Override
//        public void renderObjects(MapLayer layer) {
//            super.renderObjects(layer);
//            Batch batch = getBatch();
//            for (MapObject obj : layer.getObjects()) {
//                batch.draw(texture, 4, 4, 72, 72);
//            }
//        }
    }

}
