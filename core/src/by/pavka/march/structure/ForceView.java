package by.pavka.march.structure;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import by.pavka.march.military.Force;

public class ForceView extends Table {
    public CheckBox checkBox;
    public AssignButton assignButton;
    public ForceView(Force force, Skin skin) {
        super(skin);
        checkBox = new CheckBox(force.getName(), skin);
        add(checkBox).padRight(8);
        assignButton = new AssignButton(force, skin);
        add(assignButton);
        if (force.superForce == null) {
            assignButton.setDisabled(true);
        }
    }
}
