package by.pavka.march.thread;

import com.badlogic.gdx.Gdx;

import by.pavka.march.military.Force;
import by.pavka.march.order.Order;

public class OrderThread extends Thread {
    private Force force;
    private Order order;
    private long del;

    public OrderThread(Force force, Order order) {
        this.force = force;
        this.order = order;
        order.visualize(force);
        setDaemon(true);
    }

    public OrderThread(Force force, Order order, long delay) {
        this.force = force;
        this.order = order;
        this.del = delay;
        order.visualize(force);
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            long delay = (long) force.findCommandDistance() + del;
            System.out.println("Order " + order + order.hashCode() + " is sent with delay " + delay);
            long beg = 0;
            while (beg < delay / 3) {
                Thread.sleep(100);
                if (!force.playScreen.timer.isChecked()) {
                    beg += 100;
                }
            }
            if (order != null) {
                order.irrevocable = true;
            }
            while (beg < delay || !order.isExecutable(force)) {
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
                System.out.println("Order " + order.hashCode() + " is received");
                order.receive(force);
            }
        });
    }
}


