package by.pavka.march;

import com.badlogic.gdx.input.GestureDetector;

public class HexGestureListener extends GestureDetector.GestureAdapter {
    private PlayScreen playScreen;

    public void setGestureDetector(PlayScreen playScreen) {
        this.playScreen = playScreen;
    }

    @Override
    public boolean longPress(float x, float y) {
        playScreen.longPressed = true;
        return super.longPress(x, y);
    }
}
