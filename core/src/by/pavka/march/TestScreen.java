package by.pavka.march;

import static by.pavka.march.configuration.Nation.FRANCE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import by.pavka.march.configuration.Configurator;
import by.pavka.march.fight.impl.Battle;
import by.pavka.march.map.Hex;

public class TestScreen extends Stage implements Screen {
    BuonaparteGame game;
    Configurator configurator;
    Hex hex = new Hex();
    Battle battle;
    int firstWins;

    public TestScreen(BuonaparteGame game, Configurator configurator) {
        super(new ExtendViewport(34, 34));
        this.game = game;
        this.configurator = configurator;
        Gdx.input.setInputProcessor(this);

        TestConfigurator.prepareStage(this, hex);
        battle = new Battle(hex, FRANCE);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        act();
        battle.addDuration(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
