package by.pavka.march.configuration;

import static by.pavka.march.configuration.Nation.AUSTRIA;
import static by.pavka.march.configuration.Nation.FRANCE;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.BuonaparteGame;
import by.pavka.march.PlayScreen;
import by.pavka.march.characteristic.Spirit;
import by.pavka.march.characteristic.Strength;
import by.pavka.march.map.Hex;
import by.pavka.march.map.HexGraph;
import by.pavka.march.military.Force;
import by.pavka.march.military.Formation;
import by.pavka.march.military.HQType;
import by.pavka.march.military.Unit;
import by.pavka.march.military.UnitType;

public class Configurator {
    public static BuonaparteGame game;
    public PlayScreen playScreen;
    public Nation nation;
    private Campaign campaign;
    private HexGraph hexGraph;

    public Configurator(BuonaparteGame game, Nation nation, Campaign campaign) {
        this.game = game;
        this.nation = nation;
        this.campaign = campaign;
        game.nation = nation;
    }

//    public TiledMap getMap() {
//        return campaign.map;
//    }

    public static Drawable getImage(String name) {
        return game.getDrawable(name);
    }
    public Drawable getDrawable(String name) {
        return game.getDrawable(name);
    }

    public String getMapName() {
        return campaign.mapName;
    }

    public String getLayerName() {
        return campaign.layerName;
    }

    public Formation addForces(PlayScreen playScreen) {
//        Force testForce = new Unit(game.getTextureRegion("fr_cav"));
        Force testForce = new Unit(UnitType.CAVALRY);
        testForce.setName("X.Cav.Sq.");
//        Formation cavDivision = new Formation(game.getTextureRegion("fr_cav"));
        Formation cavDivision = new Formation(HQType.DIVISION_HQ);
        cavDivision.setName("IV.Cav.Div.");
//        Formation cavRegiment = new Formation(game.getTextureRegion("fr_cav"));
        Formation cavRegiment = new Formation(HQType.CAVALRY_REGIMENT_HQ);
        cavRegiment.setName("XI.Cav.Regmnt.");
        cavRegiment.attach(testForce, true);
//        Formation cavBrigade = new Formation(game.getTextureRegion("fr_cav"));
        Formation cavBrigade = new Formation(HQType.CAVALRY_BRIGADE_HQ);
        cavBrigade.setName("XII.Brigade");
        cavBrigade.attach(cavRegiment, true);
        cavDivision.attach(cavBrigade, true);
//        Force anotherTestForce = new Unit(game.getTextureRegion("fr_art"));
        Force anotherTestForce = new Unit(UnitType.ARTILLERY);
        anotherTestForce.setName("II.Art.Bttr.");
//        Formation headForce = new Formation(game.getTextureRegion("fr_mil"));
        Formation headForce = new Formation(HQType.DIVISION_HQ);
//        headForce.setDrawable(getDrawable(headForce.image()));
        headForce.setName("IV.Inf.Division.");
//        Force unit = new Unit(game.getTextureRegion("fr_inf"));
        Unit unit = new Unit(UnitType.INFANTRY);
//        unit.setDrawable(getDrawable(unit.image()));
        unit.setName("I.Inf.Bat.");
        headForce.attach(unit, true);
//        Formation formation = new Formation(game.getTextureRegion("fr_inf"));
        Formation formation = new Formation(HQType.INFANTRY_BRIGADE_HQ);
//        formation.setDrawable(getDrawable(formation.image()));
//        Formation formation1 = new Formation((game.getTextureRegion("fr_inf")));
        Formation formation1 = new Formation(HQType.INFANTRY_REGIMENT_HQ);
//        formation1.setDrawable(getDrawable(formation1.image()));
//        Unit un = new Unit(game.getTextureRegion("fr_inf"));
        Unit un = new Unit(UnitType.INFANTRY);
//        un.setDrawable(getDrawable(un.image()));
        formation.setName("I.Inf.Brigade");
        formation1.setName("III.Inf.Regmnt.");
        un.setName("III.Inf.Bat.");
        formation1.attach(un, true);
        formation.attach(formation1, true);
//        headForce.attach(formation, true);
        Formation form = new Formation(game.getTextureRegion("fr_art"));
        form.setName("Reserve Art.");
        form.remoteHeadForce = headForce;
        form.nation = FRANCE;
        cavDivision.remoteHeadForce = headForce;
        anotherTestForce.remoteHeadForce = headForce;
        cavDivision.nation = FRANCE;
        anotherTestForce.nation = FRANCE;
        headForce.nation = FRANCE;

//        Force enemy = new Unit(game.getTextureRegion("hostile"));
        Force enemy = new Unit(UnitType.INFANTRY);
        enemy.nation = AUSTRIA;
        enemy.setName("Enemy small");
        enemy.spirit.xp = 1.2;
        Formation enemyHQ = new Formation(game.getTextureRegion("hostile"));
        enemyHQ.nation = AUSTRIA;
        enemyHQ.setName("Enemy HQ");
        enemy.remoteHeadForce = enemyHQ;

        addForce(headForce, 4,6, playScreen);
//        headForce.sendReport("initial");
        addForce(cavDivision, 2,1, playScreen);
        addForce(anotherTestForce, 2,2, playScreen);
//        addForce(headForce, 4,6, playScreen);
        addForce(enemy, 9,9, playScreen);
        addForce(enemyHQ, 10, 10, playScreen);
        addForce(form, 8, 7, playScreen);

        addForce(AUSTRIA, 0, 1, playScreen);
        return headForce;
    }

