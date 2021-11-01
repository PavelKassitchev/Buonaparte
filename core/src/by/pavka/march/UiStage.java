package by.pavka.march;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public abstract class UiStage extends Stage {
    protected Game game;
    protected PlayScreen1 screen;
    protected boolean dragged;

    public UiStage(PlayScreen1 screeen) {
        super(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        this.screen = screeen;
        game = screen.game;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return !super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        if (dragged) {
            dragged = false;
            return true;
        }
        return false;
    }

    public abstract void init();

    public abstract void resize();
}
