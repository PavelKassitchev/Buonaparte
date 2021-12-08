package by.pavka.march.military;

import static by.pavka.march.BuonaparteGame.HEX_SIZE_M;
import static by.pavka.march.BuonaparteGame.TILE_SIZE_PX;
import static by.pavka.march.characteristic.Stock.NORMAL_FOOD_STOCK_DAYS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectSet;

import by.pavka.march.PlayScreen;
import by.pavka.march.characteristic.Spirit;
import by.pavka.march.characteristic.Stock;
import by.pavka.march.characteristic.Strength;
import by.pavka.march.configuration.Nation;
import by.pavka.march.map.Hex;
import by.pavka.march.map.Path;

public abstract class Force extends Image {
    public static final String CAV = "cavalry";
    public static final String INF = "infantry";
    public static final String ENG = "engineer";
    public static final String ART = "artillery";
    public static final String SUP = "supply";
    public static final double MAX_SPEED = 36.0;

    public static final int TEST_SPEED = 2;
    public static final int TEST_REPORT_PERIOD = 1;
    public static final int TEST_REPORT_SPEED = 1;
    public static final int TEST_ORDER_SPEED = 5;

    public Nation nation;
    String name;

    public Strength strength;
    Spirit spirit;

    double speed;

    public Formation superForce;
    public Formation remoteHeadForce;

    TextureRegion icon;
    public Hex hex;
    public Hex visualHex;


    float start = 0;
    float report = 0;
    public Array<Path> forcePath;
    public Array<Path> visualForcePath;
    public Array<Path> tail;
    public Array<Path> visualTail;
    public ObjectIntMap<Force> visualEnemies;
    public int visualTime;
    public int sections;
    public int currentSections;
    public int size;
    public ShapeRenderer shapeRenderer;
    public PlayScreen playScreen;
    public boolean showPath;

    TextureRegion reg = new TextureRegion(new Texture("unit/cav1.png"));

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
        visualEnemies = new ObjectIntMap<>();

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("FORCE!");
            }
        });
    }

    public int findCommandDistance() {
        int delay = 0;
        if (remoteHeadForce != null) {
//            GraphPath<Hex> hexPath = playScreen.getHexGraph().findPath(remoteHeadForce.hex, hex);
//            delay = hexPath.getCount() > 1 ? hexPath.getCount() - 1 : 0;
            delay = (int)(Courier.courierDelay(remoteHeadForce.hex, hex) * 1000);
        }
        return delay;
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
                    Thread.sleep(force.findCommandDistance());

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
        if (nation == PlayScreen.nation || playScreen.enemies.containsKey(this)) {
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

    public void setHex(Hex hex) {
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


    public void mark() {
        setColor(getColor().b, getColor().g, getColor().r, 1);
        showPath = true;
    }

    public void unmark() {
        setColor(getColor().b, getColor().g, getColor().r, 0.75f);
        showPath = false;
    }

    public boolean isEnemy() {
        return nation != PlayScreen.nation;
    }

    public void recon() {
        for (Hex h : getReconArea()) {
//            if (h.getForces().size > 0) {
//                if (h.getForces().get(0) == this) {
//                    System.out.println("My Force!");
//                } else {
//                    System.out.println("ENEMY FOUND!");
//                    playScreen.enemies.put(h.getForces().get(0), 0);
//                }
//            }
            if (h.enemies() != null) {
                for (Force f : h.enemies()) {
                    if (ReconData.reconEnemy(f) != null) {
                        final int delay = (int)(Courier.courierDelay(h, hex) * 1000);
                        final Force force = f;
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                final int time = playScreen.time;
                                try {
                                    Thread.sleep(delay);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Gdx.app.postRunnable(new Runnable() {

                                    @Override
                                    public void run() {
                                        visualEnemies.put(force, time);
                                    }
                                });
                            }
                        }).start();

                    }
                }
            }
        }
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

    private void sendReport(float delta) {
        //final int delay = findCommandDistance() > 1 ? findCommandDistance() - 1 : 0;
        if (findCommandDistance() > 1) {
            report += delta;
            if (report > TEST_REPORT_PERIOD) {
                report = 0;
                sendReport();
            }
        }
    }

    private void sendReport() {
        final int delay = findCommandDistance() > 1 ? findCommandDistance() - 1 : 0;
        new Thread(new Runnable() {

            @Override
            public void run() {
                final Hex delayedHex = hex;
                final Array<Path> delayedTail = new Array<>(tail);
                final Array<Path> delayedPath = new Array<>(forcePath);
                final ObjectIntMap<Force> delayedEnemies = new ObjectIntMap<>(visualEnemies);
                final int time = playScreen.time;
                try {
                        //Thread.sleep(TEST_REPORT_SPEED * 1000);
                    //Thread.sleep(delay * 1000);
                    Thread.sleep(delay);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Gdx.app.postRunnable(new Runnable() {

                    @Override
                    public void run() {
                        visualHex = delayedHex;
                        visualTail = delayedTail;
                        visualForcePath = delayedPath;
                        setHex(visualHex);
                        playScreen.enemies.putAll(delayedEnemies);
                        visualTime = time;
                    }
                });
            }
        }).start();

    }

    public void receiveMoveOrder(Array<Path> paths) {

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (forcePath != null && !forcePath.isEmpty()) {
            move(delta);
        }
        //sendReport(delta);
    }


    public void move(float delta) {
        start += delta;
        if (start > TEST_SPEED) {
            start = 0;
            Path path = forcePath.removeIndex(0);
            Hex toHex = path.getToNode();
            setRealHex(toHex);

            if (playScreen.selectedForce == this && playScreen.destinations.contains(toHex, true)) {
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
            recon();
            sendReport();
        }
    }

    public abstract double findSpeed();

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
