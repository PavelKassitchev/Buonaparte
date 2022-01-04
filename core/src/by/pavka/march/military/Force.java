package by.pavka.march.military;

import static by.pavka.march.BuonaparteGame.HEX_SIZE_M;
import static by.pavka.march.BuonaparteGame.TILE_SIZE_PX;
import static by.pavka.march.PlayScreen.HOURS_IN_SECOND;
import static by.pavka.march.characteristic.Stock.NORMAL_FOOD_STOCK_DAYS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

import by.pavka.march.PlayScreen;
import by.pavka.march.characteristic.March;
import by.pavka.march.characteristic.MarchConfig;
import by.pavka.march.characteristic.Spirit;
import by.pavka.march.characteristic.Stock;
import by.pavka.march.characteristic.Strength;
import by.pavka.march.configuration.Nation;
import by.pavka.march.map.Hex;
import by.pavka.march.map.Path;
import by.pavka.march.order.Order;

public abstract class Force extends Image {
    public static final String CAV = "cavalry";
    public static final String INF = "infantry";
    public static final String ENG = "engineer";
    public static final String ART = "artillery";
    public static final String SUP = "supply";
    public static final float MAX_SPEED = 36.0f;

    public static final int TEST_SPEED = 2;
    public static final int TEST_REPORT_PERIOD = 1;
    public static final int TEST_REPORT_SPEED = 1;
    public static final int TEST_ORDER_SPEED = 5;

    public Nation nation;
    String name;

    public Strength strength;
    public Spirit spirit;

    float speed;

    public Formation superForce;
    public Formation remoteHeadForce;

    TextureRegion icon;
    public Hex hex;
    public Hex visualHex;


    float start = 0;
    float report = 0;
    float camp = 0;
    public Array<Path> forcePath;
    public Array<Path> visualForcePath;
    public Array<Path> tail;
    public Array<Path> visualTail;
    //    public ObjectIntMap<Force> visualEnemies;
    public ObjectMap<Force, Hex> visualEnemies;
    public ObjectSet<Hex> reconArea;
    public float visualTime;
    public int sections;
    public int currentSections;
    public int size;
    public ShapeRenderer shapeRenderer;
    public PlayScreen playScreen;
    public boolean showPath;
    MarchConfig marchConfig;
    public boolean hasDayToRest;

    public Force() {
    }

