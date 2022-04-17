package by.pavka.march;

import static by.pavka.march.configuration.Nation.AUSTRIA;
import static by.pavka.march.configuration.Nation.FRANCE;

import com.badlogic.gdx.scenes.scene2d.Stage;

import by.pavka.march.map.Hex;
import by.pavka.march.military.Formation;
import by.pavka.march.military.HQType;
import by.pavka.march.military.Unit;
import by.pavka.march.military.UnitType;

public class TestConfigurator {
    public static void prepareStage(Stage stage, Hex hex) {
        Unit unit = new Unit(UnitType.INFANTRY);
//        unit.changeStrength(-540);
        Unit unit1 = new Unit(UnitType.ARTILLERY);
        unit1.nation = FRANCE;
        unit1.setName("French Artillery");
//        unit.spirit.morale = 0.25;
        unit.nation = FRANCE;
        unit.setName("French Infantry");
        Formation regiment1 = new Formation(HQType.INFANTRY_BRIGADE_HQ);
        regiment1.nation = FRANCE;
        regiment1.setName("French HQ");
        regiment1.attach(unit, true);
//        regiment1.attach(unit1, true);
//        hex.addForce(unit);
//        stage.addActor(unit);
//        hex.addForce(regiment1);
//        stage.addActor(regiment1);

        Unit enemyUnit = new Unit(UnitType.CAVALRY);
        enemyUnit.nation = AUSTRIA;
        enemyUnit.setName("Austrian Cavalry");
        Unit enemUnit = new Unit(UnitType.CAVALRY);
        enemUnit.nation = AUSTRIA;
        enemUnit.setName("Austrian Cav");
        Unit enemUnit1 = new Unit(UnitType.CAVALRY);
        enemUnit1.nation = AUSTRIA;
        enemUnit1.setName("Austrian Cav1");
        Unit enemUnit2 = new Unit(UnitType.CAVALRY);
        enemUnit2.nation = AUSTRIA;
        enemUnit2.setName("Austrian Cav2");
        Unit austrArtillery = new Unit(UnitType.ARTILLERY);
        austrArtillery.nation = AUSTRIA;
        austrArtillery.setName("Austrian Art");
//        enemyUnit.spirit.morale = 0.1;
        Formation regiment = new Formation(HQType.CAVALRY_REGIMENT_HQ);
        regiment.setName("Austrian Cav.Regiment HQ");
        regiment.nation = AUSTRIA;
        regiment.attach(enemyUnit, true);
//        regiment.attach(enemUnit, true);
//        regiment.attach(enemUnit1, true);
        Formation brigade = new Formation(HQType.INFANTRY_BRIGADE_HQ);
        brigade.setName("Austrian Brigade HQ");
        brigade.nation = AUSTRIA;
        brigade.attach(regiment, true);
//        brigade.attach(enemUnit2, true);
//        brigade.attach(austrArtillery, true);
//        hex.addForce(brigade);
//        stage.addActor(brigade);

//        hex.addForce(regiment);
//        stage.addActor(regiment);
//        hex.addForce(regiment1);
//        stage.addActor(regiment1);

//        stage.addActor(unit);

        // Infantry vs Cavalry

        Unit frenchInfantry = new Unit(UnitType.INFANTRY);
        frenchInfantry.nation = FRANCE;
        frenchInfantry.setName("French Inf");

        Unit ausCavalry = new Unit(UnitType.CAVALRY);
        ausCavalry.nation = AUSTRIA;
        ausCavalry.setName("Austrian Cav");

        Unit ausCavalry2 = new Unit(UnitType.CAVALRY);
        ausCavalry2.nation = AUSTRIA;
        ausCavalry2.setName("Austrian Cav2");

        Unit ausCavalry3 = new Unit(UnitType.CAVALRY);
        ausCavalry3.nation = AUSTRIA;
        ausCavalry3.setName("Austrian Cav3");

        Formation reg = new Formation(HQType.CAVALRY_REGIMENT_HQ);

        reg.nation = AUSTRIA;
        reg.setName("Austrian Reg");
        reg.attach(ausCavalry, true);
        reg.attach(ausCavalry2, true);
        reg.attach(ausCavalry3, true);

//        hex.addForce(frenchInfantry);
//        stage.addActor(frenchInfantry);
////        hex.addForce(ausCavalry);
////        stage.addActor(ausCavalry);
//        hex.addForce(reg);
//        stage.addActor(reg);

      // TWO BATTALIONS

        Unit finf = new Unit(UnitType.INFANTRY);
        finf.nation = FRANCE;
        finf.setName("French Inf");

        Unit ainf = new Unit(UnitType.INFANTRY);
        ainf.nation = AUSTRIA;
        ainf.setName("Austrian Inf");

//        hex.addForce(finf);
//        stage.addActor(finf);
//        hex.addForce(ainf);
//        stage.addActor(ainf);

// TWO DIVISIONS


        Unit fu1 = new Unit(UnitType.INFANTRY);
        fu1.setName("FInfantry1");
        fu1.nation = FRANCE;
        Unit fu2 = new Unit(UnitType.INFANTRY);
        fu2.setName("FInfantry2");
        fu2.nation = FRANCE;
        Unit fu3 = new Unit(UnitType.INFANTRY);
        fu3.setName("FInfantry3");
        fu3.nation = FRANCE;
        Unit fu4 = new Unit(UnitType.INFANTRY);
        fu4.setName("FInfantry4");
        fu4.nation = FRANCE;
        Unit fu5 = new Unit(UnitType.INFANTRY);
        fu5.setName("FInfantry5");
        fu5.nation = FRANCE;
        Unit fu6 = new Unit(UnitType.INFANTRY);
        fu6.setName("FInfantry6");
        fu6.nation = FRANCE;
        Unit fu7 = new Unit(UnitType.INFANTRY);
        fu7.setName("FInfantry7");
        fu7.nation = FRANCE;
        Unit fu8 = new Unit(UnitType.INFANTRY);
        fu8.setName("FInfantry8");
        fu8.nation = FRANCE;
        Unit fu9 = new Unit(UnitType.INFANTRY);
        fu9.setName("FInfantry9");
        fu9.nation = FRANCE;
        Unit fu10 = new Unit(UnitType.INFANTRY);
        fu10.setName("FInfantry10");
        fu10.nation = FRANCE;
        Unit fu11 = new Unit(UnitType.INFANTRY);
        fu11.setName("FInfantry11");
        fu11.nation = FRANCE;
        Unit fu12 = new Unit(UnitType.INFANTRY);
        fu12.setName("FInfantry12");
        fu12.nation = FRANCE;
        Unit fu13 = new Unit(UnitType.INFANTRY);
        fu13.setName("FInfantry13");
        fu13.nation = FRANCE;
        Unit fu14 = new Unit(UnitType.INFANTRY);
        fu14.setName("FInfantry14");
        fu14.nation = FRANCE;
        Unit fu15 = new Unit(UnitType.INFANTRY);
        fu15.setName("FInfantry15");
        fu15.nation = FRANCE;
        Unit fu16 = new Unit(UnitType.INFANTRY);
        fu16.setName("FInfantry16");
        fu16.nation = FRANCE;
        Unit fcu1 = new Unit(UnitType.CAVALRY);
        fcu1.setName("FCavalry1");
        fcu1.nation = FRANCE;
        Unit fcu2 = new Unit(UnitType.CAVALRY);
        fcu2.setName("FCavalry2");
        fcu2.nation = FRANCE;
        Unit fcu3 = new Unit(UnitType.CAVALRY);
        fcu3.setName("FCavalry3");
        fcu3.nation = FRANCE;
        Unit fcu4 = new Unit(UnitType.CAVALRY);
        fcu4.setName("FCavalry4");
        fcu4.nation = FRANCE;
        Unit fcu5 = new Unit(UnitType.CAVALRY);
        fcu5.setName("FCavalry5");
        fcu5.nation = FRANCE;
        Unit fcu6 = new Unit(UnitType.CAVALRY);
        fcu6.setName("FCavalry6");
        fcu6.nation = FRANCE;
        Unit fau1 = new Unit(UnitType.ARTILLERY);
        fau1.setName("FArt1");
        fau1.nation = FRANCE;
        Unit fau2 = new Unit(UnitType.ARTILLERY);
        fau2.setName("FArt2");
        fau2.nation = FRANCE;
        Unit fau3 = new Unit(UnitType.ARTILLERY);
        fau3.setName("FArt3");
        fau3.nation = FRANCE;
        Unit fau4 = new Unit(UnitType.ARTILLERY);
        fau4.setName("FArt4");
        fau4.nation = FRANCE;

        Formation freg1 = new Formation(HQType.INFANTRY_REGIMENT_HQ);
        freg1.setName("FReg1");
        freg1.nation = FRANCE;
        Formation freg2 = new Formation(HQType.INFANTRY_REGIMENT_HQ);
        freg2.setName("FReg2");
        freg2.nation = FRANCE;
        Formation freg3 = new Formation(HQType.INFANTRY_REGIMENT_HQ);
        freg3.setName("FReg3");
        freg3.nation = FRANCE;
        Formation freg4 = new Formation(HQType.INFANTRY_REGIMENT_HQ);
        freg4.setName("FReg4");
        freg4.nation = FRANCE;

        Formation fcav = new Formation(HQType.CAVALRY_REGIMENT_HQ);
        fcav.setName("FCav");
        fcav.nation = FRANCE;

        Formation fbrig1 = new Formation(HQType.INFANTRY_BRIGADE_HQ);
        fbrig1.setName("FBrigade1");
        fbrig1.nation = FRANCE;
        Formation fbrig2 = new Formation(HQType.INFANTRY_BRIGADE_HQ);
        fbrig2.setName("FBrigade2");
        fbrig2.nation = FRANCE;

        Formation fdiv = new Formation(HQType.DIVISION_HQ);
        fdiv.setName("FDiv");
        fdiv.nation = FRANCE;

        freg1.attach(fu1, true);
        freg1.attach(fu2, true);
        freg1.attach(fu3, true);
        freg1.attach(fu4, true);

        freg2.attach(fu5, true);
        freg2.attach(fu6, true);
        freg2.attach(fu7, true);
        freg2.attach(fu8, true);

        freg3.attach(fu9, true);
        freg3.attach(fu10, true);
        freg3.attach(fu11, true);
        freg3.attach(fu12, true);

        freg4.attach(fu13, true);
        freg4.attach(fu14, true);
        freg4.attach(fu15, true);
        freg4.attach(fu16, true);

        fbrig1.attach(freg1, true);
        fbrig1.attach(freg2, true);
        fbrig1.attach(fcu1, true);
        fbrig1.attach(fau1, true);

        fbrig2.attach(freg3, true);
        fbrig2.attach(freg4, true);
        fbrig2.attach(fcu2, true);
        fbrig2.attach(fau2, true);

        fcav.attach(fcu3, true);
        fcav.attach(fcu4, true);
        fcav.attach(fcu5, true);
        fcav.attach(fcu6, true);

        fdiv.attach(fbrig1, true);
        fdiv.attach(fbrig2, true);
        fdiv.attach(fau3, true);
        fdiv.attach(fau4, true);
        fdiv.attach(fcav, true);

//        hex.addForce(fdiv);
//        stage.addActor(fdiv);


        Unit au1 = new Unit(UnitType.INFANTRY);
        au1.setName("aInfantry1");
        au1.nation = AUSTRIA;
        Unit au2 = new Unit(UnitType.INFANTRY);
        au2.setName("aInfantry2");
        au2.nation = AUSTRIA;
        Unit au3 = new Unit(UnitType.INFANTRY);
        au3.setName("aInfantry3");
        au3.nation = AUSTRIA;
        Unit au4 = new Unit(UnitType.INFANTRY);
        au4.setName("aInfantry4");
        au4.nation = AUSTRIA;
        Unit au5 = new Unit(UnitType.INFANTRY);
        au5.setName("aInfantry5");
        au5.nation = AUSTRIA;
        Unit au6 = new Unit(UnitType.INFANTRY);
        au6.setName("aInfantry6");
        au6.nation = AUSTRIA;
        Unit au7 = new Unit(UnitType.INFANTRY);
        au7.setName("aInfantry7");
        au7.nation = AUSTRIA;
        Unit au8 = new Unit(UnitType.INFANTRY);
        au8.setName("aInfantry8");
        au8.nation = AUSTRIA;
        Unit au9 = new Unit(UnitType.INFANTRY);
        au9.setName("aInfantry9");
        au9.nation = AUSTRIA;
        Unit au10 = new Unit(UnitType.INFANTRY);
        au10.setName("aInfantry10");
        au10.nation = AUSTRIA;
        Unit au11 = new Unit(UnitType.INFANTRY);
        au11.setName("aInfantry11");
        au11.nation = AUSTRIA;
        Unit au12 = new Unit(UnitType.INFANTRY);
        au12.setName("aInfantry12");
        au12.nation = AUSTRIA;
        Unit au13 = new Unit(UnitType.INFANTRY);
        au13.setName("aInfantry13");
        au13.nation = AUSTRIA;
        Unit au14 = new Unit(UnitType.INFANTRY);
        au14.setName("aInfantry14");
        au14.nation = AUSTRIA;
        Unit au15 = new Unit(UnitType.INFANTRY);
        au15.setName("aInfantry15");
        au15.nation = AUSTRIA;
        Unit au16 = new Unit(UnitType.INFANTRY);
        au16.setName("aInfantry16");
        au16.nation = AUSTRIA;
        Unit acu1 = new Unit(UnitType.CAVALRY);
        acu1.setName("aCavalry1");
        acu1.nation = AUSTRIA;
        Unit acu2 = new Unit(UnitType.CAVALRY);
        acu2.setName("aCavalry2");
        acu2.nation = AUSTRIA;
        Unit acu3 = new Unit(UnitType.CAVALRY);
        acu3.setName("aCavalry3");
        acu3.nation = AUSTRIA;
        Unit acu4 = new Unit(UnitType.CAVALRY);
        acu4.setName("aCavalry4");
        acu4.nation = AUSTRIA;
        Unit acu5 = new Unit(UnitType.CAVALRY);
        acu5.setName("aCavalry5");
        acu5.nation = AUSTRIA;
        Unit acu6 = new Unit(UnitType.CAVALRY);
        acu6.setName("aCavalry6");
        acu6.nation = AUSTRIA;
        Unit aau1 = new Unit(UnitType.ARTILLERY);
        aau1.setName("aArt1");
        aau1.nation = AUSTRIA;
        Unit aau2 = new Unit(UnitType.ARTILLERY);
        aau2.setName("aArt2");
        aau2.nation = AUSTRIA;
        Unit aau3 = new Unit(UnitType.ARTILLERY);
        aau3.setName("aArt3");
        aau3.nation = AUSTRIA;
        Unit aau4 = new Unit(UnitType.ARTILLERY);
        aau4.setName("aArt4");
        aau4.nation = AUSTRIA;

        Formation areg1 = new Formation(HQType.INFANTRY_REGIMENT_HQ);
        areg1.setName("aReg1");
        areg1.nation = AUSTRIA;
        Formation areg2 = new Formation(HQType.INFANTRY_REGIMENT_HQ);
        areg2.setName("aReg2");
        areg2.nation = AUSTRIA;
        Formation areg3 = new Formation(HQType.INFANTRY_REGIMENT_HQ);
        areg3.setName("aReg3");
        areg3.nation = AUSTRIA;
        Formation areg4 = new Formation(HQType.INFANTRY_REGIMENT_HQ);
        areg4.setName("aReg4");
        areg4.nation = AUSTRIA;

        Formation acav = new Formation(HQType.CAVALRY_REGIMENT_HQ);
        acav.setName("aCav");
        acav.nation = AUSTRIA;

        Formation abrig1 = new Formation(HQType.INFANTRY_BRIGADE_HQ);
        abrig1.setName("aBrigade1");
        abrig1.nation = AUSTRIA;
        Formation abrig2 = new Formation(HQType.INFANTRY_BRIGADE_HQ);
        abrig2.setName("aBrigade2");
        abrig2.nation = AUSTRIA;

        Formation adiv = new Formation(HQType.DIVISION_HQ);
        adiv.setName("aDiv");
        adiv.nation = AUSTRIA;

        areg1.attach(au1, true);
        areg1.attach(au2, true);
        areg1.attach(au3, true);
        areg1.attach(au4, true);

        areg2.attach(au5, true);
        areg2.attach(au6, true);
        areg2.attach(au7, true);
        areg2.attach(au8, true);

        areg3.attach(au9, true);
        areg3.attach(au10, true);
        areg3.attach(au11, true);
        areg3.attach(au12, true);

        areg4.attach(au13, true);
        areg4.attach(au14, true);
        areg4.attach(au15, true);
        areg4.attach(au16, true);

        abrig1.attach(areg1, true);
        abrig1.attach(areg2, true);
        abrig1.attach(acu1, true);
        abrig1.attach(aau1, true);

        abrig2.attach(areg3, true);
        abrig2.attach(areg4, true);
        abrig2.attach(acu2, true);
        abrig2.attach(aau2, true);

        acav.attach(acu3, true);
        acav.attach(acu4, true);
        acav.attach(acu5, true);
        acav.attach(acu6, true);

        adiv.attach(abrig1, true);
        adiv.attach(abrig2, true);
        adiv.attach(aau3, true);
        adiv.attach(aau4, true);
        adiv.attach(acav, true);

        hex.addForce(fdiv);
        stage.addActor(fdiv);
        hex.addForce(adiv);
        stage.addActor(adiv);


    }
}
