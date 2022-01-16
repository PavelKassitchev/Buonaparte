package by.pavka.march.thread;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

import by.pavka.march.map.Hex;
import by.pavka.march.map.Path;
import by.pavka.march.military.Force;

public class ReportThread extends Thread {
    private Force force;
    float delay;

    public ReportThread(Force force) {
        this.force = force;
        if (force.remoteHeadForce == null || force.playScreen.getHexGraph().areNeighbours(force.remoteHeadForce.hex, force.hex)) {
            delay = 0;
        } else {
            delay = force.findCommandDistance();
        }
        setDaemon(true);
    }

    @Override
    public void run() {
        final Hex delayedHex = force.hex;

        final Array<Path> delayedTail = new Array<>(force.tail);
        final Array<Path> delayedPath = new Array<>(force.forcePath);

        final ObjectMap<Force, Hex> delayedEnemies = new ObjectMap<Force, Hex>(force.visualEnemies);
        final ObjectSet<Hex> delayedArea = force.reconArea;
        final float time = force.playScreen.time;

        final Force copy = force.copyForce();
        long beg = 0;
        try {
            while (beg < delay) {
                Thread.sleep(100);
                if(!force.playScreen.timer.isChecked()) {
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
                    force.visualHex = delayedHex;
                    force.visualTail = delayedTail;
                    force.visualForcePath = delayedPath;
                    force.setVisualHex(force.visualHex);
                    force.visualTime = time;
                    force.playScreen.updateEnemies(delayedEnemies, delayedArea, force.visualTime);
                    force.visualizeCopy(copy);
                }
            });
        }
    }
}


