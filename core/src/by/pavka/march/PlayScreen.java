package by.pavka.march;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
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

public class PlayScreen extends GestureDetector implements Screen {
    public static final String MAP = "map/small.tmx";
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
    public Array<Force> selectedForces;
    public ObjectMap<Force, Hex> enemies;

    public Array<Hex> destinations;

    private TiledMap map;
    private TiledMapTileLayer tileLayer;
    private OrthographicCamera uiCamera;
    private InputMultiplexer inputMultiplexer;

    public Window forceWindow;

    private Table group;
    public ImageTextButton timer;
    public ImageTextButton hexButton;
    public ImageTextButton forceButton;
    public boolean dragged;

    private HexGraph hexGraph;

    public PlayScreen(BuonaparteGame game, GestureListener listener, Configurator configurator) {
        super(listener);
        skin = game.getSkin();

        hexListener = (HexGestureListener) listener;
        hexListener.setGestureDetector(this);
        this.game = configurator.game;
        shapeRenderer = new ShapeRenderer();
        map = new TmxMapLoader().load(configurator.getMapName());

        hexagonalTiledMapRenderer = new HexagonalTiledMapRenderer(map);
        tileLayer = (TiledMapTileLayer) map.getLayers().get(configurator.getLayerName());

        int w = tileLayer.getTileWidth() * 10;
        int h = w * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
        playStage = new PlayStage(new ExtendViewport(w, h));

        hexGraph = new HexGraph(map, this);
        Courier.hexGraph = hexGraph;

        configurator.addForces(this);

        enemies = new ObjectMap<>();
        selectedForces = new Array<>();

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

    public void destroyForceWindow() {
        if (forceWindow != null) {
            forceWindow.remove();
            forceWindow = null;
        }
    }

    class SelectForceButton extends TextButton {
        private Force force;

        public SelectForceButton(Force force) {
            super("Select", skin);
            this.force = force;
        }

        public void setForce(Force f) {
            force = f;
        }
    }

    private void createForceWindow() {
        forceWindow = new Window("Forces", skin);
        uiStage.addActor(forceWindow);

        HorizontalGroup hGroup = new HorizontalGroup();
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.setMinCheckCount(1);
        buttonGroup.setMaxCheckCount(1);
        final Label label = new Label("", skin);
        final Force initForce = selectedForces.get(0);
        String text = String.format("%d soldiers \nfatigue: %.1f", initForce.strength.soldiers(), initForce.spirit.fatigue);
        label.setText(text);
        final SelectForceButton select = new SelectForceButton(initForce);
        select.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedForces.clear();
                selectedForces.add(select.force);
                destroyForceWindow();
            }
        });
//        selectedForces = new Array<>();
//        selectedForces.add(initForce);

//        Tree<ForceNode, Force> tree = null;

        for (final Force f : selectedForces) {

//            tree = new Tree<ForceNode, Force>(skin);
//            tree.add(new ForceNode(f));
            final Button button = new TextButton(f.getName(), skin, "toggle");
            hGroup.addActor(button);
            buttonGroup.add(button);
            button.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    final String txt = String.format("%d soldiers \nfatigue: %.1f", f.strength.soldiers(), f.spirit.fatigue);
                    label.setText(txt);
                    select.setForce(f);
//                    selectedForces = new Array<>();
//                    selectedForces.add(f);
                }
            });
        }
        forceWindow.add(hGroup).top();
        forceWindow.row();
