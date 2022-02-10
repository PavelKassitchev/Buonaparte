package by.pavka.march.structure;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import by.pavka.march.military.Force;
import by.pavka.march.order.DetachForceOrder;
import by.pavka.march.order.Order;

public class OrderView extends Table {
    public Label orderLabel;
    public ImageButton undo;
    public Force force;

    public OrderView(final Force force, final Order order, Skin skin) {
        this.force = force;
        String text = order.toString();
        orderLabel = new Label(text, skin);
        undo = new ImageButton(skin.getDrawable("button-close-over"));
        add(orderLabel);
        add(undo);

        undo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (undo.isVisible() && force.visualOrders != null &&
                        order instanceof DetachForceOrder) {
                    System.out.println("THIS IS DETACHFORCEORDER");
                    DetachForceOrder detachForceOrder = (DetachForceOrder) order;
                    if (detachForceOrder.detachOrder != null && !detachForceOrder.detachOrder.irrevocable) {
                        detachForceOrder.detachOrder.revoked = true;
                    }
                }
                if (undo.isVisible()) {
                    clear();
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
    }



    public void setText(String text) {
        orderLabel.setText(text);
        undo.setVisible(true);
    }

    public void clear() {
        orderLabel.setText("");
        undo.setVisible(false);
    }
}
