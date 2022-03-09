package by.pavka.march.thread;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectFloatMap;
import com.badlogic.gdx.utils.ObjectMap;

import by.pavka.march.map.Hex;
import by.pavka.march.map.Path;
import by.pavka.march.military.Force;
import by.pavka.march.order.DetachOrder;
import by.pavka.march.order.JoinOrder;
import by.pavka.march.order.Order;

public class ReportThread extends Thread {
    private Force force;
    float delay;
    private Order order;

    public ReportThread(Force force) {
        this.force = force;
//        if (force.remoteHeadForce == null || force.playScreen.getHexGraph().areNeighbours(force.remoteHeadForce.hex, force.hex)) {
//            delay = 0;
//        } else {
//            delay = force.findCommandDistance();
//        }
        delay = force.findCommandDistance();
        setDaemon(true);
    }

    public ReportThread(Force force, long d) {
        this.force = force;
        if (force.remoteHeadForce == null || force.playScreen.getHexGraph().areNeighbours(force.remoteHeadForce.hex, force.hex)) {
            delay = 0;
        } else {
            delay = force.findCommandDistance();
        }
        delay += d;
        setDaemon(true);
    }

    public ReportThread(Force force, Order order) {
        this.force = force;
        this.order = order;
//        if (force.remoteHeadForce == null || force.playScreen.getHexGraph().areNeighbours(force.remoteHeadForce.hex, force.hex)) {
//            delay = 0;
//        } else {
//            delay = force.findCommandDistance();
//        }
        delay = force.findCommandDistance();

//        System.out.println("REPORT execution " + order + " with delay " + delay);
        setDaemon(true);
    }

    @Override
    public void run() {
        String d = "";
        if (order != null) {
            d += delay;
        }

        final Hex delayedHex = force.hex;

        final Array<Path> delayedTail = new Array<>(force.tail);
        final Array<Path> delayedPath = new Array<>(force.forcePath);

        final ObjectMap<Force, Hex> delayedEnemies = new ObjectMap<Force, Hex>(force.visualEnemies);
//        final ObjectSet<Hex> delayedArea = force.reconArea;
        final ObjectFloatMap<Hex> delayedMap = force.reconMap;
        final float time = force.playScreen.time;

        final Force copy = force.copyForce();

        final Order fulfilledOrder = order;
//        if (force.getName().equals("II.Art.Bttr.")) {
//            System.out.println("FulfilledOrder is " + fulfilledOrder + " thread " + hashCode());
//        }
        long beg = 0;
        try {
            while (beg < delay) {
                Thread.sleep(100);
                if (!force.playScreen.timer.isChecked()) {
                    beg += 100;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (time >= force.visualTime) {
            Gdx.app.postRunnable(new Runnable() {

                @Override
                public void run() {

//                    force.visualHex = delayedHex;
//                    force.visualTail = delayedTail;
//                    force.visualForcePath = delayedPath;
//                    force.setVisualHex(delayedHex);
//                    force.visualTime = time;
//                    force.playScreen.updateEnemies(delayedEnemies, delayedArea, force.visualTime);
//                    force.visualizeCopy(copy);
//                    if (fulfilledOrder != null) {
//                        System.out.println("Order not null");
//                        force.visualOrders.removeOrder(fulfilledOrder);
//                        if (fulfilledOrder instanceof JoinOrder) {
//                            System.out.println("Order Join Order");
//                            force.visualHex.removeActor(force);
//                            force.visualHex = null;
//                            force.hex = null;
//                            force.shapeRenderer = null;
//                            force.nation = null;
//                        } else {
//
//                        }
//                    if (force.getName().equals("II.Art.Bttr.")) {
//                        System.out.println("REPORTED EXECUTION " + fulfilledOrder);
//                    }

//                    }

                    if (!(fulfilledOrder instanceof JoinOrder)) {
//                        if (force.getName().equals("II.Art.Bttr.")) {
//                            System.out.println("Force " + force + " head " + force.remoteHeadForce + " order " + fulfilledOrder);
//                        }
                        force.visualTail = delayedTail;
                        force.visualForcePath = delayedPath;
                        force.setVisualHex(delayedHex);
                        force.visualTime = time;
//                        force.playScreen.updateEnemies(delayedEnemies, delayedArea, force.visualTime);
                        //Important scouting hexes!
                        force.playScreen.updateEnemies(delayedEnemies, delayedMap, force.visualTime);
                        //
                        force.visualizeCopy(copy);
                        force.visualOrders.removeOrder(fulfilledOrder);
                        if (fulfilledOrder instanceof DetachOrder) {
                            force.remoteHeadForce = force.playScreen.headForce;
                            force.shapeRenderer = force.playScreen.shapeRenderer;
                        }
                    } else {
                        force.visualHex.removeActor(force);
                        force.visualHex = null;
                        force.shapeRenderer = null;
//                        force.nation = null;
                        force.visualOrders.clear();
                        force.actualOrders.clear();
                        force.forcePath.clear();
                        force.remoteHeadForce = null;
                    }
                }
            });
        }
    }
}


