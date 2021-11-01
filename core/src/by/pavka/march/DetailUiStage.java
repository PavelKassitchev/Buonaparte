package by.pavka.march;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class DetailUiStage extends UiStage {
    private Table group;

    public DetailUiStage(PlayScreen1 screen) {
        super(screen);
    }

    @Override
    public void init() {
        group = new Table();
        addActor(group);
        TextButton back = new TextButton("Terrain", screen.skin);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.game.setScreen(new MenuScreen(screen.game));
            }
        });
        group.add(back);
        TextButton zoom = new TextButton("Units", screen.skin);
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
    }

    @Override
    public void resize() {
        group.setBounds(0, getHeight() * 0.9f, getWidth(), getHeight() * .1f);
        group.left();
    }
}