    public Force(TextureRegion region, int inf, float len) {
        super(region);
        unmark();
        strength = new Strength();
        strength.infantry = inf;
        strength.length = len;

        sections = (int) len / 3500;
        currentSections = 1;
        tail = new Array<>();
        visualTail = new Array<>();
        forcePath = new Array<>();
        visualForcePath = new Array<>();
//        visualEnemies = new ObjectIntMap<>();
        visualEnemies = new ObjectMap<>();
        reconArea = new ObjectSet<>();

        spirit = new Spirit(0, 1, 0);

        marchConfig = new MarchConfig(March.REGULAR);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("FORCE!");
            }
        });
    }

    public float findReportDistance(Force force) {
        float delay = 0;
        if (force != null) {
            delay = (float) Courier.courierDelay(force.hex, hex) * 1000 / HOURS_IN_SECOND;
        }
        return delay;
    }

    public float findCommandDistance() {
        return findReportDistance(remoteHeadForce);
    }

    public static void sendOrder (final Force force, final Order order) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (force.playScreen.timer.isChecked()) {
                        Thread.sleep(20);
                    }
                    Thread.sleep((long) force.findCommandDistance());
                    while (force.playScreen.timer.isChecked()) {
                        Thread.sleep(20);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        order.execute(force);
                    }
                });
            }
        }).start();
    }

    public static void sendMoveOrder(final Force force, final Array<Hex> destinations) {

        new Thread(new Runnable() {
            @Override
            public void run() {
//                int orderDelay = 0;
//                if (force.remoteHeadForce != null) {
//                    GraphPath<Hex> hexPath = force.playScreen.getHexGraph().findPath(force.remoteHeadForce.hex, force.hex);
//                    orderDelay = hexPath.getCount() > 2 ? hexPath.getCount() - 2 : 0;
//                    System.out.println("DESTINATIONS SENT WITH DELAY " + orderDelay);
//                }
                try {
                    //Thread.sleep(force.findCommandDistance() * 1000);
                    Thread.sleep((long) force.findCommandDistance());

                    //Thread.sleep(TEST_ORDER_SPEED * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        GraphPath<Hex> hexPath;
                        Array<Path> paths = new Array<>();
                        Hex start = force.hex;
                        for (Hex h : destinations) {
                            hexPath = force.playScreen.getHexGraph().findPath(start, h);
                            Hex st = null;
                            Hex en;
                            for (Hex hx : hexPath) {
                                en = hx;
                                if (st != null) {
                                    paths.add(force.playScreen.getHexGraph().getPath(st, en));
                                }
                                st = hx;
                            }
                            start = st;
                        }
                        force.forcePath = paths;
                        for (Path p : force.forcePath) {
                            System.out.println(p.fromHex + "   " + p.toHex);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (nation == playScreen.game.nation || playScreen.enemies.containsKey(this)) {
            super.draw(batch, parentAlpha);
            batch.end();
            for (Path path : visualTail) {
                path.render(shapeRenderer, 0, 0, 1);
            }
            if (visualForcePath != null && !visualForcePath.isEmpty()) {
                Path p = visualForcePath.first();
                p.render(shapeRenderer, 1, 0, 0);
                if (showPath) {
                    for (Path path : visualForcePath) {
                        path.render(shapeRenderer, 0.4f, 0.2f, 0.4f);
                    }
                }
            }
            batch.begin();
        }
    }

    public void setRealHex(Hex hex) {
        if (this.hex != null) {
            this.hex.removeForce(this);
        }
        this.hex = hex;
        hex.addForce(this);
    }

    public void setVisualHex(Hex hex) {
        if (visualHex != null) {
            visualHex.removeActor(this);
        }
        visualHex = hex;
        visualHex.addActor(this);

        double len = strength.length;
        int iconSize;
        if (len < HEX_SIZE_M / 3) {
            iconSize = 1;
        } else if (len < HEX_SIZE_M) {
            iconSize = 2;
        } else if (len < HEX_SIZE_M * 2) {
            iconSize = 3;
        } else {
            iconSize = 4;
        }
        setBounds(0, 0, iconSize * TILE_SIZE_PX / 5.0f, iconSize * TILE_SIZE_PX / 5.0f);
        float offsetY = TILE_SIZE_PX * (1 - iconSize / 5.0f) / 2;
        float offsetX = TILE_SIZE_PX * (0.75f - iconSize / 5.0f) / 2;
        setPosition(offsetX, offsetY);

    }

    public void unvisualize() {
        if (visualHex != null) {
            visualHex.removeActor(this);
        }
        visualHex = null;
    }


    public void mark() {
        setColor(getColor().b, getColor().g, getColor().r, 1);
        showPath = true;
    }

    public void unmark() {
        setColor(getColor().b, getColor().g, getColor().r, 0.75f);
        showPath = false;
    }

    public boolean isEnemy() {
        return nation != playScreen.game.nation;
    }

    public void recon() {
        final ObjectMap<Force, Hex> enemies = new ObjectMap<>();
        reconArea = getReconArea();
        for (Hex h : reconArea) {
            if (h.enemiesOf(this) != null) {
                for (Force f : h.enemiesOf(this)) {
                    if (ReconData.reconEnemy(f) != null) {
//                        f.setVisualHex(h);
                        enemies.put(f, h);
                    }
                }
            }
        }
        updateEnemies(enemies);
    }

    public void updateEnemies(ObjectMap<Force, Hex> enemies) {
        for (Force f : visualEnemies.keys()) {
            if (!enemies.containsKey(f)) {
                visualEnemies.remove(f);
            }
        }
        visualEnemies.putAll(enemies);
    }

    public ObjectSet<Hex> getReconArea() {
        int radius;
        if (strength.recon <= 6) {
            radius = 1;
        } else if (strength.recon <= 18) {
            radius = 2;
        } else if (strength.recon <= 36) {
            radius = 3;
        } else if (strength.recon <= 60) {
            radius = 4;
        } else {
            radius = 5;
        }
        ObjectSet<Hex> reconArea = playScreen.getHexGraph().getArea(hex, radius, new ObjectSet<Hex>());
        if (radius > 1) {
            if (forcePath != null && forcePath.size > radius) {
                Hex h = forcePath.get(radius).toHex;
                reconArea.add(h);
            }
        }
        return reconArea;
    }

//    private void sendReport(float delta) {
//        //final int delay = findCommandDistance() > 1 ? findCommandDistance() - 1 : 0;
//        if (findCommandDistance() > 1) {
//            report += delta;
//            if (report > TEST_REPORT_PERIOD) {
//                report = 0;
//                sendReport();
//            }
//        }
//    }

    private void sendReport(final String tag) {
        final float delay;
        if (remoteHeadForce == null || playScreen.getHexGraph().areNeighbours(remoteHeadForce.hex, hex)) {
            delay = 0;
        } else {
            delay = findCommandDistance();
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                final Hex delayedHex = hex;
                final Array<Path> delayedTail = new Array<>(tail);
                final Array<Path> delayedPath = new Array<>(forcePath);
//                final ObjectIntMap<Force> delayedEnemies = new ObjectIntMap<>(visualEnemies);
                final ObjectMap<Force, Hex> delayedEnemies = new ObjectMap<Force, Hex>(visualEnemies);
                final ObjectSet<Hex> delayedArea = reconArea;
                final float time = playScreen.time;
                try {
                    while (playScreen.timer.isChecked()) {
                        Thread.sleep(20);
                    }
                    //Thread.sleep(TEST_REPORT_SPEED * 1000);
                    //Thread.sleep(delay * 1000);
                    Thread.sleep((long) delay);
                    while (playScreen.timer.isChecked()) {
                        Thread.sleep(50);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (time > visualTime) {
                    Gdx.app.postRunnable(new Runnable() {

                        @Override
                        public void run() {
                            visualHex = delayedHex;
                            visualTail = delayedTail;
                            visualForcePath = delayedPath;
                            setVisualHex(visualHex);
                            visualTime = time;
                            //playScreen.enemies.putAll(delayedEnemies);
                            playScreen.updateEnemies(delayedEnemies, delayedArea, visualTime);
                        }
                    });
                }
            }
        }).start();

    }

    private boolean readyToRecon(float delta) {
        report += delta;
        if (report > 1) {
            report = 0;
            return true;
        }
        return false;
    }

    @Override
    public void act(float delta) {
        if (!playScreen.timer.isChecked()) {
            super.act(delta);
            float f = delta * HOURS_IN_SECOND * MarchConfig.REST_FACTOR;
            rest(f);
            if (forcePath != null && !forcePath.isEmpty() || !tail.isEmpty()) {
                move(delta);
            }
            if (!isEnemy() && readyToRecon(delta)) {
                recon();
                sendReport("JUST RECON");
            }
        }
    }

    public void camp(float delta, float duration) {
        fatigue(delta * HOURS_IN_SECOND * marchConfig.fatigueFactor());
        if (currentSections > 1) {
            camp += delta;
            if (camp > duration / HOURS_IN_SECOND) {
                System.out.println("Camping time - " + camp + " Fatigue - " + spirit.fatigue);
                camp = 0;
                System.out.println("Tail section number: " + currentSections);
                currentSections--;
                tail.removeIndex(0);
            }
        }

    }

    public abstract void fatigue(float f);

    public float prepareToCamp() {
        if (!tail.isEmpty()) {
            Path path = tail.get(0);
            float timeToCross = Hex.SIZE * path.getCost() / (speed * marchConfig.speedFactor());
            if (timeToCross * (currentSections - 1) > playScreen.timeToNight()) {
                return timeToCross;
            }
        }
        return 0;
    }

    public void normalMarch(float delta) {
        start += delta;
        if (!forcePath.isEmpty()) {
            fatigue(delta * HOURS_IN_SECOND * marchConfig.fatigueFactor());
            Path path = forcePath.get(0);
            float timeToCross = Hex.SIZE * path.getCost() / (speed * marchConfig.speedFactor());
            if (start > timeToCross / HOURS_IN_SECOND) {
                start = 0;
                forcePath.removeIndex(0);
                Hex toHex = path.getToNode();
                setRealHex(toHex);

                if (playScreen.selectedForces.contains(this, true)
                        && playScreen.destinations.contains(toHex, true)) {
                    playScreen.destinations.removeValue(toHex, true);
                }

                if (tail.contains(path.reverse(), false)) {
                    tail.pop();
                    if (currentSections > 1) {
                        currentSections--;
                    }
                } else {
                    tail.add(path);
                    currentSections++;
                    if (currentSections > sections) {
                        currentSections--;
                        tail.removeIndex(0);
                    }
                }
                if (!isEnemy()) {
                    recon();
                    sendReport("IN MOVEMENT");
                }
            }
        } else {
            Path path = tail.get(0);
            float timeToCamp = Hex.SIZE * path.getCost() / (speed * marchConfig.speedFactor()) * (currentSections - 1);

            camp(delta, timeToCamp);
        }
    }

    public abstract void rest(float f);


    public void move(float delta) {
        if (!playScreen.timer.isChecked()) {
            if(playScreen.isNight() && spirit.fatigue > 9.6f) {
                hasDayToRest = true;
            } else if (playScreen.isNight()) {
                hasDayToRest = false;
            }
            if (!playScreen.isNight() && !hasDayToRest && prepareToCamp() == 0) {
                normalMarch(delta);
            } else if (prepareToCamp() > 0) {
                camp(delta, prepareToCamp());
            }
//            else {
//                float f = delta * HOURS_IN_SECOND * MarchConfig.REST_FACTOR;
//                rest(f);
//            }
        }
    }

    public abstract float findSpeed();

    public abstract Spirit findSpirit();

    public abstract Stock changeStockDescending(Stock stock, int mode);

    public abstract Stock emptyStock();

    public Stock flattenEmptiedStock(Stock stock, int mode) {
        double foodRequest = strength.foodConsumption * NORMAL_FOOD_STOCK_DAYS;
        double ammoRequest = strength.ammoConsumption * mode;
        double ratio = strength.capacity / (ammoRequest + foodRequest);
        double foodToLoad = foodRequest * ratio;
        double ammoToLoad = ammoRequest * ratio;
        if (ammoToLoad > stock.ammo) {
            ammoToLoad = stock.ammo;
            foodToLoad = strength.capacity - ammoToLoad;
        }
        if (foodToLoad > stock.food) {
            foodToLoad = stock.food;
            if (ammoToLoad < stock.ammo) {
                if (stock.ammo > strength.capacity - foodToLoad) {
                    ammoToLoad = strength.capacity - foodToLoad;
                } else {
                    ammoToLoad = stock.ammo;
                }
            }
        }
        strength.food = foodToLoad;
        strength.ammo = ammoToLoad;
        Stock remaining = new Stock(stock.food - foodToLoad, stock.ammo - ammoToLoad);
        double foodRatio = foodToLoad / strength.capacity;
        double ammoRatio = ammoToLoad / strength.capacity;
        flatten(foodRatio, ammoRatio);
        return remaining;
    }

    public abstract void flatten(double foodRatio, double ammoRatio);

    public Stock absorbStock(Stock stock, int mode) {
        Stock distribution = emptyStock();
        distribution = distribution.plus(stock);
        return flattenEmptiedStock(distribution, mode);
    }

    public Array<WagonTrain> separateWagons() {
        Array<WagonTrain> train = new Array<>();
        addWagonToTrain(train);
        return train;
    }

    public abstract void addWagonToTrain(Array<WagonTrain> train);

    public abstract double findAmmoNeed();

    public abstract double findFoodNeed();

    public abstract int getLevel();

    public boolean detach() {
        if (superForce == null) {
            return false;
        }
        superForce.remove(this);
        superForce.changeStrength(strength.reverse());
        superForce = null;
        speed = findSpeed();
        spirit = findSpirit();


        return true;
    }

}
