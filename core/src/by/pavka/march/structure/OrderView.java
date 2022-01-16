package by.pavka.march.structure;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class OrderView extends Table {
    public Label orderLabel;
    public ImageButton undo;

    public OrderView(String text, Skin skin) {
        orderLabel = new Label(text, skin);
        undo = new ImageButton(skin.getDrawable("button-close-over"));
        add(orderLabel);
        add(undo);
        undo.setVisible(false);
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