//        forceWindow.add(tree).fill();
//        forceWindow.row();
        forceWindow.add(label).fill();
        forceWindow.row();
        forceWindow.add(select);
        forceWindow.row();
        forceWindow.pack();
    }

    class ForceNode extends Tree.Node<ForceNode, Force, Label> {
        public ForceNode(Force f) {
            super(new Label(f.getName(), skin));
            setValue(f);
            if (f instanceof Formation) {
                Formation formation = (Formation) f;
                for (Force force : formation.subForces) {
                    ForceNode fd = new ForceNode(force);
                    add(fd);
                }
            }
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
        timer = new ImageTextButton("Time: " + (int) time, skin, "toggle") {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (!isChecked()) {
                    time += delta;
                    setText("Time: " + (int) time);
                }
            }
        };
        timer.setChecked(true);
        group.add(timer);
        group.setBounds(0, uiStage.getHeight() * 0.9f, uiStage.getWidth(), uiStage.getHeight() * .1f);
        group.left();
        detailedUi = false;
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
                    uncheckForces();
                    selectedPaths = null;
                    destroyForceWindow();
                    show();
                }
            });
            group.add(cancel);

            hexButton = new ImageTextButton("", skin, "toggle");
            hexButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {

                    forceButton.setChecked(false);
                    if (!selectedForces.isEmpty()) {
                        checkHex(selectedForces.get(0).visualHex);
                        uncheckForces();
                    }

                    setHexInfo(selectedHex);
                    destinations = new Array<>();
                    selectedPaths = null;
                    destroyForceWindow();
                }
            });
            group.add(hexButton);

            forceButton = new ImageTextButton("Forces: none", skin, "toggle");
            forceButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!hexButton.isChecked()) {
                        forceButton.setChecked(true);
                        destroyForceWindow();
                        createForceWindow();
                    } else if (selectedHex.hasChildren()) {
                        selectedPaths = null;
                        destinations = new Array<>();
                        checkForces(selectedHex);
                        uncheckHex();
                        hexButton.setChecked(false);
                        forceButton.setChecked(true);
                    }
                }
            });
            group.add(forceButton);
            detailedUi = true;
        }
        destinations = new Array<>();
        selectedPaths = null;
        uncheckHex();
        uncheckForces();
        setHexInfo(hex);
        setForceInfo(hex);
        if (hex.hasChildren()) {
            checkForces(hex);
        } else {
            checkHex(hex);
        }
    }

    public void checkForces(Array<Force> forces) {
        selectedForces = new Array<>();
        for (Force f : forces) {
            selectedForces.add(f);
            f.mark();
        }
        forceButton.setChecked(true);
        hexButton.setChecked(false);
    }

    private void checkForces(Hex hex) {
        Array<Force> checked = new Array<>();
        for (Actor f : hex.getChildren()) {
            checked.add((Force) f);
        }
        checkForces(checked);
    }

    public void checkHex(Hex hex) {
        selectedHex = hex;
        hex.mark();
        hexButton.setChecked(true);
        hexButton.setDisabled(true);
        forceButton.setChecked(false);
        forceButton.setDisabled(true);
    }

    public void setHexInfo(Hex hex) {
        hexButton.setText("Mov.cost = " + hex.cell.getTile().getProperties().get("cost").toString());
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

    public void uncheckHex() {
        if (selectedHex != null) {
            selectedHex.unmark();
        }
        selectedHex = null;
        hexButton.setDisabled(false);
        hexButton.setChecked(false);
    }

    public void uncheckForces() {
        if (selectedForces != null && !selectedForces.isEmpty()) {
            for (Force f : selectedForces) {
                f.unmark();
            }
            selectedForces = new Array<>();
            forceButton.setDisabled(true);
            forceButton.setChecked(false);
        }
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
        map.dispose();
        hexagonalTiledMapRenderer.dispose();
    }

    public float getPlayTime() {
        //TODO refactor
        return (time - ((int) time / 48) * 48) / 2;
    }

    public boolean isNight() {
        if (getPlayTime() > 22 || getPlayTime() < 6) {
            return true;
        }
        return false;
    }

    public float timeToNight() {
        return 22 - getPlayTime();
    }

    public void updateEnemies(ObjectMap<Force, Hex> visualEnemies, ObjectSet<Hex> reconArea, float visualTime) {
        for (Hex h : reconArea) {
            Array<Actor> enem = h.getChildren();
            for (Actor ac : enem) {
                Force f = (Force) ac;
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

        public PlayStage(Viewport viewport) {
            super(viewport);
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
