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

    public OrderView(String text, Skin skin) {
        orderLabel = new Label(text, skin);
        undo = new ImageButton(skin.getDrawable("button-close-over"));
        add(orderLabel);
        add(undo);
        undo.setVisible(false);
    }

    public OrderView(final Force force, Skin skin) {
        this.force = force;
        String text = force.visualOrders.size() == 0 ? "" : force.visualOrders.first().toString();
        orderLabel = new Label(text, skin);
        undo = new ImageButton(skin.getDrawable("button-close-over"));
        add(orderLabel);
        add(undo);
        undo.setVisible(false);
        undo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (undo.isVisible() && force.visualOrders != null &&
                        force.visualOrders.first() instanceof DetachForceOrder) {
                    System.out.println("COMPLICATED");
                    DetachForceOrder detachForceOrder = (DetachForceOrder) force.visualOrders.first();
                    if (detachForceOrder.detachOrder != null && detachForceOrder.detachOrder.irrevocable == false) {
                        detachForceOrder.detachOrder.canceled = true;
                    }
                }
                if (undo.isVisible() && force.visualOrders.first().irrevocable == false) {
                    clear();
                    Order order = force.visualOrders.first();
                    System.out.println("ORDER:" + order);
                    force.visualOrders.removeOrder(order);
                    order.canceled = true;
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
