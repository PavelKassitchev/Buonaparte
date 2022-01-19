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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import by.pavka.march.characteristic.Spirit;
import by.pavka.march.characteristic.Strength;
import by.pavka.march.configuration.Configurator;
import by.pavka.march.configuration.FormationValidator;
import by.pavka.march.map.Hex;
import by.pavka.march.map.HexGraph;
import by.pavka.march.map.Path;
import by.pavka.march.military.Courier;
import by.pavka.march.military.Force;
import by.pavka.march.military.Formation;
import by.pavka.march.order.DetachOrder;
import by.pavka.march.structure.ForceNode;
import by.pavka.march.structure.ForceTree;
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
    Stage uiStage;
    HexGestureListener hexListener;
    public Hex selectedHex;
    public Array<Path> selectedPaths;
    public boolean detailedUi;
    public boolean longPressed;
    public Array<Force> selectedForces;
    public ObjectMap<Force, Hex> enemies;
    public Force resigningForce;

    public Array<Hex> destinations;

    private TiledMap map;
    private TiledMapTileLayer tileLayer;
    private OrthographicCamera uiCamera;
    private InputMultiplexer inputMultiplexer;

    public Window forceWindow;
    public Window treeWindow;

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
//        destroyTreeWindow();
    }

    public void destroyTreeWindow() {
        if (treeWindow != null) {
            treeWindow.remove();
            treeWindow = null;
        }
    }

    class ForceButton extends TextButton {
        private Force force;

        public ForceButton(String name, Force force) {
            super(name, skin, "toggle");
            this.force = force;
        }

        public void setForce(Force f) {
            force = f;
        }

        public Force getForce() {
            return force;
        }
    }

    private void setLabelInfo(Label label, Force force) {
        String text = String.format("%d soldiers \n  infantry: %d\n  cavalry: %d\n  guns: %d\n  wagons: %d" +
                        "\nmorale-%.1f\nfatigue-%.1f\nxp-%.1f", force.visualStrength.soldiers(), force.visualStrength.infantry,
                force.visualStrength.cavalry, force.visualStrength.artillery, force.visualStrength.supply, force.visualSpirit.morale,
                force.visualSpirit.fatigue, force.visualSpirit.xp);
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
        final ForceButton select = new ForceButton("Select", initForce);
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
        final ForceButton order = new ForceButton("Orders", initForce);
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
            final Button button = new ForceButton(f.getName(), f);
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
        treeWindow = new Window(f.getName(), skin);
        uiStage.addActor(treeWindow);
        treeWindow.setBounds(uiStage.getWidth() * 0.1f, uiStage.getHeight() * 0.1f,
                uiStage.getWidth() * 0.8f, uiStage.getHeight() * 0.8f);

        final Table tabTable = new Table(skin);
        treeWindow.add(tabTable).left().padLeft(12);
        tabTable.setBounds(0, 0, treeWindow.getWidth(), treeWindow.getHeight() * 0.1f);
        treeWindow.row();

        final ForceTree tree = new ForceTree(skin);
        Table table = new Table(skin);
        table.add(tree).padTop(12);

        final ButtonGroup<ForceButton> trees = new ButtonGroup();
        trees.setMinCheckCount(1);
        trees.setMaxCheckCount(1);

        final OrderView orderLabel = new OrderView("", skin);
        addTreeTab(f, tree, tabTable, trees, orderLabel);
        TextButton detach = new TextButton("Detach Checked", skin);
        detach.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Array<Force> forces = tree.findForcesToDetach();
                if (!forces.isEmpty()) {
                    for (Force force : forces) {
                        force.superForce.viewForces.removeValue(force, true);
                        System.out.println("Removed from view: " + force.getName());
                    }
                    Force force = FormationValidator.createGroup(forces, game);
                    addTreeTab(force, tree, tabTable, trees, orderLabel);
                }
            }
        });
        table.row();
        table.add(detach).left().padTop(12).padLeft(12);
        table.row();
        SelectBox<String> selectBox = new SelectBox(skin);
        String[] items = {"one, two two two", "Is there anybody going to listen..."};
        selectBox.setItems(items);
        table.add(selectBox).left().padTop(12).padLeft(12);

        TextButton sendOrder = new TextButton("Send Order", skin);
        sendOrder.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Force f = trees.getChecked().getForce();
                System.out.println("Send Order Exactly to " + f.getName());
                Force.sendOrder(f, new DetachOrder());
                if (f.remoteHeadForce == null) {
                    ForceButton fb = trees.getChecked();
                    fb.remove();
                    trees.remove(fb);
                    trees.getButtons().get(0).setChecked(true);
                    orderLabel.clear();
                    Force force = trees.getButtons().get(0).getForce();
                    updateTree(tree, force);
                } else {
                    destroyTreeWindow();
                }
            }
        });
        table.row();
        table.add(orderLabel);
        table.row();
        table.add(sendOrder).left().padTop(12);

        ScrollPane scrollPane = new ScrollPane(table, skin);
        scrollPane.setScrollingDisabled(true, false);
        treeWindow.add(scrollPane).left().top().width(treeWindow.getWidth());

        ImageButton closeButton = new ImageButton(skin.getDrawable("button-close"));
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                destroyTreeWindow();
            }
        });
        treeWindow.getTitleTable().add(closeButton).right();
    }

    private void addTreeTab(final Force f, final ForceTree tree, Table tabTable, ButtonGroup trees) {
        ForceButton single = new ForceButton(f.getName(), f);
        single.setChecked(true);
        tabTable.add(single).left();
        trees.add(single);
        single.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateTree(tree, f);
            }
        });
        updateTree(tree, f);
    }

    private void addTreeTab(final Force f, final ForceTree tree, Table tabTable, ButtonGroup trees,
                            final OrderView orderLabel) {
        ForceButton single = new ForceButton(f.getName(), f);
        single.setChecked(true);
        tabTable.add(single).left();
        trees.add(single);
        single.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Click!");
                updateTree(tree, f, orderLabel);
            }
        });
        updateTree(tree, f, orderLabel);
    }

    private void updateTree(ForceTree tree, Force force, OrderView orderLabel) {
        updateTree(tree, force);
        if (force.superForce != null) {
            orderLabel.setText("Detach from " + force.findHyperForce().getName());
        } else if (force.nation == null){
            orderLabel.setText("Detach from " + ((Formation)force).subForces.get(0).findHyperForce().getName());
        } else {
            orderLabel.clear();
        }
    }

    private void updateTree(ForceTree tree, Force force) {
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
                    destroyTreeWindow();
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
                    destroyTreeWindow();
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

    public void setForceInfo(Force force) {
        String info = String.format("1 Force\n%d Soldiers", force.visualStrength.soldiers());
        forceButton.setText(info);
    }

    public void setForceInfo(Hex hex) {
        if (hex.hasChildren()) {
            SnapshotArray<Actor> forces = hex.getChildren();
            int forceNumber = 0;
            int soldierNumber = 0;
            for (Actor a : forces) {
                if (a instanceof Formation) {
                    Formation f = (Formation)a;
                    System.out.println("Setting force info for " + f + " number = " + f.visualStrength.infantry);
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
