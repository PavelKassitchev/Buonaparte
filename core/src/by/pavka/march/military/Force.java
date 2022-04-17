package by.pavka.march.military;

import static by.pavka.march.BuonaparteGame.HEX_SIZE_M;
import static by.pavka.march.BuonaparteGame.TILE_SIZE_PX;
import static by.pavka.march.PlayScreen.HOURS_IN_SECOND;
import static by.pavka.march.characteristic.Stock.NORMAL_FOOD_STOCK_DAYS;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectFloatMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

import by.pavka.march.PlayScreen;
import by.pavka.march.characteristic.March;
import by.pavka.march.characteristic.MarchConfig;
import by.pavka.march.characteristic.Spirit;
import by.pavka.march.characteristic.Stock;
import by.pavka.march.characteristic.Strength;
import by.pavka.march.configuration.Nation;
import by.pavka.march.fight.Fight;
import by.pavka.march.fight.impl.Battle;
import by.pavka.march.map.Direction;
import by.pavka.march.map.Hex;
import by.pavka.march.map.Path;
import by.pavka.march.order.Order;
import by.pavka.march.order.OrderList;
import by.pavka.march.thread.OrderThread;
import by.pavka.march.thread.ReportThread;
import by.pavka.march.view.ForceRep;

public abstract class Force extends Image {
    public static final String CAV = "cavalry";
    public static final String INF = "infantry";
    public static final String ENG = "engineer";
    public static final String ART = "artillery";
    public static final String SUP = "supply";
    public static final float MAX_SPEED = 36.0f;

//    public static final int DISORDERED = -1;
//    public static final int DEFAULT = 0;
//    public static final int IN_BATTLE = 1;

    public static final int TEST_SPEED = 2;
    public static final int TEST_REPORT_PERIOD = 1;
    public static final int TEST_REPORT_SPEED = 1;
    public static final int TEST_ORDER_SPEED = 5;

    public static final double MORALE_RESTORE_HOUR = 0.1;
    public static final double MIN_MORALE = -20;
    public static double REORDER_MORALE = 0.3;

    public Nation nation;
    String name;

    public Strength strength;
    public Spirit spirit;
    public Strength visualStrength;
    public Spirit visualSpirit;
    public Strength interStrength;
    public Spirit interSpirit;
    public Strength viewStrength;
    public Spirit viewSpirit;

    public float speed;
    //    public int state = DEFAULT;
    public boolean isDisordered;
    public Fight fight;
    public Formation superForce;
    public Formation remoteHeadForce;

    public Hex hex;
    public Hex visualHex;


    float start = 0;
    float report = 0;
    float camp = 0;
    public Array<Path> forcePath;
    public Array<Path> visualForcePath;
    public Array<Path> tail;
    public Array<Path> visualTail;
    public OrderList actualOrders;
    public OrderList visualOrders;
    //    public OrderList futureOrders;
    //    public ObjectIntMap<Force> visualEnemies;
    public ObjectMap<Force, Hex> visualEnemies;
    //    public ObjectSet<Hex> reconArea;
    public ObjectFloatMap<Hex> reconMap;
    public float visualTime;
    public int sections;
    public int currentSections;
    public int size;
    public ShapeRenderer shapeRenderer;
    public PlayScreen playScreen;
    public boolean showPath;
    MarchConfig marchConfig;
    public boolean hasDayToRest;

    public ForceRep forceRep;

    public Force() {
        unmark();

//        visualStrength = new Strength(strength);

        //sections = (int) len / 3500;
        currentSections = 1;
        tail = new Array<>();
        visualTail = new Array<>();
        forcePath = new Array<>();
        visualForcePath = new Array<>();
        actualOrders = new OrderList(this);
        visualOrders = new OrderList(this);
//        futureOrders = new OrderList(this);
//        visualEnemies = new ObjectIntMap<>();
        visualEnemies = new ObjectMap<>();
//        reconArea = new ObjectSet<>();
        reconMap = new ObjectFloatMap<>();

        spirit = new Spirit(0, 1, 0);

        visualSpirit = new Spirit(spirit);

//        viewStrength = new Strength(strength);
        viewSpirit = new Spirit(spirit);

        marchConfig = new MarchConfig(March.REGULAR);
    }

    public Force(TextureRegion region) {
        super(region);
    }

