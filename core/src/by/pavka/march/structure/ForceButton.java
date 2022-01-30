package by.pavka.march.structure;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import by.pavka.march.military.Force;

public class ForceButton extends TextButton {
    public Force force;

    public ForceButton(Force force, Skin skin) {
        super(force.getName(), skin, "toggle");
//            super(name, skin, "tab");
        this.force = force;
    }

    public ForceButton(Force force, String title, Skin skin) {
        super (title, skin);
        this.force = force;
    }

    public void setForce(Force f) {
        force = f;
    }

    public Force getForce() {
        return force;
    }
}
