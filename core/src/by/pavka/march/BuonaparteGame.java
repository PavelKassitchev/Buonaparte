package by.pavka.march;

import com.badlogic.gdx.Game;

public class BuonaparteGame extends Game {
	
	@Override
	public void create () {
		setScreen(new MenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
	}
}
