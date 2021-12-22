package by.pavka.march;

import static by.pavka.march.configuration.Nation.AUSTRIA;
import static by.pavka.march.configuration.Nation.FRANCE;

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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import by.pavka.march.configuration.Configurator;
import by.pavka.march.map.Hex;
import by.pavka.march.map.HexGraph;
import by.pavka.march.map.Path;
import by.pavka.march.military.Courier;
import by.pavka.march.military.Force;
import by.pavka.march.military.Formation;
import by.pavka.march.military.Unit;

public class PlayScreen extends GestureDetector implements Screen {
    public static final String MAP = "map/small.tmx";
//    public static final Nation nation = FRANCE;
    public static final float HOURS_IN_SECOND = 0.5f;

    public BuonaparteGame game;
    public float time;

    PlayStage playStage;
    Skin skin;
    OrthographicCamera camera;
    HexagonalTiledMapRenderer hexagonalTiledMapRenderer;
    public ShapeRenderer shapeRenderer;
    Stage uiStage;
    HexGestureListener hexListener;
    public Hex selectedHex;
    public Array<Path> selectedPaths;
    public boolean detailedUi;
    public boolean longPressed;
    public Force selectedForce;
//    public ObjectIntMap<Force> enemies;
    public ObjectMap<Force, Hex> enemies;

    public Array<Hex> destinations;

    private TextureAtlas atlas;
    private TiledMap map;
    private TiledMapTileLayer tileLayer;
    private OrthographicCamera uiCamera;
    private InputMultiplexer inputMultiplexer;

    private Table group;
    public ImageTextButton timer;
    public ImageTextButton hexButton;
    public ImageTextButton forceButton;
    public boolean dragged;

    //    private Texture texture = new Texture("unit/friend.png");
    private Sprite sprite;
    private TextureRegion tRegion;
    Batch batch;
    public Force testForce;
    public Force anotherTestForce;
    public Formation headForce;
    public Force enemy;

    private HexGraph hexGraph;

    public PlayScreen(BuonaparteGame game, GestureListener listener, Configurator configurator) {
        super(listener);
//
//        atlas = new TextureAtlas("skin/clean-crispy/clean-crispy-ui.atlas");
//        skin = new Skin(Gdx.files.internal("skin/clean-crispy/clean-crispy-ui.json"), atlas);
        skin = game.getSkin();

        hexListener = (HexGestureListener) listener;
        hexListener.setGestureDetector(this);
        this.game = configurator.game;
        shapeRenderer = new ShapeRenderer();
        map = new TmxMapLoader().load(configurator.getMapName());
//        map = configurator.getMap();
//        hexagonalTiledMapRenderer = configurator.getHexagonalRenderer();
        hexagonalTiledMapRenderer = new HexagonalTiledMapRenderer(map);
        tileLayer = (TiledMapTileLayer) map.getLayers().get(configurator.getLayerName());
//        tileLayer = configurator.getTileLayer();
        int w = tileLayer.getTileWidth() * 10;
        int h = w * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
        playStage = new PlayStage(new ExtendViewport(w, h));
//        hexGraph = configurator.getHexGraph(this);

        hexGraph = new HexGraph(map, this);
        Courier.hexGraph = hexGraph;

//        testForce = new Unit(game.getTextureRegion("fr_cav"));
//        anotherTestForce = new Unit(game.getTextureRegion("fr_art"));
//        headForce = new Formation(game.getTextureRegion("fr_inf"));
//        testForce.remoteHeadForce = headForce;
//        anotherTestForce.remoteHeadForce = headForce;
//        testForce.nation = FRANCE;
//        anotherTestForce.nation = FRANCE;
//        headForce.nation = FRANCE;
//
//        enemy = new Unit(game.getTextureRegion("hostile"));
//        enemy.nation = AUSTRIA;
//
//
//        playStage.addForce(testForce, 2, 1);
//        playStage.addForce(anotherTestForce, 2, 2);
//        playStage.addForce(headForce, 4, 4);
//        playStage.addForce(enemy, 8, 8);



        configurator.addForces(this);
//        enemies = new ObjectIntMap<Force>();
        enemies = new ObjectMap<>();

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
        int w = tileLayer.getTileWidth() * 10;
        int h = w * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
        playStage = new PlayStage(new ExtendViewport(w, h));

        hexGraph = new HexGraph(map, this);
        Courier.hexGraph = hexGraph;
//        Courier.playScreen = this;
        //setGraphToPlayStage();

        //TODO reformat!

        testForce = new Unit(game.getTextureRegion("fr_cav"));
        anotherTestForce = new Unit(game.getTextureRegion("fr_art"));
        headForce = new Formation(game.getTextureRegion("fr_inf"));
        testForce.remoteHeadForce = headForce;
        anotherTestForce.remoteHeadForce = headForce;
        testForce.nation = FRANCE;
        anotherTestForce.nation = FRANCE;
        headForce.nation = FRANCE;

        enemy = new Unit(game.getTextureRegion("hostile"));
        enemy.nation = AUSTRIA;


        playStage.addForce(testForce, 2, 1);
        playStage.addForce(anotherTestForce, 2, 2);
        playStage.addForce(headForce, 4, 4);
        playStage.addForce(enemy, 8, 8);

//        enemies = new ObjectIntMap<Force>();
        enemies = new ObjectMap<>();
//        enemies.put(enemy, 0);


//

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

    public void setDetailedUi(Hex hex) {
        if (!detailedUi) {
            TextButton cancel = new TextButton("Cancel", skin);
            cancel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    detailedUi = false;
                    group.remove();
                    uncheckHex();
                    uncheckForce();
                    selectedPaths = null;
                    show();
                }
            });
            group.add(cancel);

