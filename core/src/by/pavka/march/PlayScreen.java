package by.pavka.march;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import by.pavka.march.map.Hex;
import by.pavka.march.map.HexGraph;
import by.pavka.march.map.Path;
import by.pavka.march.military.Force;
import by.pavka.march.military.Unit;

public class PlayScreen extends GestureDetector implements Screen {
    public static final String MAP = "map/small.tmx";

    BuonaparteGame game;
    PlayStage playStage;
    Skin skin;
    OrthographicCamera camera;
    HexagonalTiledMapRenderer hexagonalTiledMapRenderer;
    ShapeRenderer shapeRenderer;
    Stage uiStage;
    HexGestureListener hexListener;
    public Hex selectedHex;
    public Array<Path> selectedPaths;
    public boolean detailedUi;
    public boolean longPressed;
    public Force selectedForce;

    private TextureAtlas atlas;
    private TiledMap map;
    private TiledMapTileLayer tileLayer;
    private TiledMapTileLayer selectLayer;
    private OrthographicCamera uiCamera;
    private InputMultiplexer inputMultiplexer;

    private Table group;
    Label labelHex;
    Label labelForce;

    public ImageTextButton hexButton;
    public ImageTextButton forceButton;
    public boolean dragged;

    //    private Texture texture = new Texture("unit/friend.png");
    private Sprite sprite;
    private TextureRegion tRegion;
    Batch batch;
    public Force testForce;
    public Force anotherTestForce;

    public PlayScreen(BuonaparteGame game, GestureListener listener) {
        super(listener);
        hexListener = (HexGestureListener) listener;
        hexListener.setGestureDetector(this);
        this.game = game;
        map = new TmxMapLoader().load(MAP);

        atlas = new TextureAtlas("skin/clean-crispy/clean-crispy-ui.atlas");
        skin = new Skin(Gdx.files.internal("skin/clean-crispy/clean-crispy-ui.json"), atlas);
        hexagonalTiledMapRenderer = new BattlefieldRenderer(map, 1);
        shapeRenderer = new ShapeRenderer();

        tileLayer = (TiledMapTileLayer) map.getLayers().get("TileLayer1");
        selectLayer = (TiledMapTileLayer) map.getLayers().get("TileLayer2");
        int w = tileLayer.getTileWidth() * 10;
        int h = w * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
        playStage = new PlayStage(new ExtendViewport(w, h), map);
        setGraphToPlayStage();

        testForce = new Unit(game.getTextureRegion("fr_cav"));
        anotherTestForce = new Unit(game.getTextureRegion("fr_art"));


        playStage.addForce(testForce, 2, 1);
        playStage.addForce(anotherTestForce, 2, 2);

        uiStage = new Stage(new ExtendViewport(w, h)) {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                longPressed = false;
                float stageX = screenToStageCoordinates(new Vector2(screenX, screenY)).x;
                float stageY = screenToStageCoordinates(new Vector2(screenX, screenY)).y;
                if (hit(stageX, stageY, true) != null) {
                    return super.touchDown(screenX, screenY, pointer, button);
                }
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
        camera.position.set(tileLayer.getTileWidth() * tileLayer.getWidth() * 0.1f,
                tileLayer.getTileHeight() * tileLayer.getHeight() * 0.1f, 0);
        uiStage.addListener(new PlayDragListener());
        uiCamera = (OrthographicCamera) uiStage.getCamera();
        inputMultiplexer = new InputMultiplexer(uiStage, this, playStage);
    }

    public void setDetailedUi() {
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
                shapeRenderer.setProjectionMatrix(camera.combined);
            }
        });

        group.add(zoom);
        TextButton cancel = new TextButton("Cancel", skin);
        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                detailedUi = false;
                group.remove();
//                unselectHex();
                uncheckHex();
                selectedPaths = null;
                show();
            }
        });
        group.add(cancel);

