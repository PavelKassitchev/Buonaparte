package by.pavka.march.structure;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import by.pavka.march.military.Force;
import by.pavka.march.order.DetachForceOrder;
import by.pavka.march.order.DetachOrder;
import by.pavka.march.order.Order;

public class OrderView extends Table {
    public Label orderLabel;
    public ImageButton undo;
    public Force force;
    public Order order;

    public OrderView(final Force force, final Order order, Skin skin, boolean future) {
        this.force = force;
        this.order = order;
        String text = order.toString();
        orderLabel = new Label(text, skin);
        undo = new ImageButton(skin.getDrawable("button-close-over"));
        add(orderLabel);
        add(undo);

        if (!future) {
            undo.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (undo.isVisible() && force.visualOrders != null &&
                            order instanceof DetachForceOrder) {
                        DetachForceOrder detachForceOrder = (DetachForceOrder) order;
                        if (detachForceOrder.detachOrder != null && !detachForceOrder.detachOrder.irrevocable) {
                            detachForceOrder.detachOrder.revoked = true;
                            detachForceOrder.detachOrder.detachingForce.visualOrders.removeOrder(detachForceOrder.detachOrder);
                            System.out.println("DetachOrder revoked " + detachForceOrder.detachOrder.hashCode());
                        }
                    }
                    if (undo.isVisible()) {
//                        clear();
                        remove();
                        System.out.println("CANCELLED " + order.hashCode());
                        force.visualOrders.removeOrder(order);
                        if (!order.irrevocable) {
                            order.revoked = true;
                        } else {
                            System.out.println("cancellation");
                            order.cancel(force);
                        }
                    }
                }
            });
        } else {
            undo.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
//                    force.futureOrders.removeOrder(order);
                    OrderView.this.remove();
                    order.revoked = true;
                    if (order instanceof DetachOrder) {
                        force.playScreen.destroyTreeWindow();
                    }
                }
            });
        }
    }



    public void setText(String text) {
        orderLabel.setText(text);
        undo.setVisible(true);
    }

    public void updateLabel() {
        orderLabel.setText(order.toString());
    }

    public void clear() {
        orderLabel.setText("");
        undo.setVisible(false);
    }

}
