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

    public static final int TEST_SPEED = 3;
    public static final int TEST_REPORT_PERIOD = 1;
    public static final int TEST_REPORT_SPEED = 3;
    public static final int TEST_ORDER_SPEED = 5;

    Nation nation;
    String name;

    public Strength strength;
    Spirit spirit;

    double speed;

    Formation superForce;
    Formation remoteHeadForce;

    TextureRegion icon;
    public Hex hex;
    public Hex visualHex;


    float start = 0;
    float report = 0;
    public Array<Path> forcePath;
    public Array<Path> visualForcePath;
    public Array<Path> tail;
    public Array<Path> visualTail;
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
        setAlpha(0.7f);
        strength = new Strength();
        strength.infantry = inf;
        strength.length = len;

        sections = (int) len / 3500;
        currentSections = 1;
        tail = new Array<>();
        visualTail = new Array<>();
        forcePath = new Array<>();
        visualForcePath = new Array<>();

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("FORCE!");
            }
        });
    }

    public static void sendMoveOrder(final Force force, final Hex destination) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(TEST_ORDER_SPEED * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        GraphPath<Hex> fPath = force.playScreen.getHexGraph().findPath(force.hex, destination);
                        Array<Path> fPaths = new Array<>();
                        Hex st = null;
                        Hex en;
                        for (Hex h : fPath) {
                            en = h;
                            if (st != null) {
                                fPaths.add(force.playScreen.getHexGraph().getPath(st, en));
                            }
                            st = h;
                        }
                        force.forcePath = fPaths;
                    }
                });
            }
        }).start();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
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

    public void setRealHex(Hex hex) {
        this.hex = hex;
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


    public void setAlpha(float a) {
        setColor(getColor().b, getColor().g, getColor().r, a);
    }

    private void sendReport(float delta) {
        report += delta;
        if (report > TEST_REPORT_PERIOD) {
            report = 0;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    final Hex delayedHex = hex;
                    final Array<Path> delayedTail = new Array<>(tail);
                    final Array<Path> delayedPath = new Array<>(forcePath);
                    try {
                        Thread.sleep(TEST_REPORT_SPEED * 1000);

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
                        }
                    });
                }
            }).start();
        }
    }

    public void receiveMoveOrder(Array<Path> paths) {

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (forcePath != null && !forcePath.isEmpty()) {
            move(delta);
        }
        sendReport(delta);
    }


    public void move(float delta) {
        start += delta;
        if (start > TEST_SPEED) {
            start = 0;
            Path path = forcePath.removeIndex(0);
            Hex toHex = path.getToNode();
            setRealHex(toHex);

            if (tail.contains(path.reverse(), false)) {
                System.out.println("CUTTING!");
                tail.pop();
                if (currentSections > 1) {
                    currentSections--;
                }
            } else {
                System.out.println("ADDING!");
                tail.add(path);
                currentSections++;
                if (currentSections > sections) {
                    currentSections--;
                    tail.removeIndex(0);
                }
            }
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
