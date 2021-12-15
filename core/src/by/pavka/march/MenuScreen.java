package by.pavka.march;

import static by.pavka.march.configuration.Campaign.MARENGO;
import static by.pavka.march.configuration.Nation.AUSTRIA;
import static by.pavka.march.configuration.Nation.FRANCE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import by.pavka.march.configuration.Configurator;
import by.pavka.march.configuration.Nation;

public class MenuScreen implements Screen {
    private BuonaparteGame game;
    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;

    public MenuScreen(BuonaparteGame game) {
        this.game = game;
        stage = new Stage(new ExtendViewport(800, 450));
//        atlas = new TextureAtlas("skin/golden-ui-skin.atlas");
//        skin = new Skin(Gdx.files.internal("skin/golden-ui-skin.json"), atlas);
        skin = game.getSkin();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Table table = new Table();
//        table.setDebug(true);
        table.setFillParent(true);
        stage.addActor(table);
        TextButton play = new TextButton("France", skin);
        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                game.nation = FRANCE;
                game.setScreen(new PlayScreen(game, new HexGestureListener(), new Configurator(game, FRANCE, MARENGO)));
            }
        });
        table.add(play).fillX().uniformX();
        table.row().pad(8, 0, 8, 0);
        TextButton options = new TextButton("Austria", skin);
        options.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.nation = Nation.AUSTRIA;
                game.setScreen(new PlayScreen(game, new HexGestureListener(), new Configurator(game, AUSTRIA, MARENGO)));
            }
        });
        table.add(options).fillX().uniformX();
        table.row();
        TextButton exit = new TextButton("Exit", skin);
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table.add(exit).fillX().uniformX();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 1, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        skin.dispose();
        atlas.dispose();
        stage.dispose();
    }
}
