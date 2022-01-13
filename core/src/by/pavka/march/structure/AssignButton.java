package by.pavka.march.structure;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import by.pavka.march.military.Force;

public class AssignButton extends ImageButton {
    Force force;
    Skin skin;
    ImageButton.ImageButtonStyle assigning;
    ImageButton.ImageButtonStyle resigning;

    public AssignButton(Force force, Skin skin) {
        super(skin, "force");
        this.skin = skin;
        this.force = force;
        assigning = new ImageButton.ImageButtonStyle(skin.getDrawable("button-maximize-over"),
                skin.getDrawable("button-maximize-pressed"), skin.getDrawable("button-close"), null, null, null);
        resigning = new ImageButton.ImageButtonStyle(skin.getDrawable("button-minimize-over"),
                skin.getDrawable("button-minimize-pressed"), skin.getDrawable("button-close-over"), null, null, null);
        setStyle(resigning);
    }

    public void setResigning(boolean resign) {
        if (resign) {
            setStyle(resigning);
        } else {
            setStyle(assigning);
        }
    }

    boolean isResigning() {
        return getStyle() == resigning;
    }
}