    private static void setPlayScreenToChildren(Force force, PlayScreen playScreen) {
        if (force instanceof Formation && !((Formation)force).subForces.isEmpty()) {
            for (Force f : ((Formation)force).subForces) {
                f.playScreen = playScreen;
                f.nation = force.nation;
                setPlayScreenToChildren(f, playScreen);
            }
        }
    }

    public static void addForce(Force force, int col, int row, PlayScreen playScreen) {
        Hex hex = playScreen.getHexGraph().getHex(col, row);
        force.setVisualHex(hex);
        force.setRealHex(hex);
        force.shapeRenderer = playScreen.shapeRenderer;
        force.playScreen = playScreen;
        setPlayScreenToChildren(force, playScreen);
        force.sendReport("initial");
    }

    private void addForce(Nation nation, int col, int row, PlayScreen playScreen) {
        TextureRegion region = null;
        if (this.nation == nation) {
            region = game.getTextureRegion("fr_inf");
        } else {
            region = game.getTextureRegion("hostile");
        }
        Force force = new Unit(region);
        force.nation = nation;
        Hex hex = playScreen.getHexGraph().getHex(col, row);
        force.setVisualHex(hex);
        force.setRealHex(hex);
        force.shapeRenderer = playScreen.shapeRenderer;
        force.playScreen = playScreen;

        setInitialViewStructure(force);

        force.setTreeViewStructure();
    }

    public void setInitialViewStructure(Force force) {
        force.visualStrength = new Strength(force.strength);
        force.visualSpirit = new Spirit(force.spirit);

        if (force instanceof Formation) {
            Formation f = (Formation)force;
            f.visualForces = new Array<>();
            for (Force sub : f.subForces) {
                f.visualForces.add(sub);
                setInitialViewStructure(sub);
            }
        }
    }

//    public HexagonalTiledMapRenderer getHexagonalRenderer() {
//        return new HexagonalTiledMapRenderer(getMap());
//    }

//    public HexGraph getHexGraph(PlayScreen playScreen) {
//        if (hexGraph == null) {
//            hexGraph = new HexGraph(getMap(), playScreen);
//            Courier.hexGraph = hexGraph;
//        }
//        return hexGraph;
//    }
}