            hexButton = new ImageTextButton("", skin, "toggle");
            hexButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (hexButton.isChecked() && !hexButton.isDisabled()) {
                        forceButton.setChecked(false);
                        checkHex(selectedForce.visualHex);
                        uncheckForce();
                        setHexInfo(selectedHex);
                        destinations = new Array<>();
                        selectedPaths = null;
                    }
                }
            });
            group.add(hexButton);

            forceButton = new ImageTextButton("Forces: none", skin, "toggle");
            group.add(forceButton);
            detailedUi = true;
        }
        destinations = new Array<>();
        selectedPaths = null;
        uncheckHex();
        uncheckForce();
        setHexInfo(hex);
        setForceInfo(hex);
        if (hex.getChildren().size == 1) {
            Force f = (Force) hex.getChild(0);
            checkForce(f);
        } else {
            checkHex(hex);
        }
    }

    public void checkHex(Hex hex) {
        selectedHex = hex;
        hex.mark();
        hexButton.setChecked(true);
        hexButton.setDisabled(true);
    }

    public void setHexInfo(Hex hex) {
        hexButton.setText("Mov.cost = " + hex.cell.getTile().getProperties().get("cost").toString());
    }

    public void checkForce(Force force) {
        selectedForce = force;
        force.mark();
        forceButton.setChecked(true);
        hexButton.setChecked(false);
    }

    public void setForceInfo(Hex hex) {
        if (hex.hasChildren()) {
            SnapshotArray<Actor> forces = hex.getChildren();
            int forceNumber = 0;
            int soldierNumber = 0;
            for (Actor a : forces) {
                forceNumber++;
                soldierNumber += ((Force) a).strength.soldiers();
            }
            String info = String.format("%d Forces\n%d Soldiers", forceNumber, soldierNumber);
            forceButton.setText(info);
        } else {
            forceButton.setText("Forces: none");
        }
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
        timer = new ImageTextButton("Time: " + (int)time, skin, "toggle") {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (!isChecked()) {
                    time += delta;
                    setText("Time: " + (int)time);
                }
            }
        };
        timer.setChecked(true);
        group.add(timer);
        group.setBounds(0, uiStage.getHeight() * 0.9f, uiStage.getWidth(), uiStage.getHeight() * .1f);
        group.left();
        detailedUi = false;
    }

    public void uncheckHex() {
        if (selectedHex != null) {
            selectedHex.unmark();
        }
        selectedHex = null;
        hexButton.setDisabled(false);
        hexButton.setChecked(false);
    }

    public void uncheckForce() {
        if (selectedForce != null) {
            selectedForce.unmark();
        }
        selectedForce = null;
        forceButton.setDisabled(true);
        forceButton.setChecked(false);
//        forceButton.setText("Forces: none");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        if (!detailedUi) {
            setGeneralUi();
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
        camera.update();
        uiCamera.update();
        playStage.act();
        playStage.draw();
        uiStage.act();
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

    public float getPlayTime() {
        //TODO refactor
        return (time - ((int)time / 48) * 48) / 2;
    }

    public boolean isNight() {
        if (getPlayTime() > 22 || getPlayTime() < 6) {
            return true;
        }
        return false;
    }

    public boolean isMorning() {
        return false;
    }

    public float timeToNight() {
        return 22 - getPlayTime();
    }

    public void updateEnemies(ObjectMap<Force, Hex> visualEnemies, ObjectSet<Hex> reconArea, float visualTime) {
        for (Hex h : reconArea) {
            Array<Actor> enem = h.getChildren();
            for (Actor ac : enem) {
                Force f = (Force)ac;
                if (f.isEnemy() && f.visualTime <= visualTime && !visualEnemies.containsKey(f)) {
                    enemies.remove(f);
//                    f.unvisualize();
                }
            }
        }
        for (Force f : visualEnemies.keys()) {
            if (!enemies.containsKey(f) || f.visualTime <= visualTime) {
                f.visualTime = visualTime;
                Hex h = visualEnemies.get(f);
                f.setVisualHex(h);
                enemies.put(f, h);
            }
        }
    }


    public HexGraph getHexGraph() {
        return hexGraph;
    }

    public void addActorToPlayStage(Actor actor) {
        playStage.addActor(actor);
    }

    public class PlayStage extends Stage {
//        private TiledMap map;
        //private HexGraph hexGraph;

        public PlayStage(Viewport viewport) {
            super(viewport);
//            this.map = map;
        }

//        private void setGraph() {
//            hexGraph = new HexGraph(map, PlayScreen.this);
//        }

        private void addForce(Force force, int col, int row) {
            Hex hex = PlayScreen.this.hexGraph.getHex(col, row);
            force.setVisualHex(hex);
            force.setRealHex(hex);
            force.shapeRenderer = shapeRenderer;
            force.playScreen = PlayScreen.this;
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
    }

}