    public Force(TextureRegion region, int inf, float len) {
        super(region);
        unmark();
        strength = new Strength();
        strength.infantry = inf;
        strength.length = len;

        visualStrength = new Strength(strength);

        sections = (int) len / 3500;
        currentSections = 1;
        tail = new Array<>();
        visualTail = new Array<>();
        forcePath = new Array<>();
        visualForcePath = new Array<>();
        actualOrders = new OrderList(this);
        visualOrders = new OrderList(this);
//        futureOrders = new OrderList(this);
//        visualEnemies = new ObjectIntMap<>();
        visualEnemies = new ObjectMap<>();
//        reconArea = new ObjectSet<>();
        reconMap = new ObjectFloatMap<>();

        spirit = new Spirit(0, 1, 0);

        visualSpirit = new Spirit(spirit);

        viewStrength = new Strength(strength);
        viewSpirit = new Spirit(spirit);

        marchConfig = new MarchConfig(March.REGULAR);

    }

    public abstract String image();

    public void startFight(Hex hex, Direction direction) {
        fight = new Battle(hex, this.nation, direction);
        System.out.println("This fight is " + fight + " direction = " + direction);
    }

    public abstract void joinFight(Hex hex);

    public abstract boolean canAttach(Force force);

    public Force findHyperForce() {
        if (superForce == null) {
            return this;
        } else {
            return superForce.findHyperForce();
        }
    }

    public float findReportDistance(Force force) {
        float delay = 0;
        Hex h = hex == null ? findHyperForce().hex : hex;
        if (force != null && force.hex != h) {
            delay = (float) Courier.courierDelay(force.hex, h) * 1000 / HOURS_IN_SECOND;
        }
        return delay;
    }

    public float findCommandDistance() {

//        return findReportDistance(findHyperForce().remoteHeadForce);
        //TODO remove playScreen check
        if (playScreen != null) {
            return findReportDistance(playScreen.headForce);
        } else {
            return 0;
        }
    }


    public static void sendOrder(final Force force, final Order order) {
        Thread t = new OrderThread(force, order);
        t.start();
    }

