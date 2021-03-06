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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectFloatMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import by.pavka.march.characteristic.Spirit;
import by.pavka.march.characteristic.Strength;
import by.pavka.march.configuration.Configurator;
import by.pavka.march.map.Hex;
import by.pavka.march.map.HexGraph;
import by.pavka.march.map.Path;
import by.pavka.march.military.Courier;
import by.pavka.march.military.Force;
import by.pavka.march.military.Formation;
import by.pavka.march.military.UnitType;
import by.pavka.march.order.FollowOrder;
import by.pavka.march.structure.ForceButton;
import by.pavka.march.structure.ForceNode;
import by.pavka.march.structure.ForceTree;
import by.pavka.march.structure.ForceTreeWindow;
import by.pavka.march.structure.OrderView;

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
    public Stage uiStage;
    HexGestureListener hexListener;
    public Hex selectedHex;
    public Array<Path> selectedPaths;
    public boolean detailedUi;
    public boolean longPressed;
    public Array<Force> selectedForces;
    public ObjectMap<Force, Hex> enemies;
    public Force resigningForce;
    public Formation headForce;

    public Array<Hex> destinations;
    public boolean additiveOrder;

    private TiledMap map;
    private TiledMapTileLayer tileLayer;
    private OrthographicCamera uiCamera;
    private InputMultiplexer inputMultiplexer;

    public Window forceWindow;
    public ForceTreeWindow treeWindow;

    private Table group;
    public ImageTextButton timer;
    public ImageTextButton hexButton;
    public ImageTextButton forceButton;
    public boolean dragged;

    public HexGraph hexGraph;

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

        headForce = configurator.addForces(this);

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
//        destroyTreeWindow();
    }

    public void destroyTreeWindow() {
        if (treeWindow != null) {
            treeWindow.remove();
            treeWindow = null;
        }
    }

    public void destroySelectBox() {
        if (playStage.selectForce != null) {
            playStage.selectForce.remove();
            playStage.selectForce = null;
        }
    }

    public void activateSelectBox(Force frc, Hex hex, final FollowOrder fOrder) {
//        Array<Formation> allies = frc.getAllyFormations(hex);
//        final SelectBox<Formation> selectForce = new SelectBox<>(frc.playScreen.game.getSkin());
//        selectForce.setItems(allies);
//        addActorToPlayStage(selectForce);
//        playStage.selectForce = selectForce;
//        selectForce.setBounds(hex.getX(), hex.getY(), 120, 20);
//        selectForce.addListener(new ChangeListener() {
//
//            @Override
//            public void changed(ChangeEvent changeEvent, Actor actor) {
//                Formation target = selectForce.getSelected();
//                System.out.println("Setting TARGET = " + target);
//                jOrder.setTargetForce(target);
//                System.out.println("JORDER TARGET = " + jOrder.target);
//                destroySelectBox();
//                treeWindow.setVisible(true);
//            }
//        });

        final SelectBox<? extends Force> selectForce = fOrder.createSelectBox(frc, hex);
//        Array<Formation> allies = frc.getAllyFormations(hex);
//        final SelectBox<Formation> selectForce = new SelectBox<>(frc.playScreen.game.getSkin());
//        selectForce.setItems(allies);
        addActorToPlayStage(selectForce);
        playStage.selectForce = selectForce;
        selectForce.setBounds(hex.getX(), hex.getY(), 120, 20);
        selectForce.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
//                Formation target = selectForce.getSelected();
                Force target = selectForce.getSelected();
                fOrder.setTargetForce(target);
                destroySelectBox();
                treeWindow.setVisible(true);
            }
        });
    }

    private void setLabelInfo(Label label, Force force) {
        String text = String.format("%d soldiers \n  infantry: %d\n  cavalry: %d\n  guns: %d\n  wagons: %d" +
                        "\nmorale-%.1f\nfatigue-%.1f\nxp-%.1f\nfood-%.2f\nammo-%.2f\nspeed-%.1f", force.visualStrength.soldiers(), force.visualStrength.infantry,
                force.visualStrength.cavalry, force.visualStrength.artillery / UnitType.GUNNER_PER_CANNON,
                force.visualStrength.supply / UnitType.WAGONER_PER_WAGON, force.visualSpirit.morale,
                force.visualSpirit.fatigue, force.visualSpirit.xp, force.visualStrength.food, force.visualStrength.ammo, force.speed);
        label.setText(text);
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
        setLabelInfo(label, initForce);
//        final ForceButton select = new ForceButton("Select", initForce);
        final ForceButton select = new ForceButton(initForce, "Select", skin);
        select.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedForces.clear();
                selectedForces.add(select.force);
                destroyForceWindow();
                destroyTreeWindow();
                setForceInfo(select.force);
            }
        });
        final ForceButton order = new ForceButton(initForce, "Orders", skin);
        order.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedForces.clear();
                selectedForces.add(order.force);
                order.force.setTreeViewStructure();
                destroyForceWindow();
                destroyTreeWindow();
                setForceInfo(order.force);
                createForceTreeWindow(order.force);
            }
        });
        for (final Force f : selectedForces) {
            final Button button = new ForceButton(f, skin);
            hGroup.addActor(button);
            buttonGroup.add(button);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setLabelInfo(label, f);
                    select.setForce(f);
                    order.setForce(f);
                }
            });
        }
        forceWindow.add(hGroup).top();
        forceWindow.row();
        forceWindow.add(label).fill();
        forceWindow.row();
        forceWindow.add(select);
        forceWindow.row();
        forceWindow.add(order);
        forceWindow.pack();
    }

    private void createForceTreeWindow(final Force f) {
        treeWindow = new ForceTreeWindow(f, skin);
    }

    public void updateTree(ForceTree tree, Force force, OrderView orderLabel) {
        updateTree(tree, force);
        if (force.superForce != null) {
            orderLabel.setText("Detach from " + force.findHyperForce().getName());
        } else if (force.nation == null){
            orderLabel.setText("Detach from " + ((Formation)force).subForces.get(0).findHyperForce().getName());
        } else {
            if (force.visualOrders.size() == 0) {
                orderLabel.clear();
            } else {
                orderLabel.setText(force.visualOrders.first().toString());
            }
        }
    }

    public void updateTree(ForceTree tree, Force force) {
        tree.clearChildren();
        ForceNode forceNode = new ForceNode(force, skin);
        forceNode.getActor().checkBox.setDisabled(true);
        forceNode.getActor().assignButton.setResigning(false);
        forceNode.getActor().assignButton.setChecked(true);
        forceNode.getActor().assignButton.setDisabled(true);

        tree.add(forceNode);
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
        timer = new ImageTextButton("Time: " + (int) time, skin, "clock") {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (!isChecked()) {
                    time += delta;
                    setText("Time: " + (int) time);
                }
            }
        };
        timer.getLabelCell().width(96);
        timer.setChecked(true);
        group.add(timer).padRight(4);
        group.setBounds(0, uiStage.getHeight() * 0.9f, uiStage.getWidth(), uiStage.getHeight() * .1f);
        group.left();
        detailedUi = false;
    }

    public void setDetailedUi(Hex hex) {
        if (!detailedUi) {
            ImageButton cancel = new ImageButton(skin, "cancel");
            cancel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    detailedUi = false;
                    group.remove();
                    uncheckHex();
                    uncheckForces();
                    selectedPaths = null;
                    destroyForceWindow();
                    destroyTreeWindow();
                    show();
                }
            });

            hexButton = new ImageTextButton("", skin, "landscape");
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
                    destroyTreeWindow();
                }
            });
            group.add(hexButton).padRight(4);
            forceButton = new ImageTextButton("Forces: none", skin, "shako");
            forceButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!hexButton.isChecked()) {
                        forceButton.setChecked(true);
                        destroyForceWindow();
                        destroyTreeWindow();
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
            group.add(cancel);
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
        hexButton.setText("Point " + hex.col + ":" + hex.row + '\n' +
                "Mov.cost " + hex.cell.getTile().getProperties().get("cost").toString() + '\n' +
                String.format("Crop %.1f", hex.visualCrop));
    }

    public void setForceInfo(Force force) {
        String info = String.format("1 Forces\n%d Soldiers", force.visualStrength.soldiers());
        forceButton.setText(info);
    }

    public void setForceInfo(Hex hex) {
        if (hex.hasForces()) {
            SnapshotArray<Actor> forces = hex.getChildren();
            int forceNumber = 0;
            int soldierNumber = 0;
            for (Actor a : forces) {
                if (a instanceof Formation) {
                    Formation f = (Formation)a;
                }
                forceNumber++;
                soldierNumber += ((Force) a).visualStrength.soldiers();
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
//        group.setDebug(true, true);
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

//        System.out.println(treeWindow);
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

//    public void updateEnemies(ObjectMap<Force, Hex> visualEnemies, ObjectSet<Hex> reconArea, float visualTime) {
//        for (Hex h : reconArea) {
//            Array<Actor> enem = h.getChildren();
//            for (Actor ac : enem) {
//                Force f = (Force) ac;
//                if (f.isEnemy() && f.visualTime <= visualTime && !visualEnemies.containsKey(f)) {
//                    enemies.remove(f);
//                }
//            }
//        }
//        for (Force f : visualEnemies.keys()) {
//            if (!enemies.containsKey(f) || f.visualTime <= visualTime) {
//                f.visualTime = visualTime;
//                Hex h = visualEnemies.get(f);
//                f.setVisualHex(h);
//                f.visualStrength = new Strength(f.interStrength);
//                f.visualSpirit = new Spirit(f.interSpirit);
//                enemies.put(f, h);
//            }
//        }
//    }

    public void updateEnemies(ObjectMap<Force, Hex> visualEnemies, ObjectFloatMap<Hex> scoutMap, float visualTime) {
        for (Hex h : scoutMap.keys()) {
            h.visualCrop = scoutMap.get(h, 0);
            Array<Actor> enem = h.getChildren();
            for (Actor ac : enem) {
                Force f = (Force) ac;
                if (f.isEnemy() && f.visualTime <= visualTime && !visualEnemies.containsKey(f)) {
                    enemies.remove(f);
                }
            }
        }
        for (Force f : visualEnemies.keys()) {
            if (!enemies.containsKey(f) || f.visualTime <= visualTime) {
                f.visualTime = visualTime;
                Hex h = visualEnemies.get(f);
                f.setVisualHex(h);
                f.visualStrength = new Strength(f.interStrength);
                f.visualSpirit = new Spirit(f.interSpirit);
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

        private SelectBox<? extends Force> selectForce;

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
