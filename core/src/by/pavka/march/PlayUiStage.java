package by.pavka.march;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PlayUiStage extends UiStage {
    private Table group;

    public PlayUiStage(PlayScreen1 screen) {
        super(screen);
    }

    public void init() {
        group = new Table();
        addActor(group);
        TextButton back = new TextButton("Back", screen.skin);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        group.add(back);
        TextButton zoom = new TextButton("Zoom", screen.skin);
        zoom.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (screen.camera.zoom == 1) {
                    screen.camera.zoom = 2.65f;
                } else {
                    screen.camera.zoom = 1;
                }
                screen.camera.update();
                screen.hexagonalTiledMapRenderer.setView(screen.camera);
            }
        });
        group.add(zoom);
        group.setDebug(true, true);
        group.setBounds(0, getHeight() * 0.9f, getWidth(), getHeight() * .1f);
        group.left();
    }

    @Override
    public void resize() {
        group.setBounds(0, getHeight() * 0.9f, getWidth(), getHeight() * .1f);
        group.left();
    }
}