    public static void sendOrder(final Force force, final Order order, long delay) {
        Thread t = new OrderThread(force, order, delay);
        t.start();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if ((nation == playScreen.game.nation && shapeRenderer != null) || playScreen.enemies.containsKey(this)) {
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

        double len = visualStrength.length;
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
        return nation != playScreen.game.nation;
    }

    public Array<Force> getAllies(Hex hex) {
        Array<Force> allies = new Array<>();
        if (hex.hasChildren()) {
            for (Actor a : hex.getChildren()) {
                Force f = (Force) a;
                if (this != f && f.nation == nation) {
                    allies.add(f);
                }
            }
        }
        return allies;
    }

    public Array<Formation> getAttachFormations(Hex hex) {
        Array<Formation> allies = new Array<>();
        if (hex.hasChildren()) {
            for (Actor a : hex.getChildren()) {
                Force f = (Force) a;
                if (this != f && f.nation == nation && f.canAttach(this)) {
                    allies.add((Formation) f);
                }
            }
        }
        return allies;
    }

    public Array<Force> getEnemies(Hex hex) {
        Array<Force> allies = new Array<>();
        if (hex.hasChildren()) {
            for (Actor a : hex.getChildren()) {
                Force f = (Force) a;
                if (f.nation != nation) {
                    allies.add(f);
                }
            }
        }
        return allies;
    }

    public void recon() {
        final ObjectMap<Force, Hex> enemies = new ObjectMap<>();
//        reconArea = getReconArea();
        reconMap = getReconMap();
//        for (Hex h : reconArea) {
        for (Hex h : reconMap.keys()) {
            if (h.enemiesOf(this) != null) {
                for (Force f : h.enemiesOf(this)) {
                    if (ForceRep.reconEnemy(f) != null) {
//                        f.setVisualHex(h);
                        f.interStrength = new Strength(f.strength);
                        f.interSpirit = new Spirit(f.spirit);
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

    public ObjectFloatMap<Hex> getReconMap() {
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
        ObjectFloatMap<Hex> scoutMap = new ObjectFloatMap<>();
        for (Hex h : reconArea) {
            scoutMap.put(h, h.crop);
        }
        return scoutMap;
    }

//    public ObjectSet<Hex> getReconArea() {
//        int radius;
//        if (strength.recon <= 6) {
//            radius = 1;
//        } else if (strength.recon <= 18) {
//            radius = 2;
//        } else if (strength.recon <= 36) {
//            radius = 3;
//        } else if (strength.recon <= 60) {
//            radius = 4;
//        } else {
//            radius = 5;
//        }
//        ObjectSet<Hex> reconArea = playScreen.getHexGraph().getArea(hex, radius, new ObjectSet<Hex>());
//        if (radius > 1) {
//            if (forcePath != null && forcePath.size > radius) {
//                Hex h = forcePath.get(radius).toHex;
//                reconArea.add(h);
//            }
//        }
//        return reconArea;
//    }


    public void sendReport(final String tag) {

        Thread t = new ReportThread(this);
        t.start();
    }

    public void sendReport(final String tag, long delay) {
        Thread t = new ReportThread(this, delay);
        t.start();
    }

    public void sendReport(Order order) {

    }

    private boolean readyToRecon(float delta) {
        if (hex == null) {
            return false;
        }
        report += delta;
        if (report > 3) {
//            System.out.println(getName() + " ready to recon");
            report = 0;
            return true;
        }
        return false;
    }

    @Override
    public void act(float delta) {
        //TODO remove playScreen == null after testing battle
        if (playScreen == null || !playScreen.timer.isChecked()) {
            super.act(delta);
            feed(delta);
            flatten();
            float f = delta * HOURS_IN_SECOND * MarchConfig.REST_FACTOR;
            rest(f);
            restoreMorale(delta);
            if (fight != null) {
                fight(delta);
                System.out.println("FIGHT!!!");
//                if (!isDisordered) {
//                    fight.affect(this, delta);
//                }
            } else {
                if (actualOrders.first() != null) {
                    actualOrders.first().execute(this, delta);
                }
                //TODO remove playScreen == null after testing
                if (playScreen != null) {
                    if (!isEnemy() && readyToRecon(delta)) {
                        recon();
                        sendReport("JUST RECON");
                    }
                }
            }

        }

    }

    public void fight(float delta) {
        fight.strike(this, delta);
    }

    public void feed(float delta) {
        double f = strength.foodConsumption * delta * HOURS_IN_SECOND / 24;
        if (hex != null) {
            if (hex.crop > f) {
                hex.crop -= f;
            } else {
                eat(delta);
            }
        }
    }

    public void camp(float delta, float duration) {
        fatigue(delta * HOURS_IN_SECOND * marchConfig.fatigueFactor());
        if (currentSections > 1) {
            camp += delta;
            if (camp > duration / HOURS_IN_SECOND) {
                camp = 0;
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
            Direction direction = path.direction;
            float timeToCross = Hex.SIZE * path.getCost() / (speed * marchConfig.speedFactor());
            if (start > timeToCross / HOURS_IN_SECOND) {
//                if (getName().equals("II.Art.Bttr.") || getName().equals("IV.Cav.Div.")) {
//                    System.out.println(getName() + "time to cross " + timeToCross * 2 + " speed " + speed * marchConfig.speedFactor()
//                    + " current time " + start + " fatigue " + spirit.fatigue + " speed factor " + marchConfig.speedFactor());
//                }
                start = 0;
                forcePath.removeIndex(0);
                Hex toHex = path.getToNode();
                setRealHex(toHex);

                if (playScreen.selectedForces.contains(this, true)
                        && playScreen.destinations.contains(toHex, true)) {
                    playScreen.destinations.removeValue(toHex, true);
                    actualOrders.removeHexFromDestinations(toHex);
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
                if (!hex.enemiesOf(this).isEmpty()) {
                    if (hex.hasBattle) {
                        joinFight(hex);
                    } else {
                        System.out.println("START FIGHT!!!!");
                        startFight(hex, direction);
                    }
                }
            }
        } else {
            Path path = tail.get(0);
            float timeToCamp = Hex.SIZE * path.getCost() / (speed * marchConfig.speedFactor()) * (currentSections - 1);

            camp(delta, timeToCamp);
        }
    }

    public void setMarch(March march) {
        marchConfig.setMarch(march);
    }

    public abstract void rest(float f);

    public abstract void restoreMorale(float delta);

    public abstract void prepareForFight();

    public abstract Strength desert();

//    public void resumeFight() {
//        if (fight != null) {
//            fight.restoreForce(this);
//        }
//    }


    public void move(float delta) {
        if (!playScreen.timer.isChecked()) {
            if (playScreen.isNight() && spirit.fatigue > 9.6f) {
                hasDayToRest = true;
            } else if (playScreen.isNight()) {
                hasDayToRest = false;
            }
            if (!playScreen.isNight() && !hasDayToRest && prepareToCamp() == 0) {
                normalMarch(delta);
            } else if (prepareToCamp() > 0) {
                camp(delta, prepareToCamp());
            }
        }
    }

    public abstract float findSpeed();

    public abstract Spirit findSpirit();

    public abstract void eat(float delta);

    public abstract Stock changeStockDescending(Stock stock, int mode);

    public abstract Strength sufferLosses(double factor);

    public abstract Strength getFired(double factor);

    public abstract Strength getFired(double factor, boolean onPursuit);

    public abstract void changeSpirit(double xp, double morale, double fatigue);

    public abstract void getCharged(double morale);

    public void changeSpiritAscending(int soldiers, double xp, double morale, double fatigue) {
        if (superForce != null) {
            int superSoldiers = superForce.strength.soldiers();
            double ratio = soldiers / (superSoldiers + 1.0);
            superForce.spirit.change(xp * ratio, morale * ratio, fatigue * ratio);
            if (superForce.spirit.morale < 0) {
                superForce.isDisordered = true;
            }
            if (superForce.spirit.morale > 0.3) {
                superForce.isDisordered = false;
//                superForce.resumeFight();
            }
            superForce.changeSpiritAscending(superSoldiers, xp * ratio, morale * ratio, fatigue * ratio);
        }
    }

    public void changeStrengthAscending(Strength strth) {
        if (superForce != null) {
            superForce.strength.change(strth);
            superForce.changeStrengthAscending(strth);
        }
    }

    public void reduceFoodAscending(double food) {
        strength.food -= food;
        if (superForce != null) {
            superForce.reduceFoodAscending(food);
        }
    }

    public abstract Stock emptyStock();

    public void flatten() {
        Stock stock = emptyStock();
//        if (getName() != null && getName().equals("IV.Cav.Div.")) {
//            System.out.println("Emptied Stock is " + stock.food + " capacity is " + strength.capacity);
//        }
        double fRatio = stock.food / strength.capacity;
        double aRatio = stock.ammo / strength.capacity;

        flattenClearAll(fRatio, aRatio);
    }

    public abstract void flattenClearAll(double fRatio, double aRatio);

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

    public abstract void copyStructure();

    public abstract Force copyForce();

    public abstract void visualizeStructure();

    public abstract void visualizeCopy(Force force);

    public abstract void setTreeViewStructure();

    public abstract void addWagonToTrain(Array<WagonTrain> train);

    public abstract double findAmmoNeed();

    public abstract double findFoodNeed();

    public abstract Unitable getType();

//    public abstract int getLevel();

    public void resign() {
        if (superForce != null) {

        }
    }

    public boolean isDetachable() {
        return true;
    }

    public boolean detach() {
        if (superForce == null) {
            return false;
        }
        superForce.remove(this);
        superForce.changeStrength(strength.reverse());
        superForce.viewForces.removeValue(this, true);
        spirit = findSpirit();
        marchConfig.setMarch(March.REGULAR);

        return true;
    }

    public boolean detach(boolean physical, boolean needsReport) {
        if (!physical) {
            return detach();
        } else {
            if (superForce == null) {
                return false;
            }
            actualOrders.clear();
            visualOrders.clear();
            setRealHex(findHyperForce().hex);
            nation = findHyperForce().nation;
            detach();
            speed = findSpeed();
            findHyperForce().speed = findHyperForce().findSpeed();
            System.out.println("Hyper Force size is " + ((Formation) findHyperForce()).subForces.size + " speed "
                    + findHyperForce().speed);
            findHyperForce().sendReport("detached");
//            spirit = findSpirit();
            if (needsReport) {
                sendReport("detached");
            }
//            superForce.speed = superForce.findSpeed();
//            System.out.println("Super Force speed = " + superForce.speed);
            superForce = null;
            return true;
        }
    }

    public abstract String detailedInfo();

    public void retreat(Direction retreatDirection) {
        actualOrders.clear();
        //TODO remove check playScreen != null
        if (playScreen != null) {
            setRealHex(playScreen.hexGraph.getNeighbour(hex, retreatDirection));
        }
    }
}