//        Window window = new Window("Terrain", skin);
//        labelHex = new Label("", skin);
////        labelForce = new Label("", skin);
//        window.add(labelHex);
////        window.addListener(new ActorGestureListener());
//        window.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                System.out.println("Window!");
//                event.cancel();
//            }
//
//        });
////        window.add(labelForce);
//        group.add(window);

        hexButton = new ImageTextButton("", skin, "toggle");
        hexButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Button disabled: " + hexButton.isDisabled());
                if (hexButton.isChecked() && !hexButton.isDisabled()) {
                    forceButton.setChecked(false);
                    hexButton.setChecked(true);
                    hexButton.setDisabled(true);
                    selectedHex = selectedForce.visualHex;
                    selectedHex.mark();
                    uncheckForce();
                }
            }
        });
        group.add(hexButton);

        forceButton = new ImageTextButton("Force: none ", skin, "toggle");
        group.add(forceButton);


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
                shapeRenderer.setProjectionMatrix(camera.combined);
            }
        });
        group.add(zoom);
        group.setBounds(0, uiStage.getHeight() * 0.9f, uiStage.getWidth(), uiStage.getHeight() * .1f);
        group.left();
        detailedUi = false;
    }

    public void uncheckHex() {
        if (selectedHex != null) {
            int c = selectedHex.col;
            int r = selectedHex.row;
            selectLayer.setCell(c, r, null);
        }
        selectedHex = null;
        hexButton.setDisabled(false);
        hexButton.setChecked(false);
//        selectedPaths = null;
    }

    public void uncheckForce() {
        if (selectedForce != null) {
            selectedForce.setAlpha(0.7f);
            selectedForce.showPath = false;
        }
        selectedForce = null;
        forceButton.setDisabled(true);
        forceButton.setChecked(false);
//        selectedPaths = null;
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
    }

    @Override
    public void resize(int width, int height) {
        playStage.getViewport().update(width, height, false);
        hexagonalTiledMapRenderer.setView(camera);
        shapeRenderer.setProjectionMatrix(camera.combined);
        uiStage.getViewport().update(width, height, true);
        group.setBounds(0, uiStage.getHeight() * 0.9f, uiStage.getWidth(), uiStage.getHeight() * .1f);
        group.left();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 1, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        hexagonalTiledMapRenderer.render();
        if (selectedPaths != null) {
            for (Path path : selectedPaths) {
                path.render(shapeRenderer);
            }
        }
//        if (selectedForce != null) {
//            for (Path path : selectedForce.tail) {
//                path.render(shapeRenderer, 0, 0, 1);
//            }
//        }


//        if (selectedHex != null && selectedHex.hasChildren()) {
//            Force force = (Force) selectedHex.getChild(0);
//            if (force.forcePath != null) {
//                for (Path path : force.forcePath) {
//                    path.render(shapeRenderer, 0, 0, 1f);
//                }
//            }
//        }
        camera.update();
        uiCamera.update();
        playStage.act();
        playStage.draw();
        uiStage.draw();
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
        uiStage.dispose();
        playStage.dispose();
        skin.dispose();
        atlas.dispose();
        map.dispose();
        hexagonalTiledMapRenderer.dispose();
    }

    public void setGraphToPlayStage() {
        playStage.setGraph();
    }

    public HexGraph getHexGraph() {
        return playStage.hexGraph;
    }

    public void addActorToPlayStage(Actor actor) {
        playStage.addActor(actor);
    }

    class PlayStage extends Stage {
        private TiledMap map;
        private HexGraph hexGraph;
        private Array<Force> forces;

        public PlayStage(Viewport viewport, TiledMap map) {
            super(viewport);
            this.map = map;
        }

        private void setGraph() {
            hexGraph = new HexGraph(map, PlayScreen.this);
        }

        private void addForce(Force force, int col, int row) {
            Hex hex = hexGraph.getHex(col, row);
            force.setHex(hex);
            force.setRealHex(hex);
            force.shapeRenderer = shapeRenderer;
            force.playScreen = PlayScreen.this;
//            force.hex = hex;
//            hex.addActor(force);
////            force.setBounds(0, 0, 44, 44);
//            if (force.forcePath == null || force.forcePath.isEmpty()) {
//                force.setPosition(4, 14);
//            } else {
//                force.setPosition(32, 32);
//            }
//            force.setPosition(4, 14);
//            force.setSize(44, 44);
//            force.setColor(force.getColor().r, force.getColor().g, force.getColor().b, 0.5f);
//
//            MoveToAction moveAction = new MoveToAction();
//            moveAction.setPosition(300f, 0f);
//            moveAction.setDuration(50f);
//            force.addAction(moveAction);
        }

        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.LEFT && camera.position.x > 0) {
                camera.translate(-32 * camera.zoom, 0);
            }
            if (keycode == Input.Keys.RIGHT && camera.position.x < PlayScreen.this.tileLayer.getTileWidth() *
                    PlayScreen.this.tileLayer.getWidth()) {
                camera.translate(32 * camera.zoom, 0);
            }
            if (keycode == Input.Keys.UP && camera.position.y < PlayScreen.this.tileLayer.getTileHeight() *
                    PlayScreen.this.tileLayer.getHeight()) {
                camera.translate(0, 32 * camera.zoom, 0);
            }
            if (keycode == Input.Keys.DOWN && camera.position.y > 0) {
                camera.translate(0, -32 * camera.zoom, 0);
            }

            if (keycode == Input.Keys.C) {
                System.out.println("Tail! " + selectedForce.tail.peek().getFromNode().col + " " +
                        selectedForce.tail.peek().getFromNode().row);
                if (selectedForce.forcePath != null && !selectedForce.forcePath.isEmpty()) {
                    System.out.println("Path! " + selectedForce.forcePath.get(0).getToNode().col + " " +
                            selectedForce.forcePath.get(0).getToNode().row);
                    System.out.println(selectedForce.tail.contains(selectedForce.forcePath.get(0), false));
                    System.out.println(selectedForce.forcePath.contains(selectedForce.tail.peek(), false));
                    System.out.println(selectedForce.forcePath.get(0).getToNode().equals(selectedForce.tail.peek().getFromNode()));
                }
            }

            camera.update();
            hexagonalTiledMapRenderer.setView(camera);
            shapeRenderer.setProjectionMatrix(camera.combined);
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
            if ((camera.position.x > 0 && Gdx.input.getDeltaX() > 0 ||
                    camera.position.x < PlayScreen.this.tileLayer.getWidth() * PlayScreen.this.tileLayer.getTileWidth() && Gdx.input.getDeltaX() < 0) &&
                    (camera.position.y > 0 && Gdx.input.getDeltaY() < 0 ||
                            camera.position.y < PlayScreen.this.tileLayer.getHeight() * PlayScreen.this.tileLayer.getTileHeight() &&
                                    Gdx.input.getDeltaY() > 0)) {
                camera.translate(camera.zoom * (-Gdx.input.getDeltaX()), camera.zoom * Gdx.input.getDeltaY());
                this.x = x;
                this.y = y;
            }
            hexagonalTiledMapRenderer.setView(camera);
            shapeRenderer.setProjectionMatrix(camera.combined);
            camera.update();
            dragged = true;
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
