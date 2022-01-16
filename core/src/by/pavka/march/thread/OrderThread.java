package by.pavka.march.thread;

import com.badlogic.gdx.Gdx;

import by.pavka.march.military.Force;
import by.pavka.march.military.Formation;
import by.pavka.march.order.Order;

public class OrderThread extends Thread {
    private Force force;
    private Order order;

    public OrderThread(Force force, Order order) {
        this.force = force;
        this.order = order;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            long delay = (long) force.findCommandDistance();
            System.out.println("Sending order to " + force + " within " + delay);
            long beg = 0;
            while (beg < delay) {
                Thread.sleep(100);
                if (!force.playScreen.timer.isChecked()) {
                    beg += 100;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Formation f = (Formation) force.superForce;
                order.execute(force);
            }
        });
    }
}


